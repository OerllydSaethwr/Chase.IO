package ws.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.*;
import ws.models.Opcode;
import ws.models.TokenResponseBody;
import ws.models.WebsocketPayload;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides the websocket listener implementation for the connection to the
 * Terra websocket API. Also has facilities for making the request to the
 * developer authentication endpoint in order to generate a token to be
 * sent for the IDENTIFY websocket payload.
 */
public class WebsocketClient extends WebSocketListener {
  /**
   * The Logger to use for the instances of this class.
   */
  private static final Logger logger = LoggerFactory.getLogger(WebsocketClient.class);
  /**
   * The ObjectMapper instance to use for serialising POJOs into JSON, as well as
   * for deserialising JSON payloads into Java objects.
   */
  private static final ObjectMapper objectMapper = new ObjectMapper();
  /**
   * The OkHttpClient to use for the authentication REST request, as well
   * as for initialising the websocket connection to Terra.
   */
  private static final OkHttpClient httpClient = new OkHttpClient();
  /**
   * ScheduledExecutorService to use in order to schedule the heartbeat payloads
   * to be sent at the correct frequency.
   */
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  /**
   * The URL to make a request to in order to establish the websocket connection.
   */
  private static final String wsConnectUrl = "wss://ws.tryterra.co/connect";
  /**
   * The URL to make a request to in order to fetch a token for websocket authentication.
   */
  private static final String wsAuthUrl = "https://ws.tryterra.co/auth/developer";

  /**
   * Thread-safe boolean which indicates whether our last heartbeat payload was acknowledged by
   * the Terra websocket server.
   */
  private final AtomicBoolean expectingAck = new AtomicBoolean();

  /**
   * The developer ID to use with the authentication request.
   */
  private final String devId;
  /**
   * The API Key to use when making the authentication request.
   */
  private final String apiKey;

  /**
   * The token received from the authentication request.
   */
  private String identifyToken;
  /**
   * The ScheduledFuture which encapsulates the heartbeat process.
   */
  private ScheduledFuture<?> heartbeatFuture = null;

  /**
   * @param devId the developer ID to use when fetching the auth token for the websocket connection
   * @param apiKey the API Key to use when fetching the auth token for the websocket connection
   */
  public WebsocketClient(String devId, String apiKey) {
    this.devId = devId;
    this.apiKey = apiKey;
  }

  /**
   * Implementation of Runnable that sends a heartbeat through the websocket connection
   * every time it is called.
   */
  static class HeartbeatRunnable implements Runnable {
    /**
     * The Logger to use for the instances of this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatRunnable.class);

    /**
     * The WebSocket instance that heartbeats will be sent through.
     */
    private final WebSocket socket;
    /**
     * The flag indicating whether a HEARTBEAT_ACK message was received for
     * the previous attempt to send a heartbeat through the connection.
     */
    private final AtomicBoolean expectingAck;

    /**
     * @param socket the socket that heartbeats will be sent through
     * @param expectingAck the flag indicating whether an acknowledgement was received for the previous heartbeat
     */
    public HeartbeatRunnable(WebSocket socket, AtomicBoolean expectingAck) {
      this.socket = socket;
      this.expectingAck = expectingAck;
    }

    @Override
    public void run() {
      if (expectingAck.get()) {
        logger.warn("Heartbeat window elapsed but previous ACK was not received");
      }

      expectingAck.set(true);
      sendPayloadAsJson(socket, WebsocketPayload.newHeartbeatPayload());
    }
  }

  /**
   * Provides the stored ObjectMapper instance for other classes to be able to use
   * if necessary.
   *
   * @return the stored ObjectMapper instance
   */
  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  /**
   * Makes the request for the authentication token to be used to authenticate
   * the websocket session through the IDENTIFY payload.
   *
   * @return the fetched token.
   */
  private String fetchAuthToken(){
    var request = new Request.Builder()
        .url(wsAuthUrl)
        // Add the dev ID and api key as request headers
        .header("dev-id", devId)
        .header("x-api-key", apiKey)
        // We need to specify an empty request body as a POST request
        // requires a request body
        .post(RequestBody.create("", null))
        .build();

    try {
      Response response = httpClient.newCall(request).execute();
      if (response.body() == null) {
        throw new RuntimeException("Auth request body was empty at runtime");
      }
      // Convert the response body into a TokenResponse and return the token that was fetched
      TokenResponseBody body = objectMapper.readValue(response.body().string(), TokenResponseBody.class);
      return body.getToken();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Serialise the given payload into a JSON string and send it through
   * the given websocket connection.
   *
   * @param socket the socket to send the JSON message through
   * @param payload the object to serialise into the JSON string
   */
  @SneakyThrows(JsonProcessingException.class)
  private static void sendPayloadAsJson(WebSocket socket, WebsocketPayload payload) {
    var stringPayload = objectMapper.writeValueAsString(payload);
    socket.send(stringPayload);
  }

  @Override
  public void onOpen(@NotNull WebSocket socket, @NotNull Response response) {
    logger.info("Websocket connection established");
  }

  @Override
  public void onClosed(@NotNull WebSocket socket, int code, @NotNull String reason) {
    logger.info("Websocket connection closed with code {} ({})", code, reason);
    this.stop();
  }

  @Override
  @SneakyThrows(JsonProcessingException.class)
  public void onMessage(@NotNull WebSocket socket, @NotNull String text) {
    System.out.println(text);

//    logger.debug("Raw message received: {}", text);
    // Deserialise the payload from JSON into a java Object
    WebsocketPayload parsedPayload = objectMapper.readValue(text, WebsocketPayload.class);

    if (parsedPayload.getOpcode().equals(Opcode.HELLO)) {
      // Identify
      logger.info("Sending IDENTIFY and scheduling heartbeats");
      sendPayloadAsJson(socket, WebsocketPayload.newIdentifyPayload(this.identifyToken));
      // Read the hearbeat interval from the HELLO data payload
      var heartbeatInterval = parsedPayload.getData().get("heartbeat_interval").asLong();
      heartbeatInterval /= 40;
      // Schedule the heartbeat to be sent at the given interval
      heartbeatFuture = scheduler.scheduleAtFixedRate(
          new HeartbeatRunnable(socket, expectingAck),
          Math.min((int) (new Random().nextFloat() * heartbeatInterval), (int) (0.1 * heartbeatInterval)),
          heartbeatInterval, TimeUnit.MILLISECONDS
      );
    } else if (parsedPayload.getOpcode().equals(Opcode.HEARTBEAT_ACK)) {
      // Set the ACK flag to false - we are no longer expecting a heartbeat acknowledgement
      expectingAck.set(false);
    } else if (parsedPayload.getOpcode().equals(Opcode.READY)) {
      logger.info("Connection is READY");
    } else if (parsedPayload.getOpcode().equals(Opcode.DISPATCH)) {
      // We have been sent some data from a producer connection. Do any processing here
//      logger.info("Received DISPATCH payload for user ID {} of type {}. Data is: {}",
//          parsedPayload.getUserId(), parsedPayload.getEventType(), parsedPayload.getData().toString()
//      );
      logger.info("DISPATCH!!!!!");
      JsonNode dataNode = parsedPayload.getData();
      System.out.println("sdjkfhsjkdfhjkahdfjkahsjkhadsjk");

      logger.info("1");
      //JsonNode heartDataNode = dataNode.get("heart_data");
      //logger.info("2");
      JsonNode heatRateDataNode = dataNode.get("heart_rate_data");
      logger.info("3");
      JsonNode heartRateSummaryNode = heatRateDataNode.get("summary");
      logger.info("4");
      JsonNode avgHrBpm = heartRateSummaryNode.get("avg_hr_bpm");
      logger.info("5");
      int bpm = avgHrBpm.asInt();

      logger.info("the heart rate is " + bpm);

      //map id of watch to the player

    }
  }

  /*
    "heart_data": {
        "afib_classification_samples": [],
        "pulse_wave_velocity_samples": [],
        "ecg_signal": [],
        "heart_rate_data": {
          "summary": {
            "avg_hr_bpm": null,
            "user_max_hr_bpm": null,
            "avg_hrv_rmssd": null,
            "max_hr_bpm": null,
            "resting_hr_bpm": null,
            "min_hr_bpm": null,
            "hr_zone_data": [],
            "avg_hrv_sdnn": null
          },
          "detailed": {
            "hrv_samples_sdnn": [],
            "hr_samples": [],
            "hrv_samples_rmssd": []
          }
        }
      },

   */

  /**
   * Fetches the authentication token and creates the connection
   * to the Terra websocket servers.
   */
  public void start() {
    logger.info("Fetching authentication token");
    this.identifyToken = fetchAuthToken();
    logger.info("Creating websocket connection");
    var request = new Request.Builder()
        .url(wsConnectUrl)
        .build();
    httpClient.newWebSocket(request, this);
  }

  /**
   * Cancels heartbeating if it was running, and shuts down all
   * executors and the websocket connection
   */
  public void stop() {
    if (heartbeatFuture != null) {
      heartbeatFuture.cancel(true);
    }

    httpClient.dispatcher().executorService().shutdown();
    scheduler.shutdown();
  }
}

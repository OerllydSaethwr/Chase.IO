package ws.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import ws.models.WebsocketClient;

/**
 * Implementation of the websocket payload schema that can be serialised
 * to JSON as well as deserialised from JSON
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebsocketPayload {
  /**
   * The payload's opcode
   */
  @JsonProperty("op")
  private Opcode opcode;
  /**
   * The nested data object for the JSON
   */
  @JsonProperty("d")
  private JsonNode data = null;
  /**
   * The event type for the payload
   */
  @JsonProperty("t")
  private String eventType = null;
  /**
   * The user ID that the paylod is for
   */
  @JsonProperty("uid")
  private String userId = null;
  /**
   * The sequence value of the payload
   */
  @JsonProperty("seq")
  private Long sequenceNumber = null;

  private WebsocketPayload(Opcode opcode, JsonNode data) {
    this.opcode = opcode;
    this.data = data;
  }

  /**
   * Create an instance of this class that contains the required fields for a valid
   * IDENTIFY payload.
   *
   * @param token the token to pass in the data object of the payload
   * @return the created payload object
   */
  public static WebsocketPayload newIdentifyPayload(String token) {
    var identifyData = WebsocketClient.getObjectMapper().createObjectNode();
    identifyData.put("token", token);
    identifyData.put("type", 1);

    return new WebsocketPayload(Opcode.IDENTIFY, identifyData);
  }

  /**
   * Create an instance of this class that contains the required fields
   * for a valid HEARTBEAT payload.
   *
   * @return the created payload object
   */
  public static WebsocketPayload newHeartbeatPayload() {
    return new WebsocketPayload(Opcode.HEARTBEAT, null);
  }
}

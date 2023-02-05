package ws.models;

import org.example.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrypoint for the application.
 */
public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);

  public static void websocketMain() {
    // Pull the developer ID and API Key from environment variables
    String devId = "ichack-app-dev-7YeP5Bo6KC";
    String apiKey = "8133411e6cca957f0264dd5186acb47e27f54c1ab48911d4f58e798ffdbd5455";

    // Ensure that both the developer ID and API Key were provided
    if (devId == null || apiKey == null) {
      logger.error("Could not fetch DEVELOPER_ID and API_KEY from environment variables. Aborting.");
      return;
    }
    // Create an instance of WebsocketClient and establish the websocket connection
    var client = new WebsocketClient(devId, apiKey);
    client.start();
    // Add a shutdown hook to cleanup the client on SIGINT
    Runtime.getRuntime().addShutdownHook(new ShutdownHook(client));
  }

  /**
   * Cleanup hook to ensure the websocket client is shutdown gracefully.
   */
  static class ShutdownHook extends Thread {
    private final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
    private final WebsocketClient client;

    public ShutdownHook(WebsocketClient client) {
      this.client = client;
    }

    @Override
    public void run() {
      logger.info("Shutting down");
      this.client.stop();
    }
  }
}

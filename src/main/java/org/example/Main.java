package org.example;

import java.util.ArrayList;
import org.example.core.GameEngine;
import org.example.terra.WebhookPayload;
import spark.Spark;
import com.google.gson.*;
import ws.models.App;

public class Main {

    public static void main(String[] args) {
      App.websocketMain(args);

      Gson gson = new Gson();
      Game game = new Game();

      GameEngine gameEngine = GameEngine.getInstance();
      gameEngine.setGame(game);
      gameEngine.start();

      Spark.post("/start_game", (req, res) -> {
        try {
          //System.out.println(req.body());
          Player player = gson.fromJson(req.body(), Player.class);
          game.addPlayer(player);
          return "200";
        } catch (Exception e) {
          System.out.println(e.getMessage());
          return "400";
        }
      });

      Spark.post("/update_game", (req, res) -> {
        try {
//          System.out.println(req.body());
          Player player = gson.fromJson(req.body(), Player.class);
          game.updatePlayerLocation(player);
          game.updateTimestamps(player);
          GameRespond gr = new GameRespond(
              new ArrayList<>(game.getPlayers().values()),
              game.getPickups(),
              game.getSecondsRemaining()
          );
          return gson.toJson(gr, GameRespond.class);
        } catch (Exception e) {
          e.printStackTrace();
          return "400";
        }
      });

      Spark.post("terra_webhook", (req, res) -> {
        try {
          gson.fromJson(req.body(), WebhookPayload.class);
          System.out.println(req.body());
          return "200";
        } catch (Exception e) {
          e.printStackTrace();
          return "400";
        }
      });
    }
}
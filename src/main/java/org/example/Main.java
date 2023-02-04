package org.example;

import java.util.ArrayList;
import java.util.List;
import spark.Spark;
import spark.Spark.*;
import com.google.gson.*;
import ws.models.App;


public class Main {
    public static void main(String[] args) {

      App.websocketMain(args);

      Gson gson = new Gson();
      Game game = new Game();
      Spark.post("/start_game", (req, res) -> {
        try {
          System.out.println(req.body());
          Player player = gson.fromJson(req.body(), Player.class);
          game.addPlayer(player);
          game.generatePickups(10);
          return "200";
        } catch (Exception e) {
          System.out.println(e.getMessage());
          return "400";
        }
      });

      Spark.post("/update_game", (req, res) -> {
        try {
          System.out.println(req.body());
          Player player = gson.fromJson(req.body(), Player.class);
          game.getPlayers().get(player.getName()).updateLocation(player.getCoord().latitude, player.getCoord().longitude);
          GameRespond gr = new GameRespond(new ArrayList(game.getPlayers().values()), game.getPickups());
          String ret = gson.toJson(gr, GameRespond.class);
          return ret;
        } catch (Exception e) {
          return "400";
        }
      });
    }
}
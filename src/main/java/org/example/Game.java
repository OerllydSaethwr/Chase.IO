package org.example;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.PriorityQueue;
import java.util.Random;

public class Game {

  private final List<Pickup> pickups;
  //private final List<Player> players;
  private Map<String, Player> players;
  private Random random;

  private double secondsRemaining;

  private static int TICK_RATE = 10;

  public Game () {
    this.pickups = new ArrayList<>();
    this.players = new HashMap<>();
    this.random = new Random();
  }

  public Map<String, Player> getPlayers() {
    return players;
  }

  public List<Pickup> getPickups() {
    return pickups;
  }

  public void addPlayer(Player p) {
    players.put(p.getName(), p);
  }

  public void generatePickups(int num) {
    for (int i = 1; i <= num; i++) {
        generatePickup();
      }
    }


  public void generatePickup(){
    int point = 100;
    double max = 10.0;
    double radius;
    double latitude;
    double longitude;
    point = random.nextInt(point);
    radius = random.nextDouble() * max;
    latitude = random.nextDouble() * max;
    longitude = random.nextDouble() * max;
    pickups.add(new Pickup(point, radius, new Location(latitude, longitude)));
  }

  public List<Player> withinRange(Player player) {
    List<Player> retValue = new ArrayList<Player>();
    for (Player p : players.values()) {
      if (player.coord.distanceTo(p.coord) <= player.getRadius()) {
        retValue.add(p);
      }
    }
    return retValue;
  }

  public void duel() {
    for (Player player1 : players.values()) {
      for (Player player2 : players.values()) {
        if (player1 == player2) {
          continue;
        } else if (player1.intersects(player2)) {
          player1.duel(player2, TICK_RATE);
        }
      }
    }
  }

  public void tick() {
    for (Player player : players.values()) {
      //player.updateLocation(0.0,0.0);
      player.claimPickups(pickups, this);
    }

    duel();
    updateTime();
  }

  public void destroyPickup(Pickup pickup) {
    pickups.remove(pickup);
  }

  public void updateTime() {
    secondsRemaining -= 1 / (double)TICK_RATE;
  }


}

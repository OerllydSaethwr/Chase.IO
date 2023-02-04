package org.example;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.PriorityQueue;
import java.util.Random;

public class Game {

  private final List<Pickup> pickups;
  private Map<String, Player> players;
  private Random random;

  private double secondsRemaining;

  private static int TICK_RATE = 10;

  private final Zone zone;

  private static final int MAX_PICKUPS = 10;

  public Game () {
    this.pickups = new ArrayList<>();
    this.players = new HashMap<>();
    this.random = new Random();
    this.zone = new Zone();
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

  public void generatePickups() {
    while (pickups.size() < MAX_PICKUPS) {
      generatePickup();
    }
  }


  public void generatePickup(){
    int point = 100;
    double max = 10.0;
    double radius;
    Location pickupLocation = zone.getRandomLocation();
    point = random.nextInt(point);
    radius = random.nextDouble() * max;
    pickups.add(new Pickup(point, radius, pickupLocation));
  }

  public List<Player> withinRange(Player player) {
    List<Player> retValue = new ArrayList<Player>();
    for (Player p : players.values()) {
      if (player.getCoord().distanceTo(p.getCoord()) <= player.getRadius()) {
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


  public void updateTimestamps(Player player) {

      if (!player.updateTimestamp()) {
        players.remove(player.getName());
      }
  }



  public void tick() {

    for (Player player : players.values()) {
      //player.updateLocation(0.0,0.0);
      player.claimPickups(pickups, this);

    }

    if (players.size() > 0) {
      zone.update(players);
      generatePickups();
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

  public void updatePlayerLocation(Player player) {
    players.get(player.getName()).updateLocation(player.getCoord().latitude, player.getCoord().longitude);
  }

}

package org.example;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Game {

  private final List<Pickup> pickups;
  private final List<Player> players;
  private Random random;

  private double secondsRemaining;

  private static int TICK_RATE = 10;

  public Game () {
    this.pickups = new ArrayList<>();
    this.players = new ArrayList<>();
  }

  public void addPlayer(Player p) {
    players.add(p);
  }

  public void generatePickups(int num) {
    int point = 100;
    double max = 10.0;
    double radius = 10.0;
    double latitude = 10.0;
    double longitude = 10.0;
    for (int i = 1; i <= num; i++) {
      point = random.nextInt(point);
      radius = random.nextDouble() * max;
      latitude = random.nextDouble() * max;
      longitude = random.nextDouble() * max;
      pickups.add(new Pickup(point, radius, new Location(latitude, longitude)));
    }
  }

  public List<Player> withinRange(Player player) {
    List<Player> retValue = new ArrayList<Player>();
    for (Player p : players) {
      if (player.coord.distanceTo(p.coord) <= player.getRadius()) {
        retValue.add(p);
      }
    }
    return retValue;
  }

  public void duel() {
    for (Player player1 : players) {
      for (Player player2 : players) {
        if (player1 == player2) {
          continue;
        }

        
      }
    }
  }

  public void tick() {
    for (Player player : players) {
      player.updateLocation();
      player.claimPickups(pickups);
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

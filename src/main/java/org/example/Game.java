package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.Random;
import org.example.terra.GameMode;
import org.slf4j.LoggerFactory;

public class Game {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Game.class);

  private static final Game game = new Game();

  private final List<Pickup> pickups;
  private final List<Pickup> pickupsToDestroy;
  private final Map<String, Player> players;
  private int numTeam1 = 0;
  private int numTeam2 = 0;
  private final Random random;
  private GameMode mode = GameMode.SOLO;

  private double secondsRemaining;
  private static final double DEFAULT_GAME_LENGTH = 60;

  private static final int TICK_RATE = 1;

  private final Zone zone;

  private static final int MAX_PICKUPS = 20;

  private long tick_id = 0;

  public static Game getInstance() {
    return game;
  }

  private Game() {
    this(DEFAULT_GAME_LENGTH);
  }

  private Game(double secondsRemaining) {
    this.pickups = new ArrayList<>();
    this.pickupsToDestroy = new ArrayList<>();
    this.players = new HashMap<>();
    this.random = new Random();
    this.zone = new Zone();
    this.secondsRemaining = secondsRemaining;
  }

  public void setGame(GameMode mode) {
    this.mode = mode;
  }

  public void setHeartRate(String id, int bpm) {
    Player player = players.get(id);
    if (player != null) {
      player.setHeartRate(bpm);
    }
  }

  public Map<String, Player> getPlayers() {
    return players;
  }

  public List<Pickup> getPickups() {
    return pickups;
  }

  public void addPlayer(Player p) {
    p.absorb(10);

    players.put(p.getUuid(), p);
    if (mode == GameMode.TEAMS) {
      if (numTeam1 > numTeam2) {
        numTeam2++;
        p.setTeam(true);
      } else {
        numTeam1++;
        p.setTeam(false);
      }
    }
  }

  // set colours to corresponding teams
  // take input to the game mode
  // display the heartbeat data for each person

  public void generatePickups() {
    while (pickups.size() < MAX_PICKUPS) {
      generatePickup();
    }
  }

  public double getSecondsRemaining() {
    return secondsRemaining;
  }

  public void generatePickup(){
    int point = 50;
    double max = 10.0;
    double radius;
    Location pickupLocation = zone.getRandomLocation();
    point = random.nextInt(point);
    radius = random.nextDouble() * max;
    pickups.add(new Pickup(point, radius, pickupLocation));
  }

  public List<Player> withinRange(Player player) {
    List<Player> retValue = new ArrayList<>();
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
        } else if (player1.intersects(player2) && (player1.getTeam() == null || player1.getTeam() != player2.getTeam())) {
          player1.duel(player2, TICK_RATE);
        }
      }
    }
  }

  public void willDestroy(Pickup pickup) {
    pickupsToDestroy.add(pickup);
  }

  public void destroyClaimedPickups() {
    for (Pickup pickup : pickupsToDestroy) {
      pickups.remove(pickup);
    }
    pickupsToDestroy.clear();
  }


  public synchronized void updateTimestamps(Player player) {

      if (!player.updateTimestamp()) {
        players.remove(player.getName());
      }
  }

  public synchronized void tick() {

    logger.info("tick " + tick_id++);

    try {
      performGameUpdates();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void performGameUpdates() {
    for (Player player : players.values()) {
      //player.updateLocation(0.0,0.0);
      player.claimPickups(pickups, this);
      player.updatePoints();
      player.passiveLoss();
    }

    int minPlayers = 2;

    if (players.size() >= minPlayers) {
      zone.update(players);
      generatePickups();
    }

    duel();
    destroyClaimedPickups();
    updateTime();
    //mockHeartRates();
  }

  public void mockHeartRate(String id) {
    Player player = players.get(id);
    if (player == null || player.getHeartRate() > 0) {
      return;
    }
    int bpm = player.getHeartRate();
    int nextBpm = Math.max(60, bpm + random.nextInt(4) - 2);
    setHeartRate(id, nextBpm);
  }

  private void mockHeartRates() {
    for (Player player : players.values()) {
      String id = player.getUuid();
      mockHeartRate(id);
    }
  }

  public synchronized void updateTime() {
    secondsRemaining -= 1 / (double)TICK_RATE;
  }

  public synchronized void updatePlayerLocation(Player player) {
    players.get(player.getUuid()).updateLocation(player.getCoord().latitude, player.getCoord().longitude);
  }
}
;
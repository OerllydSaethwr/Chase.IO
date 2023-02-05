package org.example;


import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Player {
  @SerializedName("username")
  private String name;
  private double radius;
  private int points;
  private static final double INIT_SIZE = 20;
  private static final double MIN_HEARTRATE = 100;
  private int heartRate;
  @SerializedName("coordinate")
  Location coord;
  private long timestamp;

  private String uuid;

  private FitnessData fitnessData;

  public Player(String name) {
    this.uuid = UUID.randomUUID().toString();
    this.radius = INIT_SIZE;
    this.points = 0;
    this.name = name;

    this.timestamp = 0;
  }

  public synchronized void claimPickups(List<Pickup> pickups, Game game) {
    for (Pickup pickup : pickups) {
      if (coord.distanceTo(pickup.getCoord()) < radius) {
        claimPickup(pickup, game);
      }
    }
  }

  private void claimPickup(Pickup pickup, Game game) {
    absorb(pickup.getPoints());
    System.out.println("points is " + points);
    game.willDestroy(pickup);
  }

  public void absorb(int points) {
    this.points += points;
    updateRadius();
  }
  private void updateRadius() {
    radius = points / (double) 10 + INIT_SIZE;
  }

  public boolean updateTimestamp() {
    long ts = new Date().getTime();
    if (timestamp == 0 || ts - timestamp < 20000) {
      this.timestamp = ts;
      return true;
    }
    return false;
  }

  public double getRadius() {
    return radius;
  }


  public double getPoints() {
    return points;
  }

  public String getName() {
    return name;
  }

  public long getTimstamp() {return timestamp;}

  public void takeDamage(double points) {
    this.points -= points;
    if (points < 0) {
      this.points = 0;
    }
    updateRadius();
  }

  public void passiveLoss() {
//    if (apiData.getHeartrate() < MIN_HEARTRATE) {
//      points *=  0.99;
//    }
  }
  public Location getCoord() {
    return coord;
  }


  public boolean intersects(Player player2) {
    return coord.distanceTo(player2.getCoord()) - radius < 0;
  }

  public void duel (Player player2, int tickRate) {
    if (points > player2.getPoints()) {
      int absorbAmount = player2.getPoints() == 0 ? 0 : Math.max(1, (int) (player2.getPoints()/ 10) / tickRate);
      this.absorb(absorbAmount);
      player2.takeDamage(absorbAmount);
    }
  }

  public void setHeartRate(int heartRate) {
    this.heartRate = heartRate;
  }

  public void updateLocation(double latitude, double longitude) {
    coord.updateLocation(latitude, longitude);
  }

  // on each tick, update loction, passive update, check if inside someone, check if

}

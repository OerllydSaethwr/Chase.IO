package org.example;


import com.google.gson.annotations.SerializedName;
import java.util.List;


public class Player {
  @SerializedName("username")
  private String name;
  private double radius;
  private double points;
  private static double INIT_SIZE = 1;
  private static double POINT_THRESHHOLD = 50;
  private static double MIN_HEARTRATE = 100;
  @SerializedName("location")
  Location coord;

  public Player(String name) {
    this.radius = INIT_SIZE;
    this.points = 0;
    this.name = name;
  }

  public void claimPickups(List<Pickup> pickups, Game game) {
    for (Pickup pickup : pickups) {
      if (coord.distanceTo(pickup.getCoord()) < radius) {
        claimPickup(pickup, game);
      }
    }
  }

  private void claimPickup(Pickup pickup, Game game) {
    absorb(pickup.getPoints());
    pickup.destroy();
    game.generatePickup();
  }

  private void absorb(double points) {
    this.points += points;
    updateRadius();
  }
  private void updateRadius() {
    radius += points / (double) 10 + INIT_SIZE;
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

  public void takeDamage(double points) {
    points -= points;
    updateRadius();
  }

  public void passiveLoss(int tickRate) {
//    if (points < POINT_THRESHHOLD && apiData.getHeartrate() < MIN_HEARTRATE) {
//      points -= 1/tickRate;
//    }
  }
  public Location getCoord() {
    return coord;
  }


  public boolean intersects(Player player2) {
    return coord.distanceTo(player2.getCoord()) - radius < 0;
  }

  public void duel (Player player2, double tickRate) {
    if (points > player2.getPoints()) {
      double absorbAmount = radius / tickRate;
      this.absorb(absorbAmount);
      player2.takeDamage(absorbAmount);
    }
  }

  public void updateLocation(double latitude, double longitude) {
    coord.updateLocation(latitude, longitude);
  }

  // on each tick, update loction, passive update, check if inside someone, check if

}

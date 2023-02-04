package org.example;


import java.util.List;

public class Player {
  private String name;
  private double radius;
  private double points;
  private static double INIT_SIZE = 1;
  private static double POINT_THRESHHOLD = 50;
  private static double MIN_HEARTRATE = 100;
  Location coord;

  public Player(String name) {
    this.radius = INIT_SIZE;
    this.points = 0;
    this.name = name;
  }

  public void claimPickups(List<Pickup> pickups) {
    for (Pickup pickup : pickups) {
      if (coord.distanceTo(pickup.getCoord()) < radius) {
        claimPickup(pickup);
      }
    }
  }

  private void claimPickup(Pickup pickup) {
    absorb(pickup.getPoints());
    pickup.destroy();
  }

  private void absorb(int points) {
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

  public void takeDamage(int points) {
    points -= points;
    updateRadius();
  }

  public void passiveLoss(int tickRate) {
//    if (points < POINT_THRESHHOLD && apiData.getHeartrate() < MIN_HEARTRATE) {
//      points -= 1/tickRate;
//    }
  }

  public void updateLocation() {
//    int latitude = gps.getLatitude();
//    int longitude = gps.getLongitude();
//    coords.updateLocation(latitude, longitude);
  }

  // on each tick, update loction, passive update, check if inside someone, check if

}

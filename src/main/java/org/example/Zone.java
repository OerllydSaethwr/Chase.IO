package org.example;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class Zone {

  private double radius;
  private Location centre;

  private Random random;

  public Zone() {
    this.random = new Random();
  }

  public double getRadius() {
    return radius;
  }

  public Location getCentre() {
    return centre;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public void setCentre(Location centre) {
    this.centre = centre;
  }


  private void updateCentre(Map<String, Player> players) {
    double latitude = 0;
    double longitude = 0;
    for (Player p : players.values()) {
      Location coord = p.getCoord();
      latitude += coord.latitude;
      longitude += coord.longitude;
    }
    latitude /= players.size();
    longitude /= players.size();

    centre = new Location(latitude, longitude);
  }

  private void updateRadius(Location centre, Map<String, Player> players) {
    double radius = 0;
    for (Player p : players.values()) {
      radius = Math.max(radius, centre.distanceTo(p.getCoord()));
    }

    this.radius = radius;
  }

  public void update(Map<String, Player> players) {
    updateCentre(players);
    updateRadius(centre, players);
  }

  public Location getRandomLocation() {
    double randomRadius = radius * Math.sqrt(random.nextDouble());
    double theta = random.nextDouble() * 2 * PI;

    double latitude = centre.latitude + randomRadius * sin(theta);
    double longitude = centre.longitude + randomRadius * cos(theta);

    return new Location(latitude, longitude);
  }
}

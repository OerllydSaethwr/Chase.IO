package org.example;

public class Pickup {
    private final int points;
    private final double radius;
    private Location coord;

    public Pickup (int points, double radius, Location coord) {
        this.points = points;
        this.radius = radius;
        this.coord = coord;
    }

    public int getPoints() {
        return points;
    }

    public double getRadius() {
        return radius;
    }

    public Location getCoord() {
        return coord;
    }

    public void destroy() {

    }
}

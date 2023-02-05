package org.example;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class Pickup {
    private final int points;
    private final double radius;
    @SerializedName("coordinate")
    private Location coord;

    private String uuid;

    public Pickup (int points, double radius, Location coord) {
        this.points = points;
        this.radius = radius;
        this.coord = coord;
        this.uuid = UUID.randomUUID().toString();
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
}

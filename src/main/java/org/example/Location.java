package org.example;

public class Location {
  public double longitude;
  public double latitude;

  // create and initialize a point with given name and
  // (latitude, longitude) specified in degrees
  public Location(double latitude, double longitude) {
    updateLocation(latitude, longitude);
  }

  public void updateLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public double calculateDistanceInMeters(double lat1, double long1, double lat2,
      double long2) {


    double dist = org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
    return dist;
  }

  // return distance between this location and that location
  // measured in meters
  public double distanceTo(Location that) {
    // if (this.latitude == that.latitude && this.longitude == that.longitude) {
    //   return 0;
    // }
    // double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    // double lat1 = Math.toRadians(this.latitude);
    // double lon1 = Math.toRadians(this.longitude);
    // double lat2 = Math.toRadians(that.latitude);
    // double lon2 = Math.toRadians(that.longitude);

    // // great circle distance in radians, using law of cosines formula
    // double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
    //     + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

    // // each degree on a great circle of Earth is 60 nautical miles
    // double nauticalMiles = 60 * Math.toDegrees(angle);
    //  return nauticalMiles / 1852;
    return calculateDistanceInMeters(this.latitude, this.longitude, that.latitude, that.longitude);
  }
  public static Location computeOffset(Location from, double distance, double heading) {
    distance /= 6371009.0D;  //earth_radius = 6371009 # in meters
    heading = Math.toRadians(heading);
    double fromLat = Math.toRadians(from.latitude);
    double fromLng = Math.toRadians(from.longitude);
    double cosDistance = Math.cos(distance);
    double sinDistance = Math.sin(distance);
    double sinFromLat = Math.sin(fromLat);
    double cosFromLat = Math.cos(fromLat);
    double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * Math.cos(heading);
    double dLng = Math.atan2(sinDistance * cosFromLat * Math.sin(heading), cosDistance - sinFromLat * sinLat);
    return new Location(Math.toDegrees(Math.asin(sinLat)), Math.toDegrees(fromLng + dLng));
  }
}

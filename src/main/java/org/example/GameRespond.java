package org.example;

import java.util.*;
import java.util.Map;

public class GameRespond {
  private final List<Pickup> pickups;
  private final List<Player> players;
  private final double secondsRemaining;

  public GameRespond(List<Player> players, List<Pickup> pickups, double secondsRemaining) {
    this.pickups = pickups;
    this.players = players;
    this.secondsRemaining = secondsRemaining;
  }

}

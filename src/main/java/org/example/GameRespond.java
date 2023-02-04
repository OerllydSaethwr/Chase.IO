package org.example;

import java.util.*;
import java.util.Map;

public class GameRespond {
  private final List<Pickup> pickups;
  private List<Player> players;

  public GameRespond(List<Player> players, List<Pickup> pickups) {
    this.pickups = pickups;
    this.players = players;
  }

}

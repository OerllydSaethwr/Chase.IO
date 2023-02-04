package org.example.core;

import org.example.Game;

public class GameEngine extends Thread {

  private static final long TICK_INTERVAL_MS = 1000;
  private static final GameEngine gameEngine = new GameEngine();
  private GameState state = GameState.READY;
  private Game game;

  private GameEngine() {}

  public void setGame(Game game) {
    this.game = game;
  }

  public static GameEngine getInstance() {
    return gameEngine;
  }

  public void stopGame() {
    this.state = GameState.READY;
  }

  @Override
  public void run() {
    if (state == GameState.READY) {
      runTick();
    } else {
      throw new RuntimeException("Engine already running");
    }
  }

  private synchronized void runTick() {
    while (state == GameState.RUNNING) {
      try {
        game.tick();
        Thread.sleep(TICK_INTERVAL_MS);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }
}

package org.example.core;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.example.Game;
import org.slf4j.LoggerFactory;

public class GameEngine extends Thread {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GameEngine.class);

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
    logger.info("[OK] Game engine started");
    if (state == GameState.READY) {
      state = GameState.RUNNING;
      runTick();
    } else {
      logger.warn("game is already ticking");
    }
  }

  private synchronized void runTick() {
    while (state == GameState.RUNNING) {
      try {
        game.tick();
        Thread.sleep(TICK_INTERVAL_MS);
      } catch (Exception e) {
        // We should never get here. Burn everything if we do.
        e.printStackTrace();
        System.exit(2);
      }
    }
  }
}

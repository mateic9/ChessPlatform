package org.example.chess_game_logic.timer;

import org.example.websocket.WebSocketController;

public class PlayerTimer implements Runnable {

    private final Long playerId;
    private final WebSocketController webSocketController;
    private final Runnable onTimeout;

    private int secondsLeft;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private final int refreshRateSeconds = 3;
    private Thread thread;

    public PlayerTimer(Long playerId, int initialSeconds, WebSocketController webSocketController, Runnable onTimeout) {
        this.playerId = playerId;
        this.secondsLeft = initialSeconds;
        this.webSocketController = webSocketController;
        this.onTimeout = onTimeout;
    }

    public void start() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(this);
            running = true;
            paused = false;
            thread.start();
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        if (paused) {
            paused = false;
            synchronized (this) {
                notify();
            }
        }
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    @Override
    public void run() {
        try {
            while (running && secondsLeft > 0) {
                synchronized (this) {
                    while (paused) {
                        wait();
                    }
                }

                Thread.sleep(1000);
                secondsLeft--;

                if (secondsLeft % refreshRateSeconds == 0 || secondsLeft <= refreshRateSeconds) {
                    webSocketController.sendTime(playerId, secondsLeft);
                }
            }

            if (secondsLeft <= 0 && running) {
                onTimeout.run();  // ← this is the new handler
            }

        } catch (InterruptedException ignored) {}
    }
}

package org.example.chess_game_logic.timer;

import lombok.Getter;
import org.example.websocket.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TimerService {

    @Autowired
    @Getter
    private WebSocketController webSocketController;

    private final Map<Long, PlayerTimer> timers = new ConcurrentHashMap<Long, PlayerTimer>();

    public void startTimer(Long playerId, int initialSeconds, Runnable onTimeout) {
        stopTimer(playerId);
        PlayerTimer timer = new PlayerTimer(playerId, initialSeconds, webSocketController, onTimeout);
        timers.put(playerId, timer);
        timer.start();
    }

    public void pauseTimer(Long playerId) {
        PlayerTimer timer = timers.get(playerId);
        if (timer != null) {
            timer.pause();
        }
    }

    public void resumeTimer(Long playerId) {
        PlayerTimer timer = timers.get(playerId);
        if (timer != null) {
            timer.resume();
        }
    }

    public void stopTimer(Long playerId) {
        PlayerTimer timer = timers.remove(playerId);
        if (timer != null) {
            timer.stop();
        }
    }

    public Integer getTimeLeft(Long playerId) {
        PlayerTimer timer = timers.get(playerId);
        return (timer != null) ? timer.getSecondsLeft() : null;
    }
}

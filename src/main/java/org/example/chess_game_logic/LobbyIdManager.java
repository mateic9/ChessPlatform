package org.example.chess_game_logic;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LobbyIdManager {
    private static final int MAXIMUM_NR_OF_GAMES = 100;
    private final Map<Long, Boolean> bookedIdMap = new ConcurrentHashMap<>();

    public Long getId() {
        for (long i = 1; i <= MAXIMUM_NR_OF_GAMES; i++) {
            // Atomically put if absent
            if (bookedIdMap.putIfAbsent(i, true) == null) {
                return i;
            }
        }
        // All IDs are booked
        return null;
    }

    public void freeId(long id) {
        bookedIdMap.remove(id);
    }
}


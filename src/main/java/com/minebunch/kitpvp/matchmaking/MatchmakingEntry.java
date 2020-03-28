package com.minebunch.kitpvp.matchmaking;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MatchmakingEntry {
    private final UUID playerId;
    private final String kitName;
    private final boolean ranked;
    private final int elo;
}

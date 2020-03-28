package com.minebunch.kitpvp.leaderboard;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LeaderboardEntry {
    private final String name;
    private final String displayName;
    private final UUID id;
    private final long amount;
}

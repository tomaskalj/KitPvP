package com.minebunch.kitpvp.leaderboard;

import java.util.Comparator;
import lombok.Getter;

public class EntryComparator implements Comparator<LeaderboardEntry> {
    @Getter
    private static final EntryComparator instance = new EntryComparator();

    @Override
    public int compare(LeaderboardEntry a, LeaderboardEntry b) {
        return -Long.compare(a.getAmount(), b.getAmount());
    }
}

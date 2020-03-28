package com.minebunch.kitpvp.managers;

import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.leaderboard.Leaderboard;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardManager {
    private final Map<Class<? extends Kit>, Leaderboard> leaderboards = new HashMap<>();

    public LeaderboardManager(KitPlugin plugin) {
        plugin.getKitManager().getKitsByType(KitType.MATCHMAKING).values().forEach(kit ->
                leaderboards.put(kit.getClass(), new Leaderboard(plugin, kit.getClass())));
    }

    public void updateLeaderboards() {
        for (Leaderboard leaderboard : leaderboards.values()) {
            leaderboard.update();
        }
    }

    public Leaderboard getLeaderboardByKit(Class<? extends Kit> stat) {
        return leaderboards.get(stat);
    }
}

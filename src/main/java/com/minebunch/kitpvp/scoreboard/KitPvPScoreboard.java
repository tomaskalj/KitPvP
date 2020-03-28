package com.minebunch.kitpvp.scoreboard;

import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.scoreboard.ScoreboardAdapter;
import com.minebunch.core.utils.scoreboard.ScoreboardUpdateEvent;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.matchmaking.MatchmakingEntry;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import com.minebunch.kitpvp.player.PlayerStats;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class KitPvPScoreboard implements ScoreboardAdapter {
    private final KitPlugin plugin;

    @Override
    public void onScoreboardUpdate(ScoreboardUpdateEvent event) {
        Player player = event.getPlayer();
        KitPlayer data = plugin.getPlayerManager().getPlayer(player);

        // data could be null because this is called async
        if (data == null || data.getState() == PlayerState.MATCH) {
            return;
        }

        event.setTitle(Colors.SECONDARY + Colors.B + "KITPVP");
        event.setSeparator(Colors.PRIMARY + Colors.S + StringUtils.repeat("-", 23));

        switch (data.getState()) {
            case SPAWN:
            case FFA:
                PlayerStats stats = data.getStats();

                event.addLine(Colors.PRIMARY + "Kills: " + Colors.SECONDARY + stats.getKills());
                event.addLine(Colors.PRIMARY + "Deaths: " + Colors.SECONDARY + stats.getDeaths());
                event.addLine(Colors.PRIMARY + "Credits: " + Colors.SECONDARY + stats.getCredits());
                event.addLine(Colors.PRIMARY + "Kill Streak: " + Colors.SECONDARY + stats.getKillStreak());
                event.addLine(Colors.PRIMARY + "KDR: " + Colors.SECONDARY + stats.getKillDeathRatio());
                break;
            case MATCHMAKING:
                MatchmakingEntry entry = plugin.getMatchmakingManager().getEntry(player);
                event.addLine(Colors.PRIMARY + "In Matchmaking For: " + Colors.SECONDARY + entry.getKitName());
                break;
        }

        event.addLine("");
        event.addLine(Colors.SECONDARY + "kit.minebunch.com");
    }
}

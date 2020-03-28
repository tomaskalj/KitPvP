package com.minebunch.kitpvp.managers;

import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.items.ItemHotbar;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitStat;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.match.Match;
import com.minebunch.kitpvp.matchmaking.MatchmakingEntry;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import com.minebunch.kitpvp.util.MathUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@RequiredArgsConstructor
public class MatchmakingManager {
    private final Map<UUID, MatchmakingEntry> entries = new HashMap<>();
    private final Map<UUID, BukkitTask> runningQueueSearches = new HashMap<>();
    private final KitPlugin plugin;

    public void addToMatchmaking(Player player, Kit kit, boolean ranked) {
        if (entries.containsKey(player.getUniqueId())) {
            player.sendMessage(Colors.RED + "You have already joined matchmaking.");
            return;
        }

        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        int elo = kp.getStats().getStatsByKit(kit.getClass()).getAmountByStat(KitStat.ELO);

        MatchmakingEntry entry = new MatchmakingEntry(player.getUniqueId(), kit.getName(), ranked, ranked ? elo : 0);

        if (searchQueueForOpponent(entry, 50)) {
            return;
        }

        entries.put(player.getUniqueId(), entry);

        kp.setState(PlayerState.MATCHMAKING);

        ItemHotbar.QUEUE_ITEMS.apply(player);

        player.sendMessage(Colors.GREEN + "You have joined matchmaking for " + Colors.GOLD + kit.getName() + Colors.GREEN + "!");

        if (entry.isRanked()) {
            BukkitTask task = new BukkitRunnable() {
                private static final int ELO_SEARCH_RANGE = 100;
                private int currentEloRange = ELO_SEARCH_RANGE;
                private int secondsElapsed;

                @Override
                public void run() {
                    if (!entries.containsKey(entry.getPlayerId())) {
                        runningQueueSearches.remove(player.getUniqueId(), this);
                        cancel();
                        return;
                    }

                    if (!player.isOnline()) {
                        return;
                    }

                    if (currentEloRange > 500) {
                        player.sendMessage(Colors.RED + "Could not find a suitable opponent.");
                        return;
                    }

                    if (searchQueueForOpponent(entry, currentEloRange)) {
                        return;
                    }

                    if (secondsElapsed % 5 == 0) {
                        int minRange = elo - currentEloRange;
                        int maxRange = elo + currentEloRange;
                        player.sendMessage(Colors.GREEN + "Searching in ELO range " + Colors.SECONDARY + minRange + " - " + maxRange + Colors.GREEN + "...");
                    }

                    secondsElapsed++;
                    currentEloRange += 10;
                }
            }.runTaskTimer(plugin, 20L, 20L);

            runningQueueSearches.put(player.getUniqueId(), task);
        }
    }

    public MatchmakingEntry getEntry(Player player) {
        return entries.get(player.getUniqueId());
    }

    public void removeFromMatchmaking(Player player) {
        entries.remove(player.getUniqueId());

        plugin.getPlayerManager().resetPlayer(player, false);

        player.sendMessage(Colors.RED + "You have left matchmaking.");
    }

    private boolean searchQueueForOpponent(MatchmakingEntry entry, int range) {
        MatchmakingEntry opponent = entries.values().stream()
                .filter(other -> other.isRanked() == entry.isRanked()
                        && other.getKitName().equals(entry.getKitName())
                        && !other.getPlayerId().equals(entry.getPlayerId())
                        && MathUtil.isWithin(other.getElo(), entry.getElo(), range)
                )
                .findAny()
                .orElse(null);

        if (opponent == null) {
            return false;
        }

        entries.remove(opponent.getPlayerId());
        entries.remove(entry.getPlayerId());

        Kit kit = plugin.getKitManager().getKitByName(KitType.MATCHMAKING, entry.getKitName());

        Player searcherPlayer = Bukkit.getPlayer(entry.getPlayerId());
        Player opponentPlayer = Bukkit.getPlayer(opponent.getPlayerId());

        Match match = new Match(kit.getClass(), searcherPlayer, opponentPlayer);

        match.setRanked(entry.isRanked());

        match.broadcast(Colors.GREEN + "Starting match between " + searcherPlayer.getDisplayName() + Colors.GREEN + " and "
                + opponentPlayer.getDisplayName() + Colors.GREEN + " with kit " + Colors.GOLD + entry.getKitName() + Colors.GREEN + ".");

        match.start();
        return true;
    }
}

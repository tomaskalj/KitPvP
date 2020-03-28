package com.minebunch.kitpvp.match;

import com.minebunch.core.utils.message.ClickableMessage;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.arena.Arena;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitStat;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.match.inventory.InventorySnapshot;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Match extends BukkitRunnable {
    private static final int COUNTDOWN_SECONDS = 5;
    private static final int RETURN_TO_SPAWN_SECONDS = 3;
    private final Map<UUID, Boolean> playersAlive = new HashMap<>();
    private final Map<UUID, InventorySnapshot> cachedInventories = new HashMap<>();
    private final Set<UUID> disconnectedPlayers = new HashSet<>();
    private final Class<? extends Kit> kitClass;
    private final Arena arena;
    @Getter
    private final UUID id = UUID.randomUUID();
    @Getter
    private MatchState state = MatchState.STARTING;
    private int currentTick;
    private boolean ranked = false;

    public Match(Class<? extends Kit> kitClass, Player first, Player second) {
        this.arena = KitPlugin.getInstance().getArenaManager().getRandomArena();
        this.kitClass = kitClass;

        for (Player player : new Player[]{first, second}) {
            playersAlive.put(player.getUniqueId(), true);
        }
    }

    public void setRanked(boolean ranked) {
        this.ranked = ranked;
    }

    public boolean start() {
        if (arena == null) {
            broadcast(Colors.RED + "The match didn't start because there weren't enough arenas.");
            return false;
        }

        KitPlugin.getInstance().getMatchManager().addMatch(this);

        runTaskTimer(KitPlugin.getInstance(), 20L, 20L);

        boolean first = true;

        for (Player player : fighters().collect(Collectors.toList())) {
            KitPlayer kp = KitPlugin.getInstance().getPlayerManager().getPlayer(player);

            kp.setCurrentMatchId(id);
            kp.setState(PlayerState.MATCH);

            Kit kit = KitPlugin.getInstance().getKitManager().getKitByClass(KitType.MATCHMAKING, kitClass);
            kit.apply(player);

            player.teleport(first ? arena.getFirstSpawn() : arena.getSecondSpawn());

            first = false;

            Bukkit.getOnlinePlayers().forEach(fighter -> {
                if (fighters().collect(Collectors.toList()).contains(fighter)) {
                    return;
                }

                player.hidePlayer(fighter);
                fighter.hidePlayer(player);
            });
        }

        return true;
    }

    public void killPlayer(Player player) {
        if (!playersAlive.get(player.getUniqueId())) {
            return;
        }

        Kit kit = KitPlugin.getInstance().getKitManager().getKitByClass(KitType.MATCHMAKING, kitClass);

        cachedInventories.put(player.getUniqueId(), new InventorySnapshot(player, true, kit));
        playersAlive.put(player.getUniqueId(), false);

        broadcast(player.getDisplayName() + Colors.RED + " was eliminated!");

        if (remaining() == 1) {
            callEnding();
        }
    }

    public void hidePlayer(Player player) {
        Bukkit.getOnlinePlayers().forEach(fighter -> {
            player.hidePlayer(fighter);
            fighter.hidePlayer(player);
        });
    }

    private void callEnding() {
        currentTick = 0;
        state = MatchState.ENDING;

        broadcast(Colors.RED + "The match has ended.");

        Kit kit = KitPlugin.getInstance().getKitManager().getKitByClass(KitType.MATCHMAKING, kitClass);

        // cache inventories of player who haven't died yet
        fighters().filter(player -> !cachedInventories.containsKey(player.getUniqueId())).forEach(
                player -> cachedInventories.put(player.getUniqueId(), new InventorySnapshot(player, false, kit))
        );

        Player winner = Bukkit.getPlayer(getWinner());
        KitPlayer winnerKp = KitPlugin.getInstance().getPlayerManager().getPlayer(winner);

        winnerKp.getStats().setWins(winnerKp.getStats().getWins() + 1);

        boolean soupKit = !kit.getName().equals("NoDebuff");
        int remainingItems = (int) (soupKit ? Arrays.stream(winner.getInventory().getContents()).filter(i -> i != null && i.getType() == Material.MUSHROOM_SOUP).count() :
                Arrays.stream(winner.getInventory().getContents()).filter(i -> i != null && i.getDurability() == 16421).count());

        broadcast(winner.getDisplayName() + Colors.GREEN + " won the match with " + Colors.SECONDARY + remainingItems
                + " " + (soupKit ? "soups" : "potions") + Colors.GREEN + " remaining!");

        cachedInventories.values().forEach(snapshot -> KitPlugin.getInstance().getInventorySnapshotManager().cacheSnapshot(snapshot));

        ClickableMessage message = new ClickableMessage("Player Inventories: ").color(Colors.SECONDARY);

        int count = 0;

        for (Map.Entry<UUID, InventorySnapshot> entry : cachedInventories.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null) {
                continue;
            }

            message.add(player.getDisplayName())
                    .hover(Colors.GREEN + "Click to view " + player.getDisplayName() + Colors.GREEN + "'s inventory")
                    .command("/inventorysnapshot " + entry.getValue().getId());

            if (++count != cachedInventories.size()) {
                message.add(", ").color(Colors.PRIMARY);
            }
        }

        fighters().forEach(message::sendToPlayer);

        if (ranked) {
            KitPlayer loserKp = KitPlugin.getInstance().getPlayerManager().getPlayer(getLoser());
            int oldWinnerElo = winnerKp.getStats().getStatsByKit(kitClass).getAmountByStat(KitStat.ELO);
            int oldLoserElo = loserKp.getStats().getStatsByKit(kitClass).getAmountByStat(KitStat.ELO);
            MatchRatings ratings = MatchRatings.getRatingsFrom(oldWinnerElo, oldLoserElo);

            winnerKp.getStats().getStatsByKit(kitClass).setStatAmount(KitStat.ELO, ratings.getNewWinnerRating());
            loserKp.getStats().getStatsByKit(kitClass).setStatAmount(KitStat.ELO, ratings.getNewLoserRating());

            fighters().forEach(player ->
                    player.sendMessage(Colors.AQUA + "ELO Changes (±" + ratings.getDifference() + "): "
                            + winner.getDisplayName() + Colors.AQUA + " - " + ratings.getNewWinnerRating() + ", "
                            + Bukkit.getPlayer(getLoser()).getDisplayName() + Colors.AQUA + " - " + ratings.getNewLoserRating()));
        }
    }

    private void end() {
        sendAllToSpawn();
        KitPlugin.getInstance().getMatchManager().removeMatch(this);
        cancel();
    }

    private void sendAllToSpawn() {
        fighters().forEach(player -> {
            Bukkit.getOnlinePlayers().forEach(other -> {
                player.showPlayer(other);
                other.showPlayer(player);
            });

            KitPlayer kp = KitPlugin.getInstance().getPlayerManager().getPlayer(player);
            kp.setCurrentMatchId(null);

            KitPlugin.getInstance().getPlayerManager().resetPlayer(player, true);
        });
    }


    public void handleDisconnect(Player player) {
        killPlayer(player);
        disconnectedPlayers.add(player.getUniqueId());
    }

    private Stream<Player> fighters() {
        return playersAlive.keySet().stream().filter(id -> !disconnectedPlayers.contains(id)).map(Bukkit::getPlayer);
    }

    public UUID getWinner() {
        return playersAlive.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public UUID getLoser() {
        return playersAlive.entrySet().stream().filter(entry -> !entry.getValue()).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    private int remaining() {
        return (int) playersAlive.values().stream().filter(alive -> alive).count();
    }

    public void broadcast(String message) {
        fighters().forEach(player -> player.sendMessage(message));
    }

    public void broadcastWithNote(String message, boolean highPitch) {
        fighters().forEach(player -> {
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, highPitch ? 2.0F : 1.0F);
            player.sendMessage(message);
        });

    }

    @Override
    public void run() {
        switch (state) {
            case STARTING:
                if (currentTick == COUNTDOWN_SECONDS) {
                    state = MatchState.FIGHTING;
                    currentTick = 0;
                    broadcastWithNote(Colors.GREEN + "The match has started!", true);
                } else {
                    int secondsLeft = COUNTDOWN_SECONDS - currentTick;
                    broadcastWithNote(Colors.PRIMARY + "The match is starting in " + Colors.SECONDARY + secondsLeft
                            + Colors.PRIMARY + " " + (secondsLeft == 1 ? "second" : "seconds") + ".", false);
                }
                break;
            case ENDING:
                if (currentTick == RETURN_TO_SPAWN_SECONDS) {
                    end();
                    return;
                }
                break;
            case FIGHTING:
                // don't tick
                return;
        }

        currentTick++;
    }
}

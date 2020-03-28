package com.minebunch.kitpvp.listeners;

import com.google.common.collect.ImmutableSet;
import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import com.minebunch.core.utils.player.PlayerUtil;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.match.Match;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import com.minebunch.kitpvp.util.LocationUtil;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private static final Set<Material> ALLOWED_INTERACTION_TYPES = ImmutableSet.of(Material.STONE_BUTTON, Material.WOOD_BUTTON,
            Material.GOLD_PLATE, Material.WOOD_PLATE, Material.STONE_PLATE, Material.IRON_PLATE);
    private static final Set<Material> DROPPABLE_ITEMS = ImmutableSet.of(Material.BOWL, Material.GLASS_BOTTLE);
    private final KitPlugin plugin;

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            plugin.getPlayerManager().createPlayer(event.getUniqueId());
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            KitPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer());

            if (player == null) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Strings.DATA_LOAD_FAIL);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getPlayerManager().resetPlayer(player, true);

        player.sendMessage("");
        player.sendMessage(Colors.PRIMARY + "Welcome to " + Colors.SECONDARY + Colors.B + "MINEBUNCH" + Colors.PRIMARY + " KitPvP!");
        player.sendMessage("");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp == null) {
            return;
        }

        switch (kp.getState()) {
            case MATCHMAKING:
                plugin.getMatchmakingManager().removeFromMatchmaking(player);
                break;
            case MATCH:
                Match match = plugin.getMatchManager().getMatch(kp.getCurrentMatchId());
                match.handleDisconnect(player);
                break;
            case FFA:
                List<Player> nearbyPlayers = PlayerUtil.getNearbyPlayers(player, 16.0);
                boolean kill = false;

                for (Player nearbyPlayer : nearbyPlayers) {
                    KitPlayer nearbyKp = plugin.getPlayerManager().getPlayer(nearbyPlayer);

                    if (nearbyKp.getState() == PlayerState.FFA) {
                        kill = true;
                        break;
                    }
                }

                if (kill) {
                    player.setHealth(0.0);
                }
                break;
        }

        kp.save(true);

        plugin.getPlayerManager().removePlayer(player);
    }

    @EventHandler
    public void onClickInventorySnapshot(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getName().contains("'s Inventory")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractWithBlock(PlayerInteractEvent event) {
        if (!event.hasBlock() || ALLOWED_INTERACTION_TYPES.contains(event.getClickedBlock().getType())) {
            return;
        }

        Player player = event.getPlayer();
        CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player);

        if (profile.hasStaff()) {
            return;
        }

        if (!LocationUtil.isOutsideSpawn(event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (DROPPABLE_ITEMS.contains(event.getItemDrop().getItemStack().getType())) {
            event.getItemDrop().remove();
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        KitPlayer kp = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (!kp.isInSpawnState()) {
            return;
        }

        if (event.getClickedInventory() == player.getInventory()) {
            event.setCancelled(true);
        }
    }
}

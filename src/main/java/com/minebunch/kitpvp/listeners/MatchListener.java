package com.minebunch.kitpvp.listeners;

import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.time.timer.Timer;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.match.Match;
import com.minebunch.kitpvp.match.MatchState;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import com.minebunch.kitpvp.util.MathUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

@RequiredArgsConstructor
public class MatchListener implements Listener {
    private final KitPlugin plugin;

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        if (!player.getInventory().contains(Material.MUSHROOM_SOUP)) {
            return;
        }

        if (event.getFoodLevel() < 20) {
            event.setFoodLevel(20);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamagePreMatch(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (kp.getState() != PlayerState.MATCH) {
            return;
        }

        Match match = plugin.getMatchManager().getMatch(kp.getCurrentMatchId());

        if (match.getState() != MatchState.FIGHTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (event.getEntity() instanceof EnderPearl) {
            Timer timer = kp.getPearlTimer();
            timer.isActive(); // check if timer is active when the pearl actually shot (used to prevent glitches with checking on interact instead)
        }
    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();

            if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();

                if (arrow.getShooter() instanceof Player) {
                    Player shooter = (Player) arrow.getShooter();

                    if (entity == shooter) {
                        return;
                    }

                    double health = MathUtil.roundToHalves(entity.getHealth() - event.getFinalDamage());

                    if (health > 0.0) {
                        shooter.sendMessage(entity.getDisplayName() + Colors.PRIMARY + " now has " + Colors.RED + health + "❤" + Colors.PRIMARY + ".");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUseItemBeforeMatch(PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }

        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.MATCH) {
            return;
        }

        Match match = plugin.getMatchManager().getMatch(kp.getCurrentMatchId());
        ItemStack item = event.getItem();

        switch (item.getType()) {
            case ENDER_PEARL:
                if (match.getState() == MatchState.STARTING) {
                    event.setCancelled(true);
                    player.updateInventory();
                    player.sendMessage(Colors.RED + "You can't use pearls before the match has started.");
                    return;
                }

                Timer timer = kp.getPearlTimer();

                if (timer.isActive(false)) {
                    event.setCancelled(true);
                    player.updateInventory();
                    player.sendMessage(Colors.PRIMARY + "You can't throw pearls for another " + Colors.SECONDARY + timer.formattedExpiration() + Colors.PRIMARY + ".");
                }
                break;
            case POTION:
                if (match.getState() == MatchState.STARTING) {
                    Potion potion = Potion.fromItemStack(item);

                    if (potion.isSplash()) {
                        event.setCancelled(true);
                        player.updateInventory();
                    }
                }
                break;
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() == PlayerState.MATCH) {
            Match match = plugin.getMatchManager().getMatch(kp.getCurrentMatchId());
            match.killPlayer(player);

            final Location oldLocation = player.getLocation();
            oldLocation.setX(oldLocation.getBlockX() + 0.5);
            oldLocation.setY(oldLocation.getBlockY() + 0.5);
            oldLocation.setZ(oldLocation.getBlockZ() + 0.5);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    match.hidePlayer(player);
                    player.spigot().respawn();
                    player.teleport(oldLocation);
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onPearlLand(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        if (kp.getState() == PlayerState.MATCH) {
            Match match = plugin.getMatchManager().getMatch(kp.getCurrentMatchId());

            if (match.getState() == MatchState.ENDING) {
                event.setCancelled(true);
            }
        }

        Location pearlLocation = event.getTo();
        Location playerLocation = event.getFrom();

        pearlLocation.setX(pearlLocation.getBlockX() + 0.5);
        pearlLocation.setZ(pearlLocation.getBlockZ() + 0.5);

        if (playerLocation.getBlockY() < pearlLocation.getBlockY()) {
            Block block = pearlLocation.getBlock();

            for (BlockFace face : BlockFace.values()) {
                Material type = block.getRelative(face).getType();

                if (type == Material.GLASS || type == Material.BARRIER) {
                    pearlLocation.setY(pearlLocation.getBlockY() - 1.0);
                    break;
                }
            }
        } else {
            pearlLocation.setY(pearlLocation.getBlockY() + 0.5);
        }

        event.setTo(pearlLocation);
    }
}

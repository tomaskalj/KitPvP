package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.player.PlayerUtil;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public SpawnCommand(KitPlugin plugin) {
        super("spawn");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (kp.getState() == PlayerState.SPAWN) {
            player.teleport(plugin.getSpawn());
            return;
        } else if (kp.getState() != PlayerState.FFA) {
            player.sendMessage(Colors.RED + "You can only teleport to spawn from within the FFA area.");
            return;
        }

        List<Player> nearbyPlayers = PlayerUtil.getNearbyPlayers(player, 16.0);
        boolean wait = false;

        for (Player nearbyPlayer : nearbyPlayers) {
            KitPlayer nearbyKp = plugin.getPlayerManager().getPlayer(nearbyPlayer);

            if (nearbyKp.getState() == PlayerState.FFA) {
                wait = true;
                break;
            }
        }

        if (!wait) {
            warpToSpawn(player);
            player.sendMessage(Colors.GREEN + "You now have spawn protection.");
        } else {
            kp.setAwaitingTeleport(true);
            player.sendMessage(Colors.PRIMARY + "Players are nearby! You will be teleported in 5 seconds.");

            new BukkitRunnable() {
                private int count = 6;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                    } else if (!kp.isAwaitingTeleport()) {
                        player.sendMessage(Colors.RED + "You moved! The teleportation has been cancelled.");
                        cancel();
                    } else if (--count == 0) {
                        warpToSpawn(player);
                        player.sendMessage(Colors.GREEN + "You now have spawn protection.");
                        cancel();
                    } else {
                        player.sendMessage(Colors.PRIMARY + count + "...");
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }

    private void warpToSpawn(Player player) {
        plugin.getPlayerManager().resetPlayer(player, true);
        player.sendMessage(Colors.GREEN + "Warped to spawn.");
    }
}

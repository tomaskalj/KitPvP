package com.minebunch.kitpvp.listeners;

import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class MatchmakingListener implements Listener {
    private final KitPlugin plugin;

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.hasItem() || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();

        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.MATCHMAKING) {
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getItem();

        switch (item.getType()) {
            case INK_SACK:
                player.getInventory().setHeldItemSlot(2);
                plugin.getMatchmakingManager().removeFromMatchmaking(player);
                break;
        }
    }
}

package com.minebunch.kitpvp.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SoupListener implements Listener {

    @EventHandler
    public void onSoup(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.MUSHROOM_SOUP || event.getItem().getAmount() != 1 ||
                (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)) {
            return;
        }

        Player player = event.getPlayer();
        double health = player.getHealth();

        if (health <= 19) {
            event.setCancelled(true);

            player.setHealth(Math.min(player.getHealth() + 7, player.getMaxHealth()));
            player.setItemInHand(new ItemStack(Material.BOWL));
            player.updateInventory();
        } else if (player.getFoodLevel() != 20) {
            event.setCancelled(true);

            player.setFoodLevel(Math.min(player.getFoodLevel() + 7, 20));
            player.setItemInHand(new ItemStack(Material.BOWL));
            player.updateInventory();
        }
    }
}

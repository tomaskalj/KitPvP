package com.minebunch.kitpvp.items;

import com.google.common.collect.ImmutableMap;
import com.minebunch.core.utils.item.ItemBuilder;
import com.minebunch.core.utils.message.Colors;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Getter
@RequiredArgsConstructor
public enum ItemHotbar {
    SPAWN_ITEMS(ImmutableMap.<Integer, ItemStack>builder()
            .put(0, new ItemBuilder(Material.CHEST).name(Colors.GOLD + "Select a Kit").build())
            .put(4, new ItemBuilder(Material.BLAZE_ROD).name(Colors.PRIMARY + "Duel a Player " + Colors.SECONDARY + "(Right Click on Them)").build())
            .put(7, new ItemBuilder(Material.DIAMOND_SWORD).name(Colors.AQUA + "Join Ranked Matchmaking").build())
            .put(8, new ItemBuilder(Material.IRON_SWORD).name(Colors.GRAY + "Join Unranked Matchmaking").build())
            .build()
    ),
    QUEUE_ITEMS(ImmutableMap.<Integer, ItemStack>builder()
            .put(0, new ItemBuilder(Material.INK_SACK).durability(1).name(Colors.RED + "Leave Matchmaking").build())
            .build()
    );

    private final Map<Integer, ItemStack> items;

    public void apply(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        items.forEach(inventory::setItem);
    }
}

package com.minebunch.kitpvp.match.inventory;

import com.minebunch.core.utils.item.ItemBuilder;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.time.TimeUtil;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.util.MathUtil;
import com.minebunch.kitpvp.util.PotionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class InventorySnapshot {
    private final Inventory inventory;
    @Getter
    private final UUID id = UUID.randomUUID();

    public InventorySnapshot(Player player, boolean dead, Kit kit) {
        KitPlugin plugin = KitPlugin.getInstance();

        this.inventory = plugin.getServer().createInventory(null, 54, player.getName() + "'s Inventory");

        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i + 27, contents[i]);
            inventory.setItem(i + 18, contents[i + 27]);
            inventory.setItem(i + 9, contents[i + 18]);
            inventory.setItem(i, contents[i + 9]);
        }

        for (int i = 36; i < 40; i++) {
            inventory.setItem(i, armor[39 - i]);
        }

        ItemBuilder playerInfoItem = new ItemBuilder(Material.SPECKLED_MELON).name(Colors.ACCENT + Colors.B + "Player Info");
        List<String> lore = new ArrayList<>();

        double health = dead ? 0.0 : MathUtil.roundToHalves(player.getHealth());
        double hunger = MathUtil.roundToHalves(player.getFoodLevel());

        lore.add(Colors.PRIMARY + "Health: " + Colors.SECONDARY + health);
        lore.add(Colors.PRIMARY + "Hunger: " + Colors.SECONDARY + hunger);

        boolean soupKit = !kit.getName().equals("NoDebuff");
        int remainingPots = (int) Arrays.stream(contents).filter(i -> i != null && i.getDurability() == 16421).count();
        int remainingItems = soupKit ? countItemType(contents, Material.MUSHROOM_SOUP) : remainingPots;

        lore.add(Colors.PRIMARY + "Remaining " + (soupKit ? "Soups" : "Potions") + ": " + Colors.SECONDARY + remainingItems);

        if (player.getActivePotionEffects().size() != 0) {
            lore.add("");
            lore.add(Colors.ACCENT + Colors.B + "Effects");

            for (PotionEffect effect : player.getActivePotionEffects()) {
                String effectName = PotionUtil.getFriendlyName(effect.getType());
                int level = effect.getAmplifier() + 1;
                String effectInfo = Colors.PRIMARY + effectName + (level < 2 ? "" : " " + level)
                        + ": " + Colors.SECONDARY + TimeUtil.formatTimeSecondsToClock(effect.getDuration() / 20);
                lore.add(effectInfo);
            }
        }

        playerInfoItem.lore(lore.toArray(new String[0]));

        inventory.setItem(49, playerInfoItem.build());
    }

    private static int countItemType(ItemStack[] items, Material material) {
        return (int) Arrays.stream(items).filter(item -> item != null && item.getType() == material).count();
    }

    public void openTo(Player player) {
        player.openInventory(inventory);
    }
}

package com.minebunch.kitpvp.kits.match.impl;

import com.google.common.collect.ImmutableSet;
import com.minebunch.kitpvp.kits.match.MatchmakingKit;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Elite extends MatchmakingKit {
    public Elite() {
        super("Diamond Sword, Iron Armor, Hotbar of Soup", 0);
        addItem(new ItemStack(Material.DIAMOND_SWORD));
        addItem(8, new ItemStack(Material.MUSHROOM_SOUP));
    }

    @Override
    protected Set<ItemStack> getArmor() {
        return ImmutableSet.of(
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        );
    }

    @Override
    protected Set<PotionEffect> getEffects() {
        return ImmutableSet.of(
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)
        );
    }

    @Override
    protected ItemStack getIconItem() {
        return new ItemStack(Material.DIAMOND_SWORD);
    }
}

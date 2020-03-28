package com.minebunch.kitpvp.kits.ffa.impl;

import com.google.common.collect.ImmutableSet;
import com.minebunch.kitpvp.kits.ffa.FfaKit;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Tank extends FfaKit {
    public Tank() {
        super("Start with Full Diamond Armor, a Diamond Sword, and Slowness I.");
        addItem(new ItemStack(Material.DIAMOND_SWORD));
    }

    @Override
    protected Set<ItemStack> getArmor() {
        return ImmutableSet.of(
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.DIAMOND_HELMET)
        );
    }

    @Override
    protected Set<PotionEffect> getEffects() {
        return ImmutableSet.of(
                new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1)
        );
    }

    @Override
    protected ItemStack getIconItem() {
        return new ItemStack(Material.DIAMOND_CHESTPLATE);
    }
}

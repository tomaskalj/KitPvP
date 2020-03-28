package com.minebunch.kitpvp.kits.match.impl;

import com.google.common.collect.ImmutableSet;
import com.minebunch.core.utils.item.ItemBuilder;
import com.minebunch.kitpvp.kits.match.MatchmakingKit;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed extends MatchmakingKit {
    public Speed() {
        super("Speed 2, Sharp 1 Diamond Sword, Iron Armor, Hotbar of Soup", 5);
        addItem(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build());
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
        return new ItemBuilder(Material.POTION).durability(8226).build();
    }
}
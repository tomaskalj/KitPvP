package com.minebunch.kitpvp.kits.ffa.impl;

import com.google.common.collect.ImmutableSet;
import com.minebunch.core.utils.item.ItemBuilder;
import com.minebunch.kitpvp.kits.ffa.FfaKit;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Archer extends FfaKit {
    public Archer() {
        super("Start with Protection I Leather and Chainmail Armor, a Sharp I Iron Sword, and a Power I Bow.");
        addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build());
        addItem(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 1).enchant(Enchantment.ARROW_INFINITE, 1).build());
        setItem(27, new ItemStack(Material.ARROW));
        fill(new ItemStack(Material.MUSHROOM_SOUP));
    }

    @Override
    protected Set<ItemStack> getArmor() {
        return ImmutableSet.of(
                new ItemBuilder(Material.LEATHER_BOOTS).dye(Color.RED).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.LEATHER_HELMET).dye(Color.RED).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build()
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
        return new ItemStack(Material.BOW);
    }
}

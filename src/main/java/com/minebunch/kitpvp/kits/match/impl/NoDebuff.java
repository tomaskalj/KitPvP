package com.minebunch.kitpvp.kits.match.impl;

import com.google.common.collect.ImmutableSet;
import com.minebunch.core.utils.item.ItemBuilder;
import com.minebunch.kitpvp.kits.match.MatchmakingKit;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class NoDebuff extends MatchmakingKit {
    public NoDebuff() {
        super("Speed II, Diamond Sword, Full Iron Armor, Hotbar of Soup", 8);
        addItem(new ItemBuilder(Material.DIAMOND_SWORD)
                .enchant(Enchantment.DAMAGE_ALL, 3)
                .enchant(Enchantment.FIRE_ASPECT, 2)
                .enchant(Enchantment.DURABILITY, 3).build());
        addItem(new ItemStack(Material.ENDER_PEARL, 16));

        Potion fireResPotion = new Potion(PotionType.FIRE_RESISTANCE);
        fireResPotion.setHasExtendedDuration(true);

        setItem(2, fireResPotion.toItemStack(1));

        ItemStack speedPotion = new Potion(PotionType.SPEED, 2).toItemStack(1);

        for (int slot : new int[]{3, 17, 26, 35}) {
            setItem(slot, speedPotion);
        }

        setItem(8, new ItemStack(Material.COOKED_BEEF, 64));

        fill(new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1));
    }

    private static ItemStack enchantOp(ItemBuilder builder) {
        return builder.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .enchant(Enchantment.DURABILITY, 3).build();
    }

    @Override
    protected Set<ItemStack> getArmor() {
        return ImmutableSet.of(
                enchantOp(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_FALL, 3)),
                enchantOp(new ItemBuilder(Material.DIAMOND_LEGGINGS)),
                enchantOp(new ItemBuilder(Material.DIAMOND_CHESTPLATE)),
                enchantOp(new ItemBuilder(Material.DIAMOND_HELMET))
        );
    }

    @Override
    protected Set<PotionEffect> getEffects() {
        return new HashSet<>();
    }

    @Override
    protected ItemStack getIconItem() {
        return new ItemBuilder(Material.POTION).durability(16421).build();
    }
}

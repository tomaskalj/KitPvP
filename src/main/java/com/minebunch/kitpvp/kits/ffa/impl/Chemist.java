package com.minebunch.kitpvp.kits.ffa.impl;

import com.google.common.collect.Sets;
import com.minebunch.core.utils.item.ItemBuilder;
import com.minebunch.kitpvp.kits.ffa.FfaKit;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class Chemist extends FfaKit {
    public Chemist() {
        super("Start with Protection I Gold and Chainmail Armor, a Sharp I Iron Sword, and 3 Poison and 3 Instant Damage Splash Potions.");
        addItem(new ItemBuilder(Material.IRON_SWORD).enchant(Enchantment.DAMAGE_ALL, 1).build());

        addItem(getPoisonII().toItemStack(3));
        addItem(getDamageII().toItemStack(3));
    }

    @Override
    protected Set<ItemStack> getArmor() {
        Set<ItemStack> armor = Sets.newHashSet(
                new ItemStack(Material.CHAINMAIL_BOOTS),
                new ItemStack(Material.CHAINMAIL_LEGGINGS),
                new ItemStack(Material.GOLD_CHESTPLATE),
                new ItemStack(Material.CHAINMAIL_HELMET)
        );
        armor.forEach(item -> item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        return armor;
    }

    @Override
    protected Set<PotionEffect> getEffects() {
        return null;
    }

    @Override
    protected ItemStack getIconItem() {
        return getPoisonII().toItemStack(1);
    }

    private Potion getPoisonII() {
        Potion poison = new Potion(PotionType.POISON).splash();
        poison.setLevel(2);
        return poison;
    }

    private Potion getDamageII() {
        Potion damage = new Potion(PotionType.INSTANT_DAMAGE).splash();
        damage.setLevel(2);
        return damage;
    }
}

package com.minebunch.kitpvp.kits.technical;

import com.minebunch.core.utils.item.ItemBuilder;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.util.StringUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public abstract class Kit {
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final ItemStack[] armor;
    private final Set<PotionEffect> effects;
    @Getter
    private final KitType type;
    @Getter
    private final String name = getClass().getSimpleName();
    @Getter
    private final ItemStack icon;

    public Kit(KitType type, String description) {
        this.type = type;
        this.armor = getArmor().toArray(new ItemStack[0]);
        this.effects = getEffects();
        description = Colors.YELLOW + description;
        this.icon = ItemBuilder.from(getIconItem()).name(Colors.SECONDARY + name).lore(StringUtil.separateDescription(description)).build();
    }

    public void apply(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        player.getInventory().setArmorContents(null);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        player.getInventory().setArmorContents(armor);
        items.forEach((slot, item) -> player.getInventory().setItem(slot, item));

        if (effects != null && !effects.isEmpty()) {
            effects.forEach(player::addPotionEffect);
        }
    }

    protected void setItem(int slot, ItemStack item) {
        items.put(slot, item);
    }

    protected void addItem(int amount, ItemStack item) {
        int count = 0;

        for (int i = 0; i <= 35; i++) {
            if (!items.containsKey(i)) {
                items.put(i, item);

                if (++count == amount) {
                    return;
                }
            }
        }
    }

    protected void addItem(ItemStack item) {
        addItem(1, item);
    }

    protected void fill(ItemStack item) {
        for (int i = 0; i <= 35; i++) {
            items.putIfAbsent(i, item);
        }
    }

    protected abstract Set<ItemStack> getArmor();

    protected abstract Set<PotionEffect> getEffects();

    protected abstract ItemStack getIconItem();
}

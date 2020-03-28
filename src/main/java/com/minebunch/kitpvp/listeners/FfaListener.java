package com.minebunch.kitpvp.listeners;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerDamageData;
import com.minebunch.kitpvp.player.PlayerState;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class FfaListener implements Listener {
    private final KitPlugin plugin;
    private final Map<Item, UUID> onlyPickup = new HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();

        Player player = event.getEntity();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.FFA) {
            return;
        }

        PlayerDamageData damageData = kp.getDamageData();
        double totalDamage = damageData.total();
        Map<UUID, Double> sortedDamage = damageData.sortedMap();
        boolean killer = true;

        for (Map.Entry<UUID, Double> entry : sortedDamage.entrySet()) {
            UUID damagerId = entry.getKey();
            Player damager = plugin.getServer().getPlayer(damagerId);
            CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(damagerId);
            KitPlayer damagerProfile = plugin.getPlayerManager().getPlayer(damager);
            double damage = entry.getValue();
            double percent = damage / totalDamage;

            if (!killer && percent < 0.15) {
                continue;
            }

            int worth = killer ? 15 : (int) (15 * percent);
            if (profile.hasRank(Rank.SPONSOR)) {
                worth *= 2;
            } else if (profile.hasRank(Rank.MVP)) {
                worth *= 1.5;
            } else if (profile.hasRank(Rank.VIP)) {
                worth *= 1.2;
            }

            if (damagerProfile.getSelectedKit() == null) {
                return;
            }

            String strPercent = String.format("%.1f", percent * 100);

            damagerProfile.getStats().setCredits(damagerProfile.getStats().getCredits() + worth);

            if (killer) {
                killer = false;
                damagerProfile.getStats().handleKill();
                damager.sendMessage(Colors.PRIMARY + "You killed " + Colors.SECONDARY + player.getDisplayName()
                        + Colors.PRIMARY + " and received " + Colors.SECONDARY + worth + Colors.PRIMARY + " credits "
                        + Colors.GRAY + "(" + strPercent + "% of damage)" + Colors.PRIMARY + ".");

                for (int i = 0; i < 8; i++) {
                    Item item = player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.MUSHROOM_SOUP));
                    onlyPickup.put(item, damagerId);
                }

                player.sendMessage(Colors.PRIMARY + "You were slain by " + Colors.SECONDARY + damager.getDisplayName() + Colors.PRIMARY + ".");

                int streak = kp.getStats().getKillStreak();
                boolean announceKillStreak = (streak <= 10 && streak % 5 == 0) ||
                        (streak > 10 && streak <= 100 && streak % 10 == 0) || (streak > 100 && streak % 50 == 0);

                if (announceKillStreak) {
                    plugin.getServer().broadcastMessage(Colors.RED + "[Streaks] " + damager.getDisplayName()
                            + Colors.GREEN + " is on a kill streak of " + streak + "!");
                }
            } else {
                damager.sendMessage(Colors.PRIMARY + "You got an assist on " + Colors.SECONDARY + player.getDisplayName()
                        + Colors.PRIMARY + " and received " + Colors.SECONDARY + worth + Colors.PRIMARY + " credits "
                        + Colors.GRAY + "(" + strPercent + "% of damage)" + Colors.PRIMARY + ".");
            }
        }

        damageData.clear();
        kp.getStats().handleDeath();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.spigot().respawn(), 20L);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (onlyPickup.containsKey(event.getItem()) && !player.getUniqueId().equals(onlyPickup.get(event.getItem()))) {
            event.setCancelled(true);
        }
    }
}

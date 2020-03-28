package com.minebunch.kitpvp.listeners;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.match.MatchmakingKit;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import com.minebunch.kitpvp.util.LocationUtil;
import com.minebunch.kitpvp.util.gui.GuiClickable;
import com.minebunch.kitpvp.util.gui.GuiFolder;
import com.minebunch.kitpvp.util.gui.GuiPage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class SpawnListener implements Listener {
    private final KitPlugin plugin;

    @EventHandler
    public void onSpawnDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            player.setHealth(0.0);
            return;
        }

        if (kp.isInSpawnState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() == PlayerState.MATCH) {
            return;
        }

        if (event.getFoodLevel() < 20) {
            event.setFoodLevel(20);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLoseSpawnProtectionByMovement(PlayerMoveEvent event) {
        loseSpawnProtection(event);
    }

    @EventHandler
    public void onLoseSpawnProtectionByTeleportation(PlayerTeleportEvent event) {
        loseSpawnProtection(event);
    }

    private void loseSpawnProtection(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();

        // optimized: only checks movement every block
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.isAwaitingTeleport()) {
            kp.setAwaitingTeleport(false);
        }

        if (kp.getState() != PlayerState.SPAWN) {
            return;
        }

        if (LocationUtil.isOutsideSpawn(event.getTo())) {
            kp.loseSpawnProtection(player);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.FFA) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Arrow) {
            if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
                damager = (Player) ((Arrow) event.getDamager()).getShooter();
            }
        }

        if (damager == null) {
            return;
        }

        KitPlayer dp = plugin.getPlayerManager().getPlayer(damager);

        if (dp.getState() == PlayerState.SPAWN) {
            dp.loseSpawnProtection(damager);
        } else if (dp.getState() == PlayerState.MATCHMAKING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMoveInQueue(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.MATCHMAKING) {
            return;
        }

        if (LocationUtil.isOutsideSpawn(event.getTo())) {
            event.setTo(LocationUtil.normalize(event.getFrom()));
            player.sendMessage(Colors.RED + "You can't leave spawn while in matchmaking!");
        }
    }

    @EventHandler
    public void onClickItem(PlayerInteractEvent event) {
        if (!event.hasItem() || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.SPAWN) {
            return;
        }

        event.setCancelled(true);

        ItemStack item = event.getItem();

        switch (item.getType()) {
            case CHEST: {
                if (kp.getSelectedKit() != null) {
                    return;
                }

                GuiFolder duelFolder = new GuiFolder("Select a Kit", 9);
                GuiPage selectionPage = new GuiPage(duelFolder);

                int slot = 0;

                for (Kit kit : plugin.getKitManager().getKitsByType(KitType.FFA).values()) {
                    selectionPage.addItem(slot++, new GuiClickable() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            event.setCancelled(true);
                            player.closeInventory();
                            kit.apply(player);
                            kp.setSelectedKit(kit.getClass());
                            player.sendMessage(Colors.GREEN + "You have selected the " + Colors.GOLD + kit.getName() + Colors.GREEN + " kit!");
                        }

                        @Override
                        public ItemStack getItemStack() {
                            return kit.getIcon();
                        }
                    });
                }

                duelFolder.setCurrentPage(selectionPage);
                duelFolder.openGui(player);
                break;
            }
            case DIAMOND_SWORD: {
                if (kp.getSelectedKit() != null) {
                    return;
                }

                CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player);

                if (!profile.hasDonor()) {
                    int wins = kp.getStats().getWins();

                    if (wins < 10) {
                        player.sendMessage(Colors.RED + "You still need " + Colors.SECONDARY + (10 - wins)
                                + Colors.RED + " wins to play ranked matches.");
                        return;
                    }
                }

                GuiFolder duelFolder = new GuiFolder("Select Ranked Matchmaking", 9);
                GuiPage selectionPage = new GuiPage(duelFolder);

                plugin.getKitManager().getKitsByType(KitType.MATCHMAKING).values().forEach(kit -> {
                    MatchmakingKit matchmakingKit = (MatchmakingKit) kit;

                    selectionPage.addItem(matchmakingKit.getSlot(), new GuiClickable() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            event.setCancelled(true);
                            player.closeInventory();
                            plugin.getMatchmakingManager().addToMatchmaking(player, kit, true);
                        }

                        @Override
                        public ItemStack getItemStack() {
                            return kit.getIcon();
                        }
                    });
                });

                duelFolder.setCurrentPage(selectionPage);
                duelFolder.openGui(player);
                break;
            }
            case IRON_SWORD: {
                if (kp.getSelectedKit() != null) {
                    return;
                }

                GuiFolder duelFolder = new GuiFolder("Select Unranked Matchmaking", 9);
                GuiPage selectionPage = new GuiPage(duelFolder);

                plugin.getKitManager().getKitsByType(KitType.MATCHMAKING).values().forEach(kit -> {
                    MatchmakingKit matchmakingKit = (MatchmakingKit) kit;

                    selectionPage.addItem(matchmakingKit.getSlot(), new GuiClickable() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            event.setCancelled(true);
                            player.closeInventory();
                            plugin.getMatchmakingManager().addToMatchmaking(player, kit, false);
                        }

                        @Override
                        public ItemStack getItemStack() {
                            return kit.getIcon();
                        }
                    });
                });

                duelFolder.setCurrentPage(selectionPage);
                duelFolder.openGui(player);
                break;
            }
        }
    }

    @EventHandler
    public void onInteractWithPlayer(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.SPAWN) {
            return;
        }

        if (event.getRightClicked() instanceof Player && player.getItemInHand() != null && player.getItemInHand().getType() == Material.BLAZE_ROD) {
            Player clicked = (Player) event.getRightClicked();
            player.performCommand("duel " + clicked.getName());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() == PlayerState.FFA) {
            kp.setState(PlayerState.SPAWN);
        }

        if (kp.getState() != PlayerState.SPAWN) {
            return;
        }

        event.setRespawnLocation(plugin.getSpawn());

        plugin.getPlayerManager().resetPlayer(player, false);
    }
}

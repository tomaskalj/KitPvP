package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.ClickableMessage;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.match.MatchmakingKit;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import com.minebunch.kitpvp.util.gui.GuiClickable;
import com.minebunch.kitpvp.util.gui.GuiFolder;
import com.minebunch.kitpvp.util.gui.GuiPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DuelCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public DuelCommand(KitPlugin plugin) {
        super("duel");
        this.plugin = plugin;
        setUsage(Colors.RED + "Usage: /duel <player>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getDuelTimer().isActive(false)) {
            player.sendMessage(Colors.RED + "You can't send a duel request for another " + kp.getDuelTimer().formattedExpiration() + ".");
            return;
        }

        if (kp.getState() != PlayerState.SPAWN) {
            player.sendMessage(Colors.RED + "You must have spawn protection to duel a player.");
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Strings.PLAYER_NOT_FOUND);
            return;
        }

        if (target == player) {
            player.sendMessage(Colors.RED + "You can't duel yourself.");
            return;
        }

        KitPlayer targetKp = plugin.getPlayerManager().getPlayer(target);

        if (targetKp.getState() != PlayerState.SPAWN) {
            player.sendMessage(Colors.RED + "That player doesn't have spawn protection.");
            return;
        }

        GuiFolder duelFolder = new GuiFolder("Select a Duel Kit", 9);
        GuiPage selectionPage = new GuiPage(duelFolder);

        plugin.getKitManager().getKitsByType(KitType.MATCHMAKING).values().forEach(kit -> {
            MatchmakingKit matchmakingKit = (MatchmakingKit) kit;

            selectionPage.addItem(matchmakingKit.getSlot(), new GuiClickable() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    event.setCancelled(true);
                    player.closeInventory();
                    sendDuelRequest(target, player, kit);
                }

                @Override
                public ItemStack getItemStack() {
                    return kit.getIcon();
                }
            });
        });

        duelFolder.setCurrentPage(selectionPage);
        duelFolder.openGui(player);
    }

    private void sendDuelRequest(Player to, Player from, Kit kit) {
        KitPlayer fromKp = plugin.getPlayerManager().getPlayer(from);

        fromKp.getDuelTimer().isActive(); // Start the cooldown timer

        from.sendMessage(Colors.GREEN + "Sent a duel request to " + to.getDisplayName()
                + Colors.GREEN + " with kit " + Colors.GOLD + kit.getName() + Colors.GREEN + ".");

        KitPlayer toKp = plugin.getPlayerManager().getPlayer(to);

        toKp.getDuelRequests().put(from.getUniqueId(), kit.getName());

        ClickableMessage acceptMessage = new ClickableMessage(from.getDisplayName())
                .add(" has sent you a duel request with kit ").color(Colors.GREEN)
                .add(kit.getName()).color(Colors.GOLD)
                .add(".").color(Colors.GREEN)
                .add(" [Click to Accept]")
                .color(Colors.YELLOW)
                .hover(Colors.GREEN + "Accept Duel Request")
                .command("/accept " + from.getName());

        acceptMessage.sendToPlayer(to);
    }
}

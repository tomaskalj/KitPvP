package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import org.bukkit.entity.Player;

public class KitCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public KitCommand(KitPlugin plugin) {
        super("kit");
        this.plugin = plugin;
        setUsage(Colors.RED + "Usage: /kit <kit>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.SPAWN) {
            player.sendMessage(Colors.RED + "You can only pick a kit in spawn.");
            return;
        }

        Kit kit = plugin.getKitManager().getKitByName(KitType.FFA, args[0]);

        if (kit == null) {
            player.sendMessage(Colors.RED + "That kit doesn't exist.");
            return;
        }

        kit.apply(player);
    }
}

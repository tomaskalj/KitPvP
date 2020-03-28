package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.match.Match;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import org.bukkit.entity.Player;

public class AcceptCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public AcceptCommand(KitPlugin plugin) {
        super("accept");
        this.plugin = plugin;
        setUsage(Colors.RED + "Usage: /accept <player>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        KitPlayer kp = plugin.getPlayerManager().getPlayer(player);

        if (kp.getState() != PlayerState.SPAWN) {
            player.sendMessage(Colors.RED + "You must have spawn protection to accept a duel request.");
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

        String kitName = kp.getDuelRequests().getIfPresent(target.getUniqueId());

        if (kitName == null) {
            player.sendMessage(Colors.RED + "You don't have a duel request from that player.");
            return;
        }

        Kit kit = plugin.getKitManager().getKitByName(KitType.MATCHMAKING, kitName);

        Match match = new Match(kit.getClass(), player, target);

        match.broadcast(Colors.GREEN + "Starting match between " + player.getDisplayName() + Colors.GREEN + " and "
                + target.getDisplayName() + Colors.GREEN + " with kit " + Colors.GOLD + kitName + Colors.GREEN + ".");

        match.start();
    }
}

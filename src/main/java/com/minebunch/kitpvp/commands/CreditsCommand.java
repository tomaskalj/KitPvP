package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CreditsCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public CreditsCommand(KitPlugin plugin) {
        super("credits");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = args.length < 1 || Bukkit.getPlayer(args[0]) == null ? player : Bukkit.getPlayer(args[0]);
        KitPlayer targetKp = plugin.getPlayerManager().getPlayer(target);
        PlayerStats stats = targetKp.getStats();

        player.sendMessage(Colors.ACCENT + (target == player ? "Your" : target.getDisplayName() + Colors.ACCENT + "'s") + " Credits");
        player.sendMessage(Colors.PRIMARY + "Credits: " + Colors.SECONDARY + stats.getCredits());
        player.sendMessage("");
    }
}

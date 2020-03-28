package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitStat;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public StatsCommand(KitPlugin plugin) {
        super("stats");
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = args.length < 1 || Bukkit.getPlayer(args[0]) == null ? player : Bukkit.getPlayer(args[0]);
        KitPlayer targetKp = plugin.getPlayerManager().getPlayer(target);
        PlayerStats stats = targetKp.getStats();

        player.sendMessage(Colors.ACCENT + (target == player ? "Your" : target.getDisplayName() + Colors.ACCENT + "'s") + " Stats");
        player.sendMessage(Colors.PRIMARY + "Kills: " + Colors.SECONDARY + stats.getKills());
        player.sendMessage(Colors.PRIMARY + "Deaths: " + Colors.SECONDARY + stats.getDeaths());
        player.sendMessage(Colors.PRIMARY + "Current Kill Streak: " + Colors.SECONDARY + stats.getKillStreak());
        player.sendMessage(Colors.PRIMARY + "Highest Kill Streak: " + Colors.SECONDARY + stats.getHighestKillStreak());
        player.sendMessage(Colors.PRIMARY + "KDR: " + Colors.SECONDARY + stats.getKillDeathRatio());
        player.sendMessage(Colors.PRIMARY + "1v1 Wins: " + Colors.SECONDARY + stats.getWins());
        player.sendMessage(Colors.PRIMARY + "Credits: " + Colors.SECONDARY + stats.getCredits());
        player.sendMessage("");

        for (Kit kit : plugin.getKitManager().getKitsByType(KitType.MATCHMAKING).values()) {
            int elo = stats.getStatsByKit(kit.getClass()).getAmountByStat(KitStat.ELO);
            player.sendMessage(Colors.PRIMARY + kit.getName() + " ELO: " + Colors.SECONDARY + elo);
        }
    }
}

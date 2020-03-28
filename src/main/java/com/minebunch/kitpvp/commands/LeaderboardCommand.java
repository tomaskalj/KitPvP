package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.leaderboard.Leaderboard;
import com.minebunch.kitpvp.leaderboard.LeaderboardEntry;
import org.bukkit.entity.Player;

public class LeaderboardCommand extends PlayerCommand {
    private static final int PAGE_SIZE = 10;
    private final KitPlugin plugin;

    public LeaderboardCommand(KitPlugin plugin) {
        super("leaderboard");
        this.plugin = plugin;
        setAliases("leaderboards", "lb", "top10", "topstats");
        setUsage(Colors.RED + "Usage: /leaderboard <kit> [page]");
    }

    private static int validIntegerOf(int max, String arg) {
        try {
            int i = Integer.parseInt(arg);

            if (i < 1) {
                return 1;
            } else if (i > max) {
                return max;
            } else {
                return i;
            }
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private static String formatNumberWithCommas(long amount) {
        return String.format("%,d", amount);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        Kit kit = plugin.getKitManager().getKitByName(KitType.MATCHMAKING, args[0]);

        if (kit == null) {
            player.sendMessage(Colors.RED + "That's not a valid kit!");
            return;
        }

        Leaderboard leaderboard = plugin.getLeaderboardManager().getLeaderboardByKit(kit.getClass());

        if (leaderboard == null || leaderboard.getEntries().isEmpty()) {
            player.sendMessage(Colors.RED + "The leaderboards are empty... Please wait until they have been updated.");
            return;
        }

        int size = leaderboard.getEntries().size();
        int maxPages = size % PAGE_SIZE == 0 ? size / PAGE_SIZE : size / PAGE_SIZE + 1;
        int pageIndex = args.length < 2 ? 1 : validIntegerOf(maxPages, args[1]);

        int index = (pageIndex - 1) * PAGE_SIZE;
        int count = index + 1;

        player.sendMessage(Colors.PRIMARY + "Leaderboard for " + kit.getName()
                + Colors.SECONDARY + " (" + pageIndex + " of " + maxPages + ")");
        player.sendMessage("");

        for (LeaderboardEntry entry : leaderboard.getEntries(index)) {
            if (count > index + (PAGE_SIZE + 1)) {
                break;
            }

            player.sendMessage(Colors.PRIMARY + formatNumberWithCommas(count) + ". " + Colors.PRIMARY
                    + entry.getDisplayName() + Colors.GRAY + ": " + Colors.SECONDARY + formatNumberWithCommas(entry.getAmount()) + " ELO");

            count++;
        }
    }
}

package com.minebunch.kitpvp.commands;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.jedis.cache.UUIDCache;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.TaskUtil;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.player.KitPlayer;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetStatsCommand extends BaseCommand {
    private final KitPlugin plugin;

    public ResetStatsCommand(KitPlugin plugin) {
        super("resetstats", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(Colors.RED + "Usage: /resetstats <player>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target != null) {
            KitPlayer targetKp = plugin.getPlayerManager().getPlayer(target);
            targetKp.getStats().reset();
            sender.sendMessage(Colors.GREEN + "Cleared stats for " + target.getDisplayName() + Colors.GREEN + ".");
        } else {
            TaskUtil.runAsync(plugin, () -> {
                UUIDCache cache = CorePlugin.getInstance().getUuidCache();
                UUID id = cache.getUuid(args[0].toLowerCase());

                if (id == null) {
                    sender.sendMessage(Strings.PLAYER_NOT_FOUND);
                    return;
                }

                Document document = CorePlugin.getInstance().getMongoStorage().getDocument("kit_players", id);

                if (document == null) {
                    sender.sendMessage(Colors.RED + "Player has no KitPvP data.");
                    return;
                }

                document.clear();

                sender.sendMessage(Colors.GREEN + "Cleared stats for " + args[0] + Colors.GREEN + ".");
            });
        }
    }
}

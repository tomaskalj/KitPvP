package com.minebunch.kitpvp.commands;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.commands.BaseCommand;
import com.minebunch.core.jedis.cache.UUIDCache;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.NumberUtil;
import com.minebunch.core.utils.TaskUtil;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.message.Strings;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.player.KitPlayer;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditCreditsCommand extends BaseCommand {
    private final KitPlugin plugin;

    public EditCreditsCommand(KitPlugin plugin) {
        super("editcredits", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(Colors.RED + "Usage: /editcredits <player> <amount>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(usageMessage);
            return;
        }

        Integer amount = NumberUtil.getInteger(args[1]);

        if (amount == null || amount < 0) {
            sender.sendMessage(Colors.RED + "You must enter an amount above 0.");
            return;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target != null) {
            KitPlayer targetKp = plugin.getPlayerManager().getPlayer(target);
            targetKp.getStats().setCredits(amount);
            sender.sendMessage(Colors.GREEN + "Set " + target.getDisplayName() + Colors.GREEN + "'s credits to " + amount + ".");
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

                Document statsDocument = document.get("stats", Document.class);

                statsDocument.replace("credits", amount);

                sender.sendMessage(Colors.GREEN + "Set " + args[0] + Colors.GREEN + "'s credits to " + amount + ".");
            });
        }
    }
}

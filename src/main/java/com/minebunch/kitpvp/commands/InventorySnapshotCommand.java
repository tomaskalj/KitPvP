package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.match.inventory.InventorySnapshot;
import java.util.UUID;
import org.bukkit.entity.Player;

public class InventorySnapshotCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public InventorySnapshotCommand(KitPlugin plugin) {
        super("inventorysnapshot");
        this.plugin = plugin;
        setUsage(Colors.RED + "Usage: /inventorysnapshot <id>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return;
        }

        UUID id = UUID.fromString(args[0]);
        InventorySnapshot snapshot = plugin.getInventorySnapshotManager().getSnapshot(id);

        if (snapshot == null) {
            player.sendMessage(Colors.RED + "The inventory snapshot has expired or doesn't exist!");
        } else {
            snapshot.openTo(player);
        }
    }
}

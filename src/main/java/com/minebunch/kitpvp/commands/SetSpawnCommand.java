package com.minebunch.kitpvp.commands;

import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public SetSpawnCommand(KitPlugin plugin) {
        super("setspawn", Rank.ADMIN);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        Location spawn = player.getLocation();

        plugin.setSpawn(LocationUtil.normalize(spawn));
        plugin.getKitConfig().set("spawn", spawn);
        plugin.getKitConfig().save();

        player.sendMessage(Colors.GREEN + "Set the spawn to your current location.");
    }
}

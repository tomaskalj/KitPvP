package com.minebunch.kitpvp.commands;


import com.minebunch.core.commands.PlayerCommand;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ArenaCommand extends PlayerCommand {
    private final KitPlugin plugin;

    public ArenaCommand(KitPlugin plugin) {
        super("arena", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(Colors.RED + "Usage: /arena <subcommand> <args>");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(usageMessage);
            return;
        }

        String name = args[1];
        Arena arena = plugin.getArenaManager().getArena(name);
        String subCommand = args[0].toLowerCase();
        Location location = player.getLocation();

        location.setX(location.getBlockX() + 0.5);
        location.setY(location.getBlockY() + 3.0);
        location.setZ(location.getBlockZ() + 0.5);

        switch (subCommand) {
            case "create":
                if (arena == null) {
                    plugin.getArenaManager().createArena(name);
                    player.sendMessage(Colors.GREEN + "Created arena " + name + ".");
                } else {
                    player.sendMessage(Colors.RED + "That arena already exists.");
                }
                break;
            case "remove":
                if (arena == null) {
                    player.sendMessage(Colors.RED + "That arena doesn't exist.");
                } else {
                    plugin.getArenaManager().removeArena(arena);
                    player.sendMessage(Colors.GREEN + "Removed arena " + name + ".");
                }
                break;
            case "first":
                if (arena == null) {
                    player.sendMessage(Colors.RED + "That arena doesn't exist.");
                } else {
                    arena.setFirstSpawn(location);
                    player.sendMessage(Colors.GREEN + "Set the first team spawn to your location.");
                }
                break;
            case "second":
                if (arena == null) {
                    player.sendMessage(Colors.RED + "That arena doesn't exist.");
                } else {
                    arena.setSecondSpawn(location);
                    player.sendMessage(Colors.GREEN + "Set the second team spawn to your location.");
                }
                break;
            case "tp":
                if (arena == null) {
                    player.sendMessage(Colors.RED + "That arena doesn't exist.");
                } else {
                    player.teleport(arena.getFirstSpawn());
                    player.sendMessage(Colors.GREEN + "Teleported to the arena.");
                }
                break;
            default:
                player.sendMessage(usageMessage);
                break;
        }
    }
}

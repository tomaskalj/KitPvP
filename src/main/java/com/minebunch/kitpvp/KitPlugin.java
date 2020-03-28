package com.minebunch.kitpvp;

import com.minebunch.core.utils.scoreboard.ScoreboardAPI;
import com.minebunch.core.utils.storage.Config;
import com.minebunch.kitpvp.commands.AcceptCommand;
import com.minebunch.kitpvp.commands.ArenaCommand;
import com.minebunch.kitpvp.commands.DuelCommand;
import com.minebunch.kitpvp.commands.EditCreditsCommand;
import com.minebunch.kitpvp.commands.InventorySnapshotCommand;
import com.minebunch.kitpvp.commands.KitCommand;
import com.minebunch.kitpvp.commands.LeaderboardCommand;
import com.minebunch.kitpvp.commands.ResetStatsCommand;
import com.minebunch.kitpvp.commands.SetSpawnCommand;
import com.minebunch.kitpvp.commands.SpawnCommand;
import com.minebunch.kitpvp.commands.StatsCommand;
import com.minebunch.kitpvp.kits.technical.KitManager;
import com.minebunch.kitpvp.listeners.FfaListener;
import com.minebunch.kitpvp.listeners.MatchListener;
import com.minebunch.kitpvp.listeners.MatchmakingListener;
import com.minebunch.kitpvp.listeners.PlayerListener;
import com.minebunch.kitpvp.listeners.SoupListener;
import com.minebunch.kitpvp.listeners.SpawnListener;
import com.minebunch.kitpvp.listeners.WorldListener;
import com.minebunch.kitpvp.listeners.fix.StrengthFixListener;
import com.minebunch.kitpvp.managers.ArenaManager;
import com.minebunch.kitpvp.managers.InventorySnapshotManager;
import com.minebunch.kitpvp.managers.KitPlayerManager;
import com.minebunch.kitpvp.managers.LeaderboardManager;
import com.minebunch.kitpvp.managers.MatchManager;
import com.minebunch.kitpvp.managers.MatchmakingManager;
import com.minebunch.kitpvp.scoreboard.KitPvPScoreboard;
import com.minebunch.kitpvp.storage.KitPvPConfig;
import com.minebunch.kitpvp.util.gui.GuiFolder;
import com.minebunch.kitpvp.util.gui.GuiListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

// TODO: cleanup gui and clickable item system
@Getter
public class KitPlugin extends JavaPlugin {
    @Getter
    private static KitPlugin instance;

    private List<GuiFolder> folders;

    private Config kitConfig;

    private KitPlayerManager playerManager;
    private KitManager kitManager;
    private MatchManager matchManager;
    private MatchmakingManager matchmakingManager;
    private ArenaManager arenaManager;
    private InventorySnapshotManager inventorySnapshotManager;
    private LeaderboardManager leaderboardManager;

    @Setter
    private Location spawn;
    private int spawnRadius;

    @Override
    public void onEnable() {
        instance = this;

        kitConfig = new KitPvPConfig(this);

        folders = new ArrayList<>();

        playerManager = new KitPlayerManager(this);
        kitManager = new KitManager();
        matchManager = new MatchManager();
        matchmakingManager = new MatchmakingManager(this);
        arenaManager = new ArenaManager(this);
        inventorySnapshotManager = new InventorySnapshotManager();
        leaderboardManager = new LeaderboardManager(this);

        spawn = kitConfig.getLocation("spawn");
        spawnRadius = kitConfig.getInt("radius");

        registerCommands(
                new DuelCommand(this), new AcceptCommand(this), new ArenaCommand(this),
                new SetSpawnCommand(this), new SpawnCommand(this), new InventorySnapshotCommand(this),
                new StatsCommand(this), new LeaderboardCommand(this), new KitCommand(this),
                new EditCreditsCommand(this), new ResetStatsCommand(this)
        );

        registerListeners(
                new PlayerListener(this), new GuiListener(this), new MatchListener(this),
                new MatchmakingListener(this), new FfaListener(this), new SpawnListener(this),
                new SoupListener(), new StrengthFixListener(), new WorldListener()
        );

        new ScoreboardAPI(this, new KitPvPScoreboard(this), 5);

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> leaderboardManager.updateLeaderboards(), 20 * 5L, 20 * 60L * 5);
    }

    @Override
    public void onDisable() {
        kitConfig.save();
        arenaManager.saveArenas();
        playerManager.savePlayers();

        clearEntities();
    }

    private void clearEntities() {
        int removed = 0;

        for (World world : getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Player || entity instanceof Hanging) {
                    continue;
                }

                entity.getLocation().getChunk().load();
                entity.remove();
                removed++;
            }
        }

        getLogger().info("Removed " + removed + " entities.");
    }

    private void registerCommands(Command... commands) {
        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            final boolean accessible = commandMapField.isAccessible();

            commandMapField.setAccessible(true);

            CommandMap commandMap = (CommandMap) commandMapField.get(getServer());

            for (Command command : commands) {
                commandMap.register(command.getName(), getName(), command);
            }

            commandMapField.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().severe("An error occurred while registering commands.");
            e.printStackTrace();
        }
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}

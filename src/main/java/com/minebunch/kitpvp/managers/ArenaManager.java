package com.minebunch.kitpvp.managers;

import com.minebunch.core.utils.storage.Config;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.arena.Arena;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ArenaManager {
    private final Set<Arena> arenas = new HashSet<>();
    private final Config config;

    public ArenaManager(KitPlugin plugin) {
        this.config = new Config(plugin, "arenas");

        config.getKeys().forEach(arenaName -> {
            Arena arena = new Arena(arenaName);

            arena.setFirstSpawn(config.getLocation(arenaName + ".first-spawn"));
            arena.setSecondSpawn(config.getLocation(arenaName + ".second-spawn"));

            arenas.add(arena);
        });
    }

    public void createArena(String arenaName) {
        arenas.add(new Arena(arenaName));
    }

    public Arena getArena(String arenaName) {
        return arenas.stream().filter(arena -> arena.getName().equals(arenaName)).findAny().orElse(null);
    }

    private List<Arena> getAvailableArenas() {
        return arenas.stream().filter(Arena::isAvailable).collect(Collectors.toList());
    }

    public Arena getRandomArena() {
        List<Arena> availableArenas = getAvailableArenas();
        return availableArenas.size() == 0 ? null : availableArenas.get(ThreadLocalRandom.current().nextInt(availableArenas.size()));
    }

    public void removeArena(Arena arena) {
        arenas.remove(arena);
    }

    public void saveArenas() {
        config.clear();

        arenas.stream().filter(Arena::isAvailable).forEach(arena -> {
                    String name = arena.getName();
                    config.set(name + ".first-spawn", arena.getFirstSpawn());
                    config.set(name + ".second-spawn", arena.getSecondSpawn());
                }
        );

        config.save();
    }
}

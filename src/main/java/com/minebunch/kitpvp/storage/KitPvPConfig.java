package com.minebunch.kitpvp.storage;

import com.google.common.collect.ImmutableMap;
import com.minebunch.core.utils.storage.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class KitPvPConfig extends Config {
    public KitPvPConfig(JavaPlugin plugin) {
        super(plugin, "kitpvp");

        addDefaults(ImmutableMap.<String, Object>builder()
                .put("spawn", Bukkit.getWorlds().get(0).getSpawnLocation())
                .put("radius", 64)
                .build()
        );
        copyDefaults();
    }
}

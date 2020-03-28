package com.minebunch.kitpvp.util;

import com.minebunch.kitpvp.KitPlugin;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class LocationUtil {
    public boolean isOutsideSpawn(Location location) {
        int radius = KitPlugin.getInstance().getSpawnRadius();
        return location.getX() > radius || location.getZ() > radius || location.getX() < -radius || location.getZ() < -radius;
    }

    public Location normalize(Location location) {
        location.setX(location.getBlockX() + 0.5);
        location.setY(location.getBlockY() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);
        return location.clone();
    }
}

package com.minebunch.kitpvp.arena;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Arena {
    private final String name;
    private Location firstSpawn;
    private Location secondSpawn;

    public boolean isAvailable() {
        return firstSpawn != null && secondSpawn != null;
    }
}

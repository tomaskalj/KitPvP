package com.minebunch.kitpvp.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.minebunch.kitpvp.match.inventory.InventorySnapshot;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InventorySnapshotManager {
    private final Cache<UUID, InventorySnapshot> snapshots = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();

    public void cacheSnapshot(InventorySnapshot snapshot) {
        snapshots.put(snapshot.getId(), snapshot);
    }

    public InventorySnapshot getSnapshot(UUID id) {
        return snapshots.getIfPresent(id);
    }
}

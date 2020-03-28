package com.minebunch.kitpvp.kits.technical;

import java.util.EnumMap;
import java.util.Map;

public class KitManager {
    private final EnumMap<KitType, Map<Class<? extends Kit>, Kit>> kitsByType = new EnumMap<>(KitType.class);

    public KitManager() {
        kitsByType.put(KitType.FFA, KitFactory.loadFfaKits());
        kitsByType.put(KitType.MATCHMAKING, KitFactory.loadMatchmakingKits());
    }

    public Kit getKitByClass(KitType type, Class<? extends Kit> clazz) {
        return kitsByType.get(type).get(clazz);
    }

    public Kit getKitByName(KitType type, String name) {
        Map<Class<? extends Kit>, Kit> kits = kitsByType.get(type);
        return kits.values().stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public Map<Class<? extends Kit>, Kit> getKitsByType(KitType type) {
        return kitsByType.get(type);
    }
}

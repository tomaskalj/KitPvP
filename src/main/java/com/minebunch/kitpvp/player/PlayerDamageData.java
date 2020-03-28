package com.minebunch.kitpvp.player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;

public class PlayerDamageData {
    private final Map<UUID, Double> attackerDamage = new HashMap<>();

    public void put(UUID attackerId, double dmg) {
        attackerDamage.put(attackerId, attackerDamage.getOrDefault(attackerId, 0.0) + dmg);
    }

    public void clear() {
        attackerDamage.clear();
    }

    public double total() {
        double total = 0.0;

        for (Map.Entry<UUID, Double> entry : attackerDamage.entrySet()) {
            if (Bukkit.getPlayer(entry.getKey()) != null) {
                total += entry.getValue();
            }
        }

        return total;
    }

    public Map<UUID, Double> sortedMap() {
        return attackerDamage.entrySet().stream()
                .filter(entry -> Bukkit.getPlayer(entry.getKey()) != null)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}

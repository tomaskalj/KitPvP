package com.minebunch.kitpvp.kits.technical;

import java.util.EnumMap;
import java.util.Map;

public class KitStats {
    private final Map<KitStat, Integer> amountByStat = new EnumMap<>(KitStat.class);

    public KitStats() {
        for (KitStat stat : KitStat.values()) {
            amountByStat.put(stat, stat.getDefaultValue());
        }
    }

    public int getAmountByStat(KitStat stat) {
        return amountByStat.get(stat);
    }

    public void setStatAmount(KitStat stat, int amount) {
        amountByStat.put(stat, amount);
    }
}

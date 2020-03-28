package com.minebunch.kitpvp.player;

import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitStats;
import com.minebunch.kitpvp.kits.technical.KitType;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class PlayerStats {
    private final Map<Class<? extends Kit>, KitStats> statsByKit = new HashMap<>();
    @Getter
    @Setter
    private int kills, deaths, killStreak, highestKillStreak, wins, credits;

    public PlayerStats() {
        loadKitStats();
    }

    private void loadKitStats() {
        KitPlugin.getInstance().getKitManager().getKitsByType(KitType.MATCHMAKING).values().forEach(kit -> statsByKit.put(kit.getClass(), new KitStats()));
    }

    public double getKillDeathRatio() {
        return kills == 0 ? 0.0 : deaths == 0 ? kills : Math.round(((double) kills / deaths) * 10.0) / 10.0;
    }

    public boolean handleDeath() {
        boolean newRecordStreak = killStreak > highestKillStreak;

        if (newRecordStreak) {
            highestKillStreak = killStreak;
        }

        killStreak = 0;
        deaths++;

        return newRecordStreak;
    }

    public void handleKill() {
        kills++;
        killStreak++;
    }

    public void reset() {
        kills = 0;
        deaths = 0;
        killStreak = 0;
        highestKillStreak = 0;
        wins = 0;
        credits = 0;
        statsByKit.clear();
        loadKitStats();
    }

    public KitStats getStatsByKit(Class<? extends Kit> clazz) {
        return statsByKit.get(clazz);
    }

    public Map<Class<? extends Kit>, KitStats> getStatsByKitMap() {
        return statsByKit;
    }
}

package com.minebunch.kitpvp.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.minebunch.core.player.PlayerProfile;
import com.minebunch.core.storage.database.MongoRequest;
import com.minebunch.core.utils.message.Colors;
import com.minebunch.core.utils.time.timer.Timer;
import com.minebunch.core.utils.time.timer.impl.DoubleTimer;
import com.minebunch.core.utils.time.timer.impl.IntegerTimer;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.ffa.impl.Standard;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitStat;
import com.minebunch.kitpvp.kits.technical.KitType;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;

@Getter
public class KitPlayer extends PlayerProfile {
    private final Cache<UUID, String> duelRequests = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).build();
    private final PlayerStats stats = new PlayerStats();
    private final PlayerDamageData damageData = new PlayerDamageData();
    private final Timer pearlTimer = new DoubleTimer(16);
    private final Timer duelTimer = new IntegerTimer(TimeUnit.SECONDS, 30);
    @Setter
    private PlayerState state = PlayerState.SPAWN;
    @Setter
    private Class<? extends Kit> selectedKit;
    @Setter
    private UUID currentMatchId;
    @Setter
    private boolean awaitingTeleport;

    public KitPlayer(UUID id) {
        super(id, "kit_players");
    }

    public boolean isInSpawnState() {
        return state == PlayerState.SPAWN || state == PlayerState.MATCHMAKING;
    }

    public void loseSpawnProtection(Player player) {
        state = PlayerState.FFA;
        player.sendMessage(Colors.RED + "You no longer have spawn protection.");

        if (selectedKit == null) {
            player.closeInventory();
            KitPlugin.getInstance().getKitManager().getKitByClass(KitType.FFA, Standard.class).apply(player);
        }
    }

    @Override
    public MongoRequest serialize() {
        Document document = new Document("_id", getId())
                .append("kills", stats.getKills())
                .append("deaths", stats.getDeaths())
                .append("highest_kill_streak", stats.getHighestKillStreak())
                .append("wins", stats.getWins())
                .append("credits", stats.getCredits());

        Document elo = new Document();

        stats.getStatsByKitMap().forEach((kitClass, stats) -> {
            Kit kit = KitPlugin.getInstance().getKitManager().getKitByClass(KitType.MATCHMAKING, kitClass);
            elo.append(kit.getName(), stats.getAmountByStat(KitStat.ELO));
        });

        document.append("elo", elo);

        return MongoRequest.newRequest("kit_players", getId()).put("stats", document);
    }

    @Override
    public void deserialize(Document document) {
        document = document.get("stats", Document.class);

        stats.setKills(document.getInteger("kills"));
        stats.setDeaths(document.getInteger("deaths"));
        stats.setHighestKillStreak(document.getInteger("highest_kill_streak"));
        stats.setWins(document.getInteger("wins"));
        stats.setCredits(document.getInteger("credits"));

        Document eloDocument = document.get("elo", Document.class);

        eloDocument.forEach((kitName, elo) -> {
            Kit kit = KitPlugin.getInstance().getKitManager().getKitByName(KitType.MATCHMAKING, kitName);
            stats.getStatsByKit(kit.getClass()).setStatAmount(KitStat.ELO, (Integer) elo);
        });
    }
}

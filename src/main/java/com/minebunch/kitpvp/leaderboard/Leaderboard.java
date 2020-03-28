package com.minebunch.kitpvp.leaderboard;

import com.minebunch.core.CorePlugin;
import com.minebunch.core.player.CoreProfile;
import com.minebunch.core.player.rank.Rank;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitStat;
import com.minebunch.kitpvp.kits.technical.KitType;
import com.minebunch.kitpvp.player.KitPlayer;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

@RequiredArgsConstructor
public class Leaderboard {
    private final KitPlugin plugin;
    private final Class<? extends Kit> trackedKitClass;
    private final List<LeaderboardEntry> entries = new ArrayList<>();

    public synchronized void update() {
        List<LeaderboardEntry> updatedEntries = new ArrayList<>();
        List<UUID> addedUUIDs = new ArrayList<>();

        for (KitPlayer kitPlayer : plugin.getPlayerManager().getPlayers().values()) {
            CoreProfile coreProfile = CorePlugin.getInstance().getProfileManager().getProfile(kitPlayer.getId());

            if (coreProfile == null) {
                return;
            }

            addedUUIDs.add(kitPlayer.getId());

            String displayName = coreProfile.getChatFormat();
            int elo = kitPlayer.getStats().getStatsByKit(trackedKitClass).getAmountByStat(KitStat.ELO);

            updatedEntries.add(new LeaderboardEntry(coreProfile.getName(), displayName, kitPlayer.getId(), elo));
        }

        MongoCursor<Document> allDocuments = CorePlugin.getInstance().getMongoStorage().getAllDocuments("kit_players");

        while (allDocuments.hasNext()) {
            Document document = allDocuments.next();
            UUID uuid = document.get("_id", UUID.class);

            document = document.get("stats", Document.class);

            if (document == null) {
                continue;
            }

            if (addedUUIDs.contains(uuid)) {
                continue;
            }

            Document coreDocument = CorePlugin.getInstance().getMongoStorage().getDocument("player", uuid);

            if (coreDocument == null) {
                continue;
            }

            String name = coreDocument.getString("name");

            if (name == null) {
                continue;
            }

            String rankName = coreDocument.getString("rank_name");
            Rank rank = Rank.getByName(rankName);

            if (rank == null) {
                continue;
            }

            String displayName = rank.getFormat() + name;

            Kit kit = plugin.getKitManager().getKitByClass(KitType.MATCHMAKING, trackedKitClass);
            Document eloDocument = document.get("elo", Document.class);

            if (eloDocument == null) {
                continue;
            }

            int elo = eloDocument.getInteger(kit.getName(), 1200);

            updatedEntries.add(new LeaderboardEntry(name, displayName, uuid, elo));
        }

        updatedEntries.sort(EntryComparator.getInstance());

        entries.clear();
        entries.addAll(updatedEntries);
    }

    public List<LeaderboardEntry> getEntries() {
        synchronized (entries) {
            return entries;
        }
    }

    public List<LeaderboardEntry> getEntries(int index) {
        synchronized (entries) {
            if (entries.isEmpty()) {
                return Collections.emptyList();
            }

            if (entries.size() < index) {
                return entries;
            }

            return entries.subList(index, Math.min(index + 20, entries.size()));
        }
    }
}

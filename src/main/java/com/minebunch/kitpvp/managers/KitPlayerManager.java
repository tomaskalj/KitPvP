package com.minebunch.kitpvp.managers;

import com.minebunch.core.utils.player.PlayerUtil;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.items.ItemHotbar;
import com.minebunch.kitpvp.player.KitPlayer;
import com.minebunch.kitpvp.player.PlayerState;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class KitPlayerManager {
    private final Map<UUID, KitPlayer> players = new ConcurrentHashMap<>();
    private final KitPlugin plugin;

    public void createPlayer(UUID uuid) {
        KitPlayer kp = new KitPlayer(uuid);
        kp.load();
        players.put(uuid, kp);
    }

    public KitPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public KitPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    public void savePlayers() {
        for (KitPlayer kitPlayer : players.values()) {
            kitPlayer.save(false);
        }
    }

    public void resetPlayer(Player player, boolean teleport) {
        KitPlayer kp = getPlayer(player);

        kp.setState(PlayerState.SPAWN);
        kp.setSelectedKit(null);

        PlayerUtil.clearPlayer(player);
        ItemHotbar.SPAWN_ITEMS.apply(player);

        if (teleport) {
            player.teleport(plugin.getSpawn());
        }
    }
}

package com.minebunch.kitpvp.managers;

import com.minebunch.kitpvp.match.Match;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MatchManager {
    private final Map<UUID, Match> ongoingMatches = new HashMap<>();

    public void addMatch(Match match) {
        ongoingMatches.put(match.getId(), match);
    }

    public Match getMatch(UUID id) {
        return ongoingMatches.get(id);
    }

    public void removeMatch(Match match) {
        ongoingMatches.remove(match.getId());
    }
}

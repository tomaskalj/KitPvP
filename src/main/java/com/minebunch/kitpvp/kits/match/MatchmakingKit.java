package com.minebunch.kitpvp.kits.match;

import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitType;
import lombok.Getter;

public abstract class MatchmakingKit extends Kit {
    @Getter
    private final int slot;

    public MatchmakingKit(String description, int slot) {
        super(KitType.MATCHMAKING, description);
        this.slot = slot;
    }
}

package com.minebunch.kitpvp.kits.technical;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KitStat {
    KILLS(0),
    DEATHS(0),
    ELO(1200);

    private final int defaultValue;
}

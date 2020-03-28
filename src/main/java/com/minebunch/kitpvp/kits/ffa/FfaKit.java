package com.minebunch.kitpvp.kits.ffa;

import com.minebunch.kitpvp.kits.technical.Kit;
import com.minebunch.kitpvp.kits.technical.KitType;

public abstract class FfaKit extends Kit {
    public FfaKit(String description) {
        super(KitType.FFA, description);
    }
}

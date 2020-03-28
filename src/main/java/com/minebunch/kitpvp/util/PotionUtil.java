package com.minebunch.kitpvp.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.WordUtils;
import org.bukkit.potion.PotionEffectType;

@UtilityClass
public class PotionUtil {

    // TODO: add more
    public String getFriendlyName(PotionEffectType type) {
        if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
            return "Strength";
        }

        return WordUtils.capitalizeFully(type.getName().toLowerCase().replace("_", " "));
    }
}

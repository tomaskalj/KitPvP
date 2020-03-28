package com.minebunch.kitpvp.kits.technical;

import com.minebunch.core.utils.object.ClassUtil;
import com.minebunch.kitpvp.KitPlugin;
import com.minebunch.kitpvp.kits.ffa.impl.Standard;
import com.minebunch.kitpvp.kits.match.impl.NoDebuff;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
class KitFactory {
    Map<Class<? extends Kit>, Kit> loadFfaKits() {
        return loadKits(Standard.class.getPackage());
    }

    Map<Class<? extends Kit>, Kit> loadMatchmakingKits() {
        return loadKits(NoDebuff.class.getPackage());
    }

    private Map<Class<? extends Kit>, Kit> loadKits(Package pkg) {
        Map<Class<? extends Kit>, Kit> kits = new HashMap<>();
        Collection<Class<?>> kitClasses = ClassUtil.getClassesInPackage(KitPlugin.getInstance(), pkg.getName());

        try {
            for (Class<?> kitClass : kitClasses) {
                if (Kit.class.isAssignableFrom(kitClass)) {
                    Class<? extends Kit> kitBaseClass = kitClass.asSubclass(Kit.class);
                    kits.put(kitBaseClass, kitBaseClass.newInstance());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading kits", e);
        }

        return kits;
    }
}

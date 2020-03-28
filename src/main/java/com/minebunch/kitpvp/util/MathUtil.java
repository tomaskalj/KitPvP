package com.minebunch.kitpvp.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {
    public double roundToHalves(double d) {
        return Math.ceil(d) / 2.0;
    }

    public boolean isWithin(int i, int j, int range) {
        return Math.abs(i - j) <= range;
    }
}

package com.minebunch.kitpvp.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class StringUtil {
    /*
     * Splits a String when the length of a line to a space reaches 40 characters or more,
     * and adds the last ChatColors onto the next line because Minecraft won't render colors from prior lines
     */
    public String[] separateDescription(String desc) {
        StringBuilder builder = new StringBuilder(desc);
        int i = 0;

        while (i + 40 < builder.length() && (i = builder.lastIndexOf(" ", i + 40)) != -1) {
            String last = builder.substring(0, i + 1);
            builder.replace(i, i + 1, "\n" + ChatColor.getLastColors(last));
        }

        return builder.toString().split("\n");
    }
}

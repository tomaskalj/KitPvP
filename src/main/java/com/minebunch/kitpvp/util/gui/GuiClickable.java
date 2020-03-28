package com.minebunch.kitpvp.util.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface GuiClickable extends GuiItem {
    void onClick(InventoryClickEvent event);
}

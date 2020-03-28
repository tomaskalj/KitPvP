package com.minebunch.kitpvp.util.gui;

import com.minebunch.kitpvp.KitPlugin;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Getter
public class GuiFolder {
    private String name;
    private int size;
    private Inventory inventory;
    private GuiPage currentPage;

    public GuiFolder(String name, int size) {
        this.name = name;
        this.size = size;
        this.inventory = KitPlugin.getInstance().getServer().createInventory(null, size, name);

        KitPlugin.getInstance().getFolders().add(this);
    }

    public void openGui(Player player) {
        player.closeInventory();
        player.openInventory(inventory);
    }

    public void setCurrentPage(GuiPage currentPage) {
        this.currentPage = currentPage;
        this.currentPage.updatePage();
    }

    public void setSize(int size) {
        this.size = size;
        this.inventory = KitPlugin.getInstance().getServer().createInventory(null, size, name);
    }
}

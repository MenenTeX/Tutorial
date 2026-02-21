package org.menentex.Tutorial.DataManager.Gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiInventoryHolder implements InventoryHolder {

    private final String guiName;
    private final String inventoryKey;

    public GuiInventoryHolder(String guiName, String inventoryKey) {
        this.guiName = guiName;
        this.inventoryKey = inventoryKey;
    }

    public String getGuiName() {
        return guiName;
    }

    public String getInventoryKey() {
        return inventoryKey;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

}
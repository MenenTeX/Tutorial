package org.menentex.Tutorial.DataManager.Gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public record GuiInventoryHolder(String guiName, String inventoryKey) implements InventoryHolder {

    @Override
    public Inventory getInventory() {
        return null;
    }

}
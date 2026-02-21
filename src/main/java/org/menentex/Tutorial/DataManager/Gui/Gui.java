package org.menentex.Tutorial.DataManager.Gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Set;

public interface Gui {

    String getGuiName();

    Inventory getInventory(String getKey);

    void addInventory(String key, Inventory inventory);

    Set<String> getKeys();

    long countActionLists();

    void open(Player player, String key);

}
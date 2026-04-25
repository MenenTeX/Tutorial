package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class ClearInventoryEvent extends TutorialEvent {

    public ClearInventoryEvent(int index){
        super(index);
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        player.getInventory().clear();
    }

    @Override
    public String getDisplayName(){
        return "ClearInventory";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
    }

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.CAMPFIRE,
                "&6ClearInventory",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex()
                ), false);
    }

}

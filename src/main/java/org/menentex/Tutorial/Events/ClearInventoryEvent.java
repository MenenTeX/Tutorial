package org.menentex.Tutorial.Events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Utils.Utils;

public class ClearInventoryEvent extends TutorialEvents{

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

}

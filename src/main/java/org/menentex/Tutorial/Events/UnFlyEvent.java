package org.menentex.Tutorial.Events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Utils.Utils;

public class UnFlyEvent extends TutorialEvents{

    public UnFlyEvent(int index){
        super(index);
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        player.setAllowFlight(false);
    }

    @Override
    public String getDisplayName(){
        return "UnFly";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
    }

}

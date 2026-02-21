package org.menentex.Tutorial.Events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Utils.Utils;

public class FlyEvent extends TutorialEvents{

    public FlyEvent(int index){
        super(index);
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        player.setAllowFlight(true);
    }

    @Override
    public String getDisplayName(){
        return "Fly";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
    }

}

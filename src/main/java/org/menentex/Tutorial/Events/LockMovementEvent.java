package org.menentex.Tutorial.Events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.DataManager.Gui.EventListMananger;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

public class LockMovementEvent extends TutorialEvents{

    public LockMovementEvent(int index){
        super(index);
    }

    @Override
    public void execute(Player player) {
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        EventListMananger e = Main.getInstance().getEventListMananger();
        e.addMovementLock(player);
    }

    @Override
    public String getDisplayName(){
        return "LockMovement";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
    }

}

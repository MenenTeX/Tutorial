package org.menentex.Tutorial.Events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Utils.Utils;

public class ClearChatEvent extends TutorialEvents{

    public ClearChatEvent(int index) {
        super(index);
    }

    @Override
    public void execute(Player player) {
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        for (int i = 0 ; i <= 50; i++){
            player.sendMessage("");
        }
    }

    @Override
    public String getDisplayName(){
        return "Clear";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
    }
}

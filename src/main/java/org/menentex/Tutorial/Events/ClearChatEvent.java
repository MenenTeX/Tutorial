package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

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

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.COBWEB,
                "&6Clear Chat",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex()
                ));
    }
}

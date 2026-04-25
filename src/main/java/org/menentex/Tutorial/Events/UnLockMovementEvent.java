package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.DataManager.EventListMananger;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class UnLockMovementEvent extends TutorialEvent {

    public UnLockMovementEvent(int index){
        super(index);
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        EventListMananger e = Main.getInstance().getEventListMananger();
        if (e.isMovementLock(player))
            e.removeMovementLock(player);
    }

    @Override
    public String getDisplayName(){
        return "UnLockMovement";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
    }

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.GOLDEN_BOOTS,
                "&6UnLockMovement",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex()
                ),false, ItemFlag.HIDE_ATTRIBUTES);
    }

}

package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.DataManager.EventListMananger;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class UnGodEvent extends TutorialEvent {


    public UnGodEvent(int index) {
        super(index);
    }

    @Override
    public void execute(Player player) {
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        EventListMananger e = Main.getInstance().getEventListMananger();
        if (e.isDamageProtection(player))
            e.removeDamageProtection(player);
    }

    @Override
    public String getDisplayName() {
        return "UnGodMode";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        if (getPermission() != null) {
            section.set("permission", getPermission());
        }
    }

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.DRAGON_BREATH,
                "&6UnGod",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex()
                ), false);
    }
}

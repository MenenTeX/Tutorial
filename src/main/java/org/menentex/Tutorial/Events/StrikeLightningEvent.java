package org.menentex.Tutorial.Events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class StrikeLightningEvent extends TutorialEvent {

    private final Location location;

    public StrikeLightningEvent(int index, Location location){
        super(index);
        this.location = location;
    }

    @Override
    public void execute(Player player) {

        location.getWorld().strikeLightning(location);

    }

    @Override
    public String getDisplayName() {
        return "StrikeLightning";
    }

    @Override
    public void serialize(ConfigurationSection section) {

    }

    @Override
    public ItemStack createItemForInv() {
        String world = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return Utils.itemCreate(Material.LIGHTNING_ROD,
                "&6Strike Lightning",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex(),
                        "&#3F9AAEWorld: &#3F9AAE: &#F6CE71" + world,
                        "&#3F9AAEX: &#3F9AAE: &#F6CE71" + x,
                        "&#3F9AAEY: &#3F9AAE: &#F6CE71" + y,
                        "&#3F9AAEZ: &#3F9AAE: &#F6CE71" + z
                ), false);
    }
}

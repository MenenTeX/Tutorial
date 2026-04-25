package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class DelayEvent extends TutorialEvent {

    private final long duration;


    public DelayEvent(int index, long duration){
        super(index);
        this.duration = duration;
    }

    public long getDuration(){
        return duration;
    }

    @Override
    public long getBlockingTicks() {
        return (duration);
    }

    @Override
    public void execute(Player player) {

    }

    @Override
    public String getDisplayName(){
        return "Delay";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("duration", duration);
    }

    public static DelayEvent deserialize(int index, ConfigurationSection section) {
        long duration = section.getLong("duration", 0L);
        return new DelayEvent(index, duration);
    }

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.CLOCK,
                "&6Delay",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex(),
                        "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + Utils.formatTick(getDuration())
                ), false);
    }

}

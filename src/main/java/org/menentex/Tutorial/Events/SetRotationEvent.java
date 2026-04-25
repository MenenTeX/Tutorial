package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class SetRotationEvent extends TutorialEvent {

    private final float yaw;
    private final float pitch;

    public SetRotationEvent(int index, float yaw, float pitch){
        super(index);
        this.yaw = yaw;
        this.pitch = pitch;
    }


    @Override
    public void execute(Player player) {
        player.setRotation(yaw, pitch);
    }

    @Override
    public String getDisplayName() {
        return "SetRotation";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("yaw", yaw);
        section.set("pitch", pitch);
    }

    public SetRotationEvent deserialize(int index, ConfigurationSection section){
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        return new SetRotationEvent(index, yaw, pitch);
    }

    @Override
    public ItemStack createItemForInv() {
        return Utils.itemCreate(Material.ENDER_EYE,
                "&6SetRotation",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + index,
                        "&#3F9AAEYaw: &#3F9AAE: &#F6CE71" + Float.toString(yaw).substring(0, Math.min(Float.toString(yaw).length(), 6)),
                        "&#3F9AAEPitch: &#3F9AAE: &#F6CE71" + Float.toString(pitch).substring(0, Math.min(Float.toString(pitch).length(), 5))
                ), false);
    }
}

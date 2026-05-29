package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class PushEvent extends TutorialEvent {

    private final Utils.Direction direction;
    private final float multiply;
    private final float strength;

    public PushEvent(int index, Utils.Direction direction, float multiply, float strength){
        super(index);
        this.direction = direction;
        this.multiply = multiply;
        this.strength = strength;
    }

    @Override
    public void execute(Player player) {
        player.setVelocity(Utils.getVector(direction, multiply, strength));
    }

    @Override
    public String getDisplayName() {
        return "Push";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("direction", direction.name());
        section.set("multiply", multiply);
        section.set("strength", strength);
    }

    public static PushEvent deserialize(int index, ConfigurationSection section){

        Utils.Direction direction = Utils.nameToDirection(section.getString("direction"));
        if (direction == null) return null;

        float multiply = section.getLong("multiply");
        float strength = section.getLong("strength");

        return new PushEvent(index, direction, multiply, strength);
    }

    @Override
    public ItemStack createItemForInv() {
        return Utils.itemCreate(Material.COMPASS,
                "&6Push",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + index,
                        "&#3F9AAEDirection &#3F9AAE: &#F6CE71" + direction.name(),
                        "&#3F9AAEStrength &#3F9AAE: &#F6CE71" + strength,
                        "&#3F9AAEMultiply &#3F9AAE: &#F6CE71" + multiply
                ), false);
    }
}

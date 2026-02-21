package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.HashMap;

public class GiveItemEvent extends TutorialEvents{

    private final Material material;
    private final int amount;

    public GiveItemEvent(int index, Material material, int amount){
        super(index);
        this.material = material;
        this.amount = amount;
    }

    public String getItemName() {
        return material.name();
    }

    public int getAmount() { return amount; }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void execute(Player player) {
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;

        ItemStack item = new ItemStack(material, amount);

        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);

        if (!leftover.isEmpty()) {
            for (ItemStack stack : leftover.values()) {
                if (stack != null && stack.getAmount() > 0) {
                    player.getWorld().dropItemNaturally(player.getLocation(), stack);
                }
            }
        }
    }


    @Override
    public String getDisplayName(){
        return "GiveItem";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
    }

    public static GiveItemEvent deserialize(int index, ConfigurationSection section) {
        String materialName = section.getString("material");
        if (materialName == null) return null;

        Material material = Material.getMaterial(materialName);
        if (material == null) return null;

        int amount = section.getInt("amount", 1);

        return new GiveItemEvent(index, material, amount);
    }


}

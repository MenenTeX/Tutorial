package org.menentex.Tutorial.Events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.DataManager.WaitingEventData.WaitActionList;
import org.menentex.Tutorial.DataManager.WaitingEventData.WaitRegionData;
import org.menentex.Tutorial.DataManager.WaitingEventData.WaitRegionParticle;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class WaitRegionEnterEvent extends TutorialEvent implements ConditionalEvent {

    private final CuboidRegion region;
    private final String guiName;

    public WaitRegionEnterEvent(int index, CuboidRegion region, String guiName) {
        super(index);
        this.region = region;
        this.guiName = guiName;
    }

    @Override
    public boolean isCompleted(Player player) {
        return false;
    }

    @Override
    public void execute(Player player) {
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        WaitActionList waitList = Main.getInstance().getWaitActionList();
        waitList.add(player, new WaitRegionData(region, guiName));
        WaitRegionParticle.getInstance().startRegionParticles(player, region, 30);
    }

    @Override
    public String getDisplayName() {
        return "WaitRegionEnter";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("world", region.getPos1().getWorld().getName());
        String keyPos1 = region.getPos1().getX() + ", " + region.getPos1().getY() + ", " + region.getPos1().getZ();
        section.set("pos1", keyPos1);
        String keyPos2 = region.getPos2().getX() + ", " + region.getPos2().getY() + ", " + region.getPos2().getZ();
        section.set("pos2", keyPos2);
    }

    public WaitRegionEnterEvent deserialize(int index, ConfigurationSection section){
        String pos1 = section.getString("pos1");
        String pos2 = section.getString("pos2");
        String worldName = section.getString("world");

        if (pos1 == null || pos2 == null || worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        String[] parts1 = pos1.split("\\s*,\\s*");
        String[] parts2 = pos2.split("\\s*,\\s*");

        if (parts1.length != 3 || parts2.length != 3) return null;

        try {
            Location pos1Loc = new Location(world,
                    Integer.parseInt(parts1[0]),
                    Integer.parseInt(parts1[1]),
                    Integer.parseInt(parts1[2]));

            Location pos2Loc = new Location(world,
                    Integer.parseInt(parts2[0]),
                    Integer.parseInt(parts2[1]),
                    Integer.parseInt(parts2[2]));

            return new WaitRegionEnterEvent(
                    index,
                    new CuboidRegion(pos1Loc, pos2Loc),
                    guiName
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public ItemStack createItemForInv() {
        return Utils.itemCreate(Material.END_CRYSTAL,
                "&6WaitRegionEnter",
                List.of(
                        "",
                        "&#3F9AAEIndex: &#F6CE71" + getIndex(),
                        "&#3F9AAEWorld: &#F6CE71" + region.getPos1().getWorld().getName(),
                        "&#3F9AAEPos1: &#F6CE71" + region.getPos1().getBlockX() + ", " + region.getPos1().getBlockY() + ", " + region.getPos1().getBlockZ(),
                        "&#3F9AAEPos2: &#F6CE71" + region.getPos2().getBlockX() + ", " + region.getPos2().getBlockY() + ", " + region.getPos2().getBlockZ()

                ), false);
    }
}

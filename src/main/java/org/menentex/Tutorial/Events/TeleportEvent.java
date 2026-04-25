package org.menentex.Tutorial.Events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class TeleportEvent extends TutorialEvent {

    private final Location location;

    public TeleportEvent(int index, Location location){
        super(index);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        player.teleport(location);
    }

    @Override
    public String getDisplayName(){
        return "Teleport";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    public static TeleportEvent deserialize(int index, ConfigurationSection section) {
        String worldName = section.getString("world");
        if (worldName == null) return null;

        org.bukkit.World world = org.bukkit.Bukkit.getWorld(worldName);
        if (world == null) return null;

        Location location = new Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch")
        );

        return new TeleportEvent(index, location);
    }

    @Override
    public ItemStack createItemForInv() {
        String world = getLocation().getWorld().getName();
        int x = getLocation().getBlockX();
        int y = getLocation().getBlockY();
        int z = getLocation().getBlockZ();
        float yaw = getLocation().getYaw();
        float pitch = getLocation().getPitch();
        return Utils.itemCreate(Material.ENDER_PEARL,
                "&6Teleport",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex(),
                        "&#3F9AAEWorld: &#3F9AAE: &#F6CE71" + world,
                        "&#3F9AAEX: &#3F9AAE: &#F6CE71" + x,
                        "&#3F9AAEY: &#3F9AAE: &#F6CE71" + y,
                        "&#3F9AAEZ: &#3F9AAE: &#F6CE71" + z,
                        "&#3F9AAEYaw: &#3F9AAE: &#F6CE71" + Float.toString(yaw).substring(0, Math.min(Float.toString(yaw).length(), 6)),
                        "&#3F9AAEPitch: &#3F9AAE: &#F6CE71" + Float.toString(pitch).substring(0, Math.min(Float.toString(pitch).length(), 5))
                ), false);
    }


}

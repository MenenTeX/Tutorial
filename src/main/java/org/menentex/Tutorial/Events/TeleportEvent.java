package org.menentex.Tutorial.Events;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Utils.Utils;

public class TeleportEvent extends TutorialEvents {

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


}

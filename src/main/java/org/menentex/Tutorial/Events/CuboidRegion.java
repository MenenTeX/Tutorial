package org.menentex.Tutorial.Events;

import org.bukkit.Location;

public class CuboidRegion {
    private final Location pos1;
    private final Location pos2;

    public CuboidRegion(Location pos1, Location pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location getCenter() {
        if (pos1 == null || pos2 == null) return null;
        if (pos1.getWorld() == null) return null;

        double centerX = (pos1.getX() + pos2.getX()) / 2.0;
        double centerY = (pos1.getY() + pos2.getY()) / 2.0;
        double centerZ = (pos1.getZ() + pos2.getZ()) / 2.0;

        return new Location(pos1.getWorld(), centerX, centerY, centerZ);
    }

    public boolean isInside(Location loc) {
        if (!loc.getWorld().equals(pos1.getWorld())) return false;

        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());

        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());

        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}

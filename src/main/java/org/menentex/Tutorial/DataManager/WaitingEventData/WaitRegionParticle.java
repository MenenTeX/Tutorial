package org.menentex.Tutorial.DataManager.WaitingEventData;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.menentex.Tutorial.Events.CuboidRegion;
import org.menentex.Tutorial.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaitRegionParticle {

    private static WaitRegionParticle instance;

    private WaitRegionParticle() {
    }
    
    public static WaitRegionParticle getInstance() {
        if (instance == null) instance = new WaitRegionParticle();
        return instance;
    }

    private final Map<UUID, BukkitTask> regionParticles = new HashMap<>();

    public void startRegionParticles(Player player, CuboidRegion region, double distance) {
        UUID id = player.getUniqueId();

        if (regionParticles.containsKey(id)) return;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (!player.isOnline()) {
                stopRegionParticles(player);
                return;
            }

            if (region.isInside(player.getLocation())) {
                stopRegionParticles(player);
                return;
            }

            spawnRegionOutlineParticles(player, region, distance);
        }, 0L, 20L);

        regionParticles.put(id, task);
    }

    public void stopRegionParticles(Player player) {
        BukkitTask task = regionParticles.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }

    public void spawnRegionOutlineParticles(Player player, CuboidRegion region, double distance) {
        Location p1 = region.getPos1();
        Location p2 = region.getPos2();
        if (p1 == null || p2 == null) return;
        if (p1.getWorld() == null || p2.getWorld() == null) return;
        if (p1.getWorld() != p2.getWorld()) return;

        int minX = Math.min(p1.getBlockX(), p2.getBlockX());
        int maxX = Math.max(p1.getBlockX(), p2.getBlockX());
        int minY = Math.min(p1.getBlockY(), p2.getBlockY());
        int maxY = Math.max(p1.getBlockY(), p2.getBlockY());
        int minZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
        int maxZ = Math.max(p1.getBlockZ(), p2.getBlockZ());

        Particle particle = Particle.REDSTONE;
        Particle.DustOptions dust = new Particle.DustOptions(Color.RED, 1.5f);

        double cxMin = minX + 0.5, cxMax = maxX + 0.5;
        double cyMin = minY + 0.5, cyMax = maxY + 0.5;
        double czMin = minZ + 0.5, czMax = maxZ + 0.5;

        double step = 0.5;

        if (player.getLocation().distanceSquared(region.getCenter()) > distance * distance) {
            return;
        }

        for (double x = cxMin; x <= cxMax + 1e-9; x += step) {
            player.spawnParticle(particle, x, cyMin, czMin, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, x, cyMin, czMax, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, x, cyMax, czMin, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, x, cyMax, czMax, 1, 0, 0, 0, 0, dust);
        }

        for (double z = czMin; z <= czMax + 1e-9; z += step) {
            player.spawnParticle(particle, cxMin, cyMin, z, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, cxMax, cyMin, z, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, cxMin, cyMax, z, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, cxMax, cyMax, z, 1, 0, 0, 0, 0, dust);
        }

        for (double y = cyMin; y <= cyMax + 1e-9; y += step) {
            player.spawnParticle(particle, cxMin, y, czMin, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, cxMin, y, czMax, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, cxMax, y, czMin, 1, 0, 0, 0, 0, dust);
            player.spawnParticle(particle, cxMax, y, czMax, 1, 0, 0, 0, 0, dust);
        }
    }
}

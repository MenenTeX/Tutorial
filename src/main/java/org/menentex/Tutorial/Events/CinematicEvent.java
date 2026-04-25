package org.menentex.Tutorial.Events;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class CinematicEvent extends TutorialEvent {

    private final Location from;
    private final Location to;
    private final float yaw;
    private final float pitch;
    private final Long duration;

    public CinematicEvent(int index, Location from, Location to, float yaw, float pitch, Long duration){
        super(index);
        this.from = from;
        this.to = to;
        this.yaw = yaw;
        this.pitch = pitch;
        this.duration = duration;
    }

    public long getBlockingTicks() {
        return duration;
    }

    @Override
    public void execute(Player player) {
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;

        if (from == null || to == null || from.getWorld() == null) return;

        GameMode oldMode = player.getGameMode();
        Location oldLoc = player.getLocation().clone();

        ArmorStand cam = from.getWorld().spawn(from, ArmorStand.class, a -> {
            a.setInvisible(true);
            a.setMarker(true);
            a.setGravity(false);
            a.setSilent(true);
            a.setInvulnerable(true);
            a.setCollidable(false);
        });

        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(cam);

        final long durationTicks = Math.max(1L, duration);
        final long period = 1L;
        final long totalSteps = (long) Math.ceil(durationTicks / (double) period);

        new BukkitRunnable() {
            long step = 0;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    cam.remove();
                    cancel();
                    return;
                }


                if (player.getGameMode() == GameMode.SPECTATOR &&
                        player.getSpectatorTarget() != cam) {
                    player.setSpectatorTarget(cam);
                }

                step++;
                double p = Math.min(1.0, step / (double) totalSteps);

                Location next = from.clone().add(
                        (to.getX() - from.getX()) * p,
                        (to.getY() - from.getY()) * p,
                        (to.getZ() - from.getZ()) * p
                );

                next.setYaw(yaw);
                next.setPitch(pitch);

                cam.teleport(next);

                if (step >= totalSteps) {

                    player.setSpectatorTarget(null);
                    player.setGameMode(oldMode);
                    player.teleport(oldLoc);

                    cam.remove();
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, period);
    }

    @Override
    public String getDisplayName() {
        return "Cinematic";
    }

    @Override
    public void serialize(ConfigurationSection section) {

        if (from == null || to == null) return;

        section.set("world", from.getWorld().getName());

        section.set("from", from.getBlockX() + "," + from.getBlockY() + "," + from.getBlockZ());
        section.set("to", to.getBlockX() + "," + to.getBlockY() + "," + to.getBlockZ());

        section.set("yaw", yaw);
        section.set("pitch", pitch);
        section.set("duration", duration);
    }

    public static CinematicEvent deserialize(int index, ConfigurationSection section) {

        String worldName = section.getString("world");
        String fromStr = section.getString("from");
        String toStr = section.getString("to");

        if (worldName == null || fromStr == null || toStr == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        String[] fromParts = fromStr.split("\\s*,\\s*");
        String[] toParts = toStr.split("\\s*,\\s*");

        if (fromParts.length != 3 || toParts.length != 3) return null;

        try {
            Location from = new Location(
                    world,
                    Double.parseDouble(fromParts[0]),
                    Double.parseDouble(fromParts[1]),
                    Double.parseDouble(fromParts[2])
            );

            Location to = new Location(
                    world,
                    Double.parseDouble(toParts[0]),
                    Double.parseDouble(toParts[1]),
                    Double.parseDouble(toParts[2])
            );

            float yaw = (float) section.getDouble("yaw", 0.0);
            float pitch = (float) section.getDouble("pitch", 0.0);
            long duration = section.getLong("duration", 1L);

            return new CinematicEvent(
                    index,
                    from,
                    to,
                    yaw,
                    pitch,
                    duration
            );

        } catch (NumberFormatException e) {
            return null;
        }
    }


    @Override
    public ItemStack createItemForInv() {
        return Utils.itemCreate(Material.REDSTONE_TORCH,
                "&6Camera Cinematic",
                List.of(
                        "",
                        "&#3F9AAEIndex : &#F6CE71" + getIndex(),
                        "&#3F9AAEWorld : &#F6CE71" + from.getWorld().getName(),
                        "&#3F9AAEFrom : &#F6CE71" + from.getX() + ", " + from.getY() + ", " + from.getZ(),
                        "&#3F9AAEto : &#F6CE71" + to.getBlockX() + ", " + to.getBlockY() + ", " + to.getBlockZ(),
                        "&#3F9AAEDuration : &#F6CE71" + Utils.formatTick(duration)
                ), false);
    }
}

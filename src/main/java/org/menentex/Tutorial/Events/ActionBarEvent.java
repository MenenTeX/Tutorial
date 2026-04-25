package org.menentex.Tutorial.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActionBarEvent extends TutorialEvent {

    private final String message;
    private final long duration;

    private static final Map<UUID, BukkitTask> TASKS = new HashMap<>();

    public ActionBarEvent(int index, String message, long duration){
        super(index);
        this.message = message;
        this.duration = duration;
    }

    public String getMessage(){
        return message;
    }

    public long getDuration(){
        return duration;
    }

    @Override
    public void execute(Player player){

        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm))
            return;

        String finalMessage = message;

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            finalMessage = PlaceholderAPI.setPlaceholders(player, finalMessage);

        finalMessage = Utils.applyPlaceholders(
                finalMessage,
                Utils.placeholders(player, player.getWorld(), Bukkit.getServer().getName())
        );

        UUID uuid = player.getUniqueId();

        BukkitTask old = TASKS.remove(uuid);
        if (old != null)
            old.cancel();

        String ff = finalMessage;
        BukkitTask task = new BukkitRunnable(){

            long count = 0;

            @Override
            public void run(){

                if (!player.isOnline()){
                    TASKS.remove(uuid);
                    cancel();
                    return;
                }

                if (count >= duration){
                    TASKS.remove(uuid);
                    cancel();
                    return;
                }

                player.sendActionBar(Utils.colorize(ff));
                count++;
            }

        }.runTaskTimer(Main.getInstance(),0L,1L);

        TASKS.put(uuid, task);
    }

    public static void stop(Player player){
        BukkitTask task = TASKS.remove(player.getUniqueId());
        if (task != null){
            task.cancel();
        }
    }

    @Override
    public String getDisplayName(){
        return "ActionBar";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("duration", duration);
        section.set("message", message);
    }

    public static ActionBarEvent deserialize(int index, ConfigurationSection section) {
        String message = section.getString("message");
        if (message == null) return null;

        long duration = section.getLong("duration", 0L);

        return new ActionBarEvent(index, message, duration);
    }

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.DIAMOND,
                "&6ActionBar",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex(),
                        "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + Utils.formatTick(getDuration()),
                        "&#3F9AAEActionBar &#3F9AAE: &#F6CE71" + getMessage()
                ), false);
    }

}

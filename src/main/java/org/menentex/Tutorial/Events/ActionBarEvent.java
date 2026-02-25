package org.menentex.Tutorial.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class ActionBarEvent extends TutorialEvents{

    private final String message;
    private final long duration;

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
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        String finalMessage = message;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            finalMessage = PlaceholderAPI.setPlaceholders(player, finalMessage);

        finalMessage = Utils.applyPlaceholders(message, Utils.placeholders(player, player.getWorld(), Bukkit.getServer().getName()));

        String finalMessage1 = finalMessage;
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){
                if (count >= duration){
                    cancel();
                    return;
                }

                player.sendActionBar(Utils.colorizeComponent(finalMessage1));
                count++;
            }
        }.runTaskTimer(Main.getInstance(), 0, 1L);
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
                ));
    }

}

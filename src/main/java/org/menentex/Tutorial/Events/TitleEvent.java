package org.menentex.Tutorial.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.time.Duration;
import java.util.List;

public class TitleEvent extends TutorialEvent {

    private final String title;
    private final String subtitle;
    private final int duration;
    private final int fadeIn;
    private final int fadeOut;


    public TitleEvent(int index, String title, String subtitle, int duration, int fadeIn, int fadeOut){
        super(index);
        this.title = title;
        this.subtitle = subtitle;
        this.duration = duration;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    @Override
    public long getBlockingTicks(){

        return (long) fadeIn + (long) duration + (long) fadeOut;
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;

        String finalTitle = title;
        String finalSubtitle = subtitle;

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            finalTitle = PlaceholderAPI.setPlaceholders(player, finalTitle);
            finalSubtitle = PlaceholderAPI.setPlaceholders(player, finalSubtitle);
        }
        finalTitle = Utils.applyPlaceholders(title, Utils.placeholders(player, player.getWorld(), Bukkit.getServer().getName()));
        finalSubtitle = Utils.applyPlaceholders(subtitle, Utils.placeholders(player, player.getWorld(), Bukkit.getServer().getName()));

        player.sendMessage(finalTitle);

        player.showTitle(
                Title.title(
                        Utils.colorize(finalTitle),
                        Utils.colorize(finalSubtitle),
                        Title.Times.times(
                                Duration.ofMillis(fadeIn * 50L),
                                Duration.ofMillis(duration * 50L),
                                Duration.ofMillis(fadeOut * 50L)
                        )
                )
        );

    }

    @Override
    public String getDisplayName(){
        return "Title";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("title", title);
        section.set("subtitle", subtitle);
        section.set("duration", duration);
        section.set("fadeIn", fadeIn);
        section.set("fadeOut", fadeOut);
    }

    public static TitleEvent deserialize(int index, ConfigurationSection section) {
        return new TitleEvent(
                index,
                section.getString("title"),
                section.getString("subtitle"),
                section.getInt("duration"),
                section.getInt("fadeIn"),
                section.getInt("fadeOut")
        );
    }

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.BOOK,
                "&6Title",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + index,
                        "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + Utils.formatTick(duration),
                        "&#3F9AAETitle &#3F9AAE: &#F6CE71" + title,
                        "&#3F9AAESubtitle &#3F9AAE: &#F6CE71" + subtitle,
                        "&#3F9AAEFadeIn &#3F9AAE: &#F6CE71" + Utils.formatTick(fadeIn),
                        "&#3F9AAEFadeOut &#3F9AAE: &#F6CE71" + Utils.formatTick(fadeOut)
                ), false);
    }


}

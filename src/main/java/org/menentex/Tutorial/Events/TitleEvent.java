package org.menentex.Tutorial.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Utils.Utils;

public class TitleEvent extends TutorialEvents{

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

    public String getTitle(){
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getDuration() {
        return duration;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getFadeOut() {
        return fadeOut;
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

        player.sendTitle(
                Utils.colorize(finalTitle),
                Utils.colorize(finalSubtitle),
                (int) (long) fadeIn,
                (int) (long) duration,
                (int) (long) fadeOut
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


}

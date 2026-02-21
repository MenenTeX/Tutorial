package org.menentex.Tutorial.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Utils.Utils;

public class MessageEvent extends TutorialEvents{

    private final String message;

    public MessageEvent(int index, String message){
        super(index);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        String finalMessage = message;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            finalMessage = PlaceholderAPI.setPlaceholders(player, finalMessage);
        }

        finalMessage = Utils.applyPlaceholders(message, Utils.placeholders(player, player.getWorld(), Bukkit.getServer().getName()));


        player.sendMessage(Utils.colorize(finalMessage));
    }

    @Override
    public String getDisplayName(){
        return "Message";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("message", message);
    }

    public static MessageEvent deserialize(int index, ConfigurationSection section) {
        String message = section.getString("message");
        if (message == null) return null;

        return new MessageEvent(index, message);
    }


}

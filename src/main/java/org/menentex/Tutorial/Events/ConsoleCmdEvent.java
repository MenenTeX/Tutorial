package org.menentex.Tutorial.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Utils.Utils;

public class ConsoleCmdEvent extends TutorialEvents{

    private final String command;

    public ConsoleCmdEvent(int index, String command){
        super(index);
        this.command = command;
    }

    public String getCommand(){
        return command;
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        String finalCommand = command;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            finalCommand = PlaceholderAPI.setPlaceholders(player, command).toLowerCase();
        }
        finalCommand = Utils.applyPlaceholders(command, Utils.placeholders(player, player.getWorld(), Bukkit.getServer().getName()));


        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
    }

    @Override
    public String getDisplayName(){
        return "ConsoleCommand";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("command", command);
    }

    public static ConsoleCmdEvent deserialize(int index, ConfigurationSection section) {
        String command = section.getString("command");
        if (command == null) return null;

        return new ConsoleCmdEvent(index, command);
    }

}

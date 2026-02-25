package org.menentex.Tutorial.Events;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

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

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.BLAZE_ROD,
                "&6Console Command",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex(),
                        "&#3F9AAECommand &#3F9AAE: &#F6CE71/" + getCommand()
                ));
    }

}

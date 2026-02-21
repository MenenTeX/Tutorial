package org.menentex.Tutorial.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {

    public List<String> onTabComplete (CommandSender sender, Command command, String label, String[] args) {

        if (
                command.getName().equalsIgnoreCase("tutorialedit") ||
                        command.getName().equalsIgnoreCase("tutoedit")
        ) {

            List<String> completions = new ArrayList<>();
            if (args.length == 1) {
                completions.add("create");
                completions.add("delete");
                completions.add("open");
                completions.add("reload");
            }
            if (args.length == 2) {
                String sub = args[0].toLowerCase();
                if (!sub.equals("reload")) {
                    RegistryGui registryGui = Main.getInstance().getRegistryGui();
                    for (InMemoryGui gui : registryGui.getAllGuis()) {
                        completions.add(gui.getGuiName());
                    }
                }
            }
            String lastWord = args[args.length - 1].toLowerCase();
            completions.removeIf((s -> {
                return !s.toLowerCase().startsWith(lastWord);
            }));
            return completions;
        }

        else if (command.getName().equalsIgnoreCase("tutorial")) {

            List<String> completions = new ArrayList<>();

            if (args.length == 1) {
                RegistryGui registryGui = Main.getInstance().getRegistryGui();
                for (InMemoryGui gui : registryGui.getAllGuis()) {
                    completions.add(gui.getGuiName());
                }
                completions.add("exit");
            }
            if (args.length == 2){
                for (Player player : Bukkit.getOnlinePlayers())
                    completions.add(player.getName());
            }
            String lastWord = args[args.length - 1].toLowerCase();
            completions.removeIf((s -> {
                return !s.toLowerCase().startsWith(lastWord);
            }));
            return completions;
        }

        else {
            return Collections.emptyList();
        }
    }
}

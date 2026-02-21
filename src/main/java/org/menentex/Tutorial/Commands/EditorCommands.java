package org.menentex.Tutorial.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.DataManager.Gui.GuiKeys;
import org.menentex.Tutorial.DataManager.Gui.GuiLoader;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.Player.EditorStateManager;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;
import org.menentex.Tutorial.Utils.Utils;

import java.util.UUID;

public class EditorCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if (!(Utils.hasPermission(sender, false, Permissions.ADMIN, Permissions.EDIT))) return true;

        if (args.length == 1){
            if (args[0].equalsIgnoreCase("reload")){
                Main plugin = Main.getInstance();
                plugin.loadMessages();
                new Messages();
                plugin.loadTutorials();
                plugin.loadBlockCommand();
                plugin.saveDefaultConfig();
                plugin.reloadConfig();
                RegistryGui registryGui = Main.getInstance().getRegistryGui();
                GuiLoader.loadGuisToRegistry(registryGui);
                sender.sendMessage(Messages.RELOAD);
                return true;
            }
        }

        if (!(sender instanceof Player player)){
            sender.sendMessage(Messages.CONSOLE_SENDER);
            return true;
        }

        if (!(Utils.hasPermission(player, false, Permissions.ADMIN, Permissions.EDIT))) return true;

        if (args.length < 2 || args.length > 3){
            for (String msg : Messages.HELP){
                player.sendMessage(msg);
            }
            return true;
        }

        String guiName = args[1];

        if (guiName == null || guiName.isEmpty()) {
            for (String msg : Messages.HELP) {
                player.sendMessage(msg);
            }
            return true;
        }

        RegistryGui registryGui = Main.getInstance().getRegistryGui();

        if (args[0].equalsIgnoreCase("create")){

            if (registryGui.exists(guiName)){

                InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
                if (gui == null) return true;

                gui.buildRemainingPages(registryGui);

                long lastPage = Math.max(1, gui.countActionLists());

                int intLastPage = (int) lastPage;

                gui.open(player, GuiKeys.ACTION_LIST + lastPage);

                EditorStateManager editorStateManager = Main.getInstance().getEditorStateManager();

                if (!editorStateManager.isInEditor(player))
                    editorStateManager.enter(player, guiName, GuiKeys.ACTION_LIST + intLastPage);

                editorStateManager.getState(player).ifPresent(editorState -> editorState.setCurrentPage(intLastPage));

            } else {
                if (args[0].equalsIgnoreCase("exit")){
                    player.sendMessage(Utils.colorize("&cYou can't use this name"));
                    return true;
                }
                InMemoryGui gui = new InMemoryGui(guiName);

                String msg = Messages.Usage.CREATE_TUTORIAL;
                msg = msg.replace("%tutorial%", guiName);
                player.sendMessage(msg);

                gui.createDefaultInventory();
                registryGui.registerGui(gui);

                EditorStateManager editorStateManager = Main.getInstance().getEditorStateManager();

                editorStateManager.enter(player, guiName, GuiKeys.ACTION_LIST + 1);

                gui.open(player, GuiKeys.ACTION_LIST + 1);
            }

        } else if (args[0].equalsIgnoreCase("delete")) {

            if (registryGui.exists(guiName)){
                EditorStateManager editorStateManager = new EditorStateManager();

                for (UUID uuid : editorStateManager.getPlayersInGui(guiName)){
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        p.closeInventory();
                        editorStateManager.exit(p);
                    }
                }
                InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
                if (gui == null) return true;
                gui.deleteGuiFromYML(true);
                registryGui.removeGui(guiName);
                String msg = Messages.Usage.DELETE_TUTORIAL;
                msg = msg.replace("%tutorial%", guiName);
                player.sendMessage(msg);
            } else {
                String msg = Messages.Usage.NOT_FOUND;
                msg = msg.replace("%tutorial%", guiName);
                player.sendMessage(msg);
                return true;
            }

        } else if (args[0].equalsIgnoreCase("open")){
            if (registryGui.exists(guiName)){

                InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
                if (gui == null) return true;

                long lastPage = Math.max(1, gui.countActionLists());
                gui.open(player, GuiKeys.ACTION_LIST + lastPage);
            } else {
                String msg = Messages.Usage.NOT_FOUND;
                msg = msg.replace("%tutorial%", guiName);
                player.sendMessage(msg);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("save")){
            if (registryGui.exists(guiName)){
                InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
                if (gui == null) return true;

                gui.saveToConfigSectionAsync();
                String msg = Messages.Usage.SAVE_TUTORIAL;
                msg = msg.replace("%tutorial%", guiName);
                player.sendMessage(msg);
            } else {
                String msg = Messages.Usage.NOT_FOUND;
                msg = msg.replace("%tutorial%", guiName);
                player.sendMessage(msg);
                return true;
            }
        }


        else {
            for (String msg : Messages.HELP)
                player.sendMessage(msg);
            return true;
        }
        return true;
    }

}

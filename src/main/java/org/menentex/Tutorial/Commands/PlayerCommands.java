package org.menentex.Tutorial.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.Player.PlayerState;
import org.menentex.Tutorial.DataManager.Player.PlayerStateManager;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;
import org.menentex.Tutorial.Tasks.GuiTask;
import org.menentex.Tutorial.Tasks.GuiTaskManager;
import org.menentex.Tutorial.Utils.Utils;

public class PlayerCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)){
            sender.sendMessage(Messages.CONSOLE_SENDER);
            return true;
        }

        if (command.getName().equalsIgnoreCase("exit")) {

            if (!Utils.hasPermission(player, true, Permissions.ADMIN, Permissions.EXIT)) return true;

            PlayerStateManager stateManager = Main.getInstance().getPlayerStateManager();
            PlayerState state = stateManager.getState(player).orElse(null);

            if (state == null) {
                player.sendMessage(Messages.Usage.NOTINTUTORIAL);
                return true;
            }

            InMemoryGui gui = Main.getInstance()
                    .getRegistryGui()
                    .getGui(state.getGuiName())
                    .orElse(null);

            if (gui == null)
                return true;

            if (!gui.getAllowExitCommand()) {
                return true;
            }

            stateManager.endTutorial(player.getUniqueId());

            GuiTask task = Main.getInstance().getGuiTaskManager().getTaskForGui(gui.getGuiName());
            if (task != null) task.endTutorial(player, gui);

            player.sendMessage(Messages.Usage.EXIT_COMMAND);
            return true;
        }


        if (!(args.length > 0 && args.length < 3)){
            Utils.sendMessageComponent(player, Messages.HELP);
            return true;
        }

        String guiName = args[0];

        if (guiName == null || guiName.isEmpty()) {
            Utils.sendMessageComponent(player, Messages.HELP);
            return true;
        }

        RegistryGui registryGui = Main.getInstance().getRegistryGui();

        if (registryGui.exists(guiName)){

            Player target = player;

            InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
            if (gui == null) return true;

            if (!(Utils.hasPermission(player, true, Permissions.ADMIN, gui.getPermission()))) return true;

            if (args.length == 2) {
                if (!Utils.hasPermission(player, true, Permissions.ADMIN)) return true;
                Player t = Bukkit.getPlayerExact(args[1]);
                if (t != null && t.isOnline()) {
                    target = t;
                }
            }

            if (gui.getEvents().isEmpty()){
                Utils.sendMessageComponent(player, Messages.Usage.TUTORIAL_EMPTY);
                return true;
            }

            PlayerStateManager playerStateManager = Main.getInstance().getPlayerStateManager();
            GuiTaskManager guiTaskManager = Main.getInstance().getGuiTaskManager();

            if (playerStateManager.inState(player)){
                player.sendMessage(
                        Utils.applyPlaceholder(Messages.Usage.ALREADY_TUTORIAL, "%tutorial%", guiName)
                );
            }

            if (guiTaskManager.isRunning(guiName)){
                playerStateManager.startTutorial(target, guiName);
            } else {
                playerStateManager.startTutorial(target, guiName);
                GuiTask guiTask = new GuiTask(guiName, playerStateManager, registryGui);
                guiTask.runTaskTimer(Main.getInstance(), 0L, 1L);
                guiTaskManager.register(guiName, guiTask);
            }

        } else if (args[0].equalsIgnoreCase("exit")){

            if (!Utils.hasPermission(player, true, Permissions.ADMIN, Permissions.EXIT)) return true;

            PlayerStateManager stateManager = Main.getInstance().getPlayerStateManager();

            PlayerState playerState = stateManager.getState(player).orElse(null);
            if (playerState == null) {
                player.sendMessage(Messages.Usage.NOTINTUTORIAL);
                return true;
            }

            InMemoryGui gui = Main.getInstance()
                    .getRegistryGui()
                    .getGui(playerState.getGuiName())
                    .orElse(null);

            if (gui == null)
                return true;

            if (!gui.getAllowExitCommand())
                return true;

            stateManager.endTutorial(player.getUniqueId());

            player.sendMessage(Messages.Usage.EXIT_COMMAND);
            return true;
        }

        return true;
    }

}

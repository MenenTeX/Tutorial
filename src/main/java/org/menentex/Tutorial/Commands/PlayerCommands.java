package org.menentex.Tutorial.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.menentex.Tutorial.DataManager.Gui.EventListMananger;
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

            // Always end tutorial
            stateManager.endTutorial(player.getUniqueId());

            // Remove potion effects
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            player.sendMessage(Messages.Usage.EXIT_COMMAND);
            return true;
        }

        if (!(args.length > 0 && args.length < 3)){
            for (String msg : Messages.HELP){
                player.sendMessage(msg);
            }
            return true;
        }

        String guiName = args[0];

        if (guiName == null || guiName.isEmpty()) {
            for (String msg : Messages.HELP) {
                player.sendMessage(msg);
            }
            return true;
        }

        RegistryGui registryGui = Main.getInstance().getRegistryGui();

        if (registryGui.exists(guiName)){

            Player target = player;

            if (args.length == 2) {
                if (!Utils.hasPermission(player, true, Permissions.ADMIN)) return true;
                Player t = Bukkit.getPlayerExact(args[1]);
                if (t != null && t.isOnline()) {
                    target = t;
                }
            }

            PlayerStateManager playerStateManager = Main.getInstance().getPlayerStateManager();
            GuiTaskManager guiTaskManager = Main.getInstance().getGuiTaskManager();

            if (guiTaskManager.isRunning(guiName)){
                playerStateManager.startTutorial(target, guiName);
            } else {
                playerStateManager.startTutorial(target, guiName);
                GuiTask guiTask = new GuiTask(guiName, playerStateManager, registryGui);
                guiTask.runTaskTimer(Main.getInstance(), 0L, 1L);
                guiTaskManager.register(guiName, guiTask);
                InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
                if (gui == null) return true;
                EventListMananger e = Main.getInstance().getEventListMananger();

                if (gui.getLockMovement()) e.addMovementLock(target);
                if (gui.getLockHeadMovement()) e.addHeadMovementLock(target);
                if (gui.getDamageProtection()) e.addDamageProtection(target);
                if (gui.getDisableSendChat()) e.addDisableSendChat(target);
                if (gui.getNormalInvisible()) e.addNormalInvisibility(target);
                if (gui.getProInvisible()) e.addProInvisibility(target);
                if (gui.getDisablePlayerInteract()) e.addDisablePlayerInteract(target, gui.getInteract());
            }

        } else if (args[0].equalsIgnoreCase("exit")){
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

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            stateManager.endTutorial(player.getUniqueId());

            player.sendMessage(Messages.Usage.EXIT_COMMAND);
            return true;
        }

        return true;
    }

}

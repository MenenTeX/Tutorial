package org.menentex.Tutorial.Listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.menentex.Tutorial.Commands.Permissions;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.Player.PlayerStateManager;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Tasks.GuiTask;
import org.menentex.Tutorial.Tasks.GuiTaskManager;
import org.menentex.Tutorial.Utils.UpdateChecker;
import org.menentex.Tutorial.Utils.Utils;

public class PlayerQuitServer implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Main.getInstance().getEditorStateManager().exit(event.getPlayer());
        Main.getInstance().getEventListMananger().removeAllEvent(event.getPlayer());
        Main.getInstance().getActionEditorState().removeSession(event.getPlayer());
        Main.getInstance().getPlayerStateManager().endTutorial(event.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (Utils.hasPermission(event.getPlayer(), false, Permissions.ADMIN)){
            new UpdateChecker(Main.getInstance(), 132904).notifyPlayer(event.getPlayer());
        }

        FileConfiguration config = Main.getInstance().getConfig();
        String guiName = config.getString("tutorials.first-join-tutorial");
        if (guiName == null || guiName.equalsIgnoreCase("none")) return;

        RegistryGui registryGui = Main.getInstance().getRegistryGui();
        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if (gui == null) return;

        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) return;

        GuiTaskManager guiTaskManager = Main.getInstance().getGuiTaskManager();

        Main.getInstance().getPlayerStateManager().startTutorial(player, guiName);
        PlayerStateManager playerStateManager = Main.getInstance().getPlayerStateManager();

        Player target = event.getPlayer();

        if (guiTaskManager.isRunning(guiName)){
            playerStateManager.startTutorial(target, guiName);
        } else {
            playerStateManager.startTutorial(target, guiName);
            GuiTask guiTask = new GuiTask(guiName, playerStateManager, registryGui);
            guiTask.runTaskTimer(Main.getInstance(), 0L, 1L);
            guiTaskManager.register(guiName, guiTask);
        }
    }
}

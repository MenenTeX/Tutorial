package org.menentex.Tutorial.Listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.menentex.Tutorial.DataManager.Gui.EventListMananger;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.Player.PlayerStateManager;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Tasks.GuiTask;
import org.menentex.Tutorial.Tasks.GuiTaskManager;

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
            EventListMananger e = Main.getInstance().getEventListMananger();
            if (gui.getLockMovement()) {
                e.addMovementLock(target);
            }
            if (gui.getLockHeadMovement()) {
                e.addHeadMovementLock(target);
            }
            if (gui.getDamageProtection()) {
                e.addDamageProtection(target);
            }
            if (gui.getDisableSendChat()) {
                e.addDisableSendChat(target);
            }
            if (gui.getNormalInvisible()) {
                e.addNormalInvisibility(target);
            }
            if (gui.getProInvisible()) {
                e.addProInvisibility(target);
            }
            if (gui.getDisablePlayerInteract()){
                e.addDisablePlayerInteract(target, gui.getInteract());
            }
            if (gui.getDisableSendChat()){
                e.addDisableSendChat(target);
            }
        }
    }
}

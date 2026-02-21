package org.menentex.Tutorial.Action;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.menentex.Tutorial.DataManager.Gui.GuiKeys;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.Player.EditorState;
import org.menentex.Tutorial.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActionEditorState {

    private final Map<UUID, ActionSession> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> timeoutTasks = new HashMap<>();

    private final Map<UUID, Long> timeoutTokens = new HashMap<>();

    public void startSession(Player p, ActionManager.ActionType type){
        sessions.put(p.getUniqueId(), new ActionSession(type));
    }

    public ActionSession getSession(Player p){
        return sessions.get(p.getUniqueId());
    }

    public void removeSession(Player p){
        sessions.remove(p.getUniqueId());
    }

    public void endSession(Player player, InMemoryGui gui){
        UUID uuid = player.getUniqueId();

        removeSession(player);

        BukkitRunnable oldTask = timeoutTasks.remove(uuid);
        if(oldTask != null) oldTask.cancel();

        timeoutTokens.remove(uuid);

        EditorState editorState = Main.getInstance()
                .getEditorStateManager()
                .getState(player)
                .orElse(null);

        if (editorState == null) return;

        gui.open(player, GuiKeys.ACTION_LIST + editorState.getCurrentPage());
    }

    public void startChatTimeout(Player player, String guiName, int duration, String currentInv){
        RegistryGui registryGui = Main.getInstance().getRegistryGui();
        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if(gui == null) return;

        UUID uuid = player.getUniqueId();

        BukkitRunnable oldTask = timeoutTasks.remove(uuid);
        if(oldTask != null) oldTask.cancel();

        long token = System.nanoTime();
        timeoutTokens.put(uuid, token);

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {

                Long currentToken = timeoutTokens.get(uuid);

                if(currentToken == null || currentToken != token) return;

                if(getSession(player) != null){
                    removeSession(player);
                    gui.open(player, currentInv);
                }

                timeoutTasks.remove(uuid);
                timeoutTokens.remove(uuid);
            }
        };

        timeoutTasks.put(uuid, task);
        task.runTaskLater(Main.getInstance(), duration * 20L);
    }

    public void eventChatTimeout(Player player, String guiName, int duration){
        RegistryGui registryGui = Main.getInstance().getRegistryGui();
        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if(gui == null) return;

        UUID uuid = player.getUniqueId();

        BukkitRunnable oldTask = timeoutTasks.remove(uuid);
        if(oldTask != null) oldTask.cancel();

        long token = System.nanoTime();
        timeoutTokens.put(uuid, token);

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {

                Long currentToken = timeoutTokens.get(uuid);

                if(currentToken == null || currentToken != token) return;

                if(getSession(player) != null){
                    endSession(player, gui);
                }

                timeoutTasks.remove(uuid);
                timeoutTokens.remove(uuid);
            }
        };

        timeoutTasks.put(uuid, task);
        task.runTaskLater(Main.getInstance(), duration * 20L);
    }
}

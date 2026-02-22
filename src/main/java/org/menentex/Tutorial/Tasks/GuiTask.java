package org.menentex.Tutorial.Tasks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.menentex.Tutorial.DataManager.Gui.EventListMananger;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.Player.PlayerState;
import org.menentex.Tutorial.DataManager.Player.PlayerStateManager;
import org.menentex.Tutorial.Events.TutorialEvents;
import org.menentex.Tutorial.Events.ActionBarEvent;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.*;

public class GuiTask extends BukkitRunnable {

    private final String guiName;
    private final PlayerStateManager playerStateManager;
    private final RegistryGui registryGui;

    private long tickCounter = 0;
    private BukkitRunnable exitBarTask;
    private final Set<UUID> actionBarHidden = new HashSet<>();

    public GuiTask(String guiName, PlayerStateManager playerStateManager, RegistryGui registryGui){
        this.guiName = guiName;
        this.playerStateManager = playerStateManager;
        this.registryGui = registryGui;
    }

    @Override
    public void run() {

        int maxPerTick = Main.getInstance()
                .getConfig()
                .getInt("tutorials.batch", 50);

        tickCounter++;

        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if (gui == null) {
            cancel();
            stopExitBar();
            return;
        }

        Collection<PlayerState> states =
                playerStateManager.getStatesForGui(guiName);

        if (states.isEmpty()) {
            cancel();
            stopExitBar();
            Main.getInstance().getGuiTaskManager().unregister(guiName);
            return;
        }

        startExitBar(gui);

        int processed = 0;
        Set<UUID> toRemove = new HashSet<>();

        for (PlayerState state : states) {

            if (maxPerTick != -1 && processed >= maxPerTick)
                break;

            UUID uuid = state.getPlayerId();
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                toRemove.add(uuid);
                continue;
            }

            if (state.isWaiting(tickCounter))
                continue;

            int index = state.getCurrentEventIndex();
            TutorialEvents event = gui.getEvent(index);

            if (event == null || index >= gui.getEvents().size()) {
                endTutorial(player, gui);
                toRemove.add(uuid);
                continue;
            }

            if (event instanceof ActionBarEvent) {
                actionBarHidden.add(uuid);
            }

            event.execute(player);

            if (!(event instanceof ActionBarEvent)) {
                actionBarHidden.remove(uuid);
            }

            long block = event.getBlockingTicks();
            if (block > 0) {
                state.setWaitUntil(tickCounter + block);
            }

            state.nextEvent();
            processed++;
        }

        for (UUID uuid : toRemove) {
            playerStateManager.endTutorial(uuid);
        }

        if (playerStateManager.getStatesForGui(guiName).isEmpty()) {
            cancel();
            stopExitBar();
            Main.getInstance().getGuiTaskManager().unregister(guiName);
        }
    }

    private void startExitBar(InMemoryGui gui) {

        stopExitBar();

        if (!gui.getAllowExitCommand())
            return;

        exitBarTask = new BukkitRunnable() {
            @Override
            public void run() {

                Collection<PlayerState> currentStates =
                        playerStateManager.getStatesForGui(guiName);

                for (PlayerState state : currentStates) {

                    Player player = Bukkit.getPlayer(state.getPlayerId());

                    if (player == null || !player.isOnline())
                        continue;

                    if (actionBarHidden.contains(player.getUniqueId()))
                        continue;

                    String message = gui.getActionBarExitMessage();

                    if (message == null)
                        continue;

                    player.sendActionBar(Utils.colorizeComponent(message));
                }
            }
        };

        exitBarTask.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private void stopExitBar() {
        if (exitBarTask != null) {
            exitBarTask.cancel();
            exitBarTask = null;
        }
    }

    private void endTutorial(Player player, InMemoryGui gui) {

        EventListMananger e =
                Main.getInstance().getEventListMananger();

        e.removeAllEvent(player);

        if (e.isProInvisibility(player))
            e.removeProInvisibility(player);

        if (e.isNormalInvisibility(player))
            e.removeNormalInvisibility(player);

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        if (gui.getExitLocation() != null)
            player.teleport(gui.getExitLocation());

        playerStateManager.endTutorial(player.getUniqueId());
    }

    public long getTickCounter() {
        return tickCounter;
    }
}
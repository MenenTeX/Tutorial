package org.menentex.Tutorial.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.menentex.Tutorial.DataManager.EventListMananger;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.Player.PlayerState;
import org.menentex.Tutorial.DataManager.Player.PlayerStateManager;
import org.menentex.Tutorial.Events.ActionBarEvent;
import org.menentex.Tutorial.Events.ConditionalEvent;
import org.menentex.Tutorial.Events.TutorialEvent;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.*;

public class GuiTask extends BukkitRunnable {

    private final String guiName;
    private final PlayerStateManager playerStateManager;
    private final RegistryGui registryGui;

    private long tickCounter = 0;
    private BukkitRunnable exitBarTask;

    private final Map<UUID, Long> actionBarHidden = new HashMap<>();

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
                actionBarHidden.remove(uuid);
                continue;
            }

            if (state.isConditionBlocked())
                continue;

            if (state.isWaiting(tickCounter))
                continue;

            int index = state.getCurrentEventIndex();
            TutorialEvent event = gui.getEvent(index);

            if (event == null || index >= gui.getEvents().size()) {
                endTutorial(player, gui);
                toRemove.add(uuid);
                continue;
            }

            if (event instanceof ActionBarEvent) {
                actionBarHidden.put(uuid, tickCounter + ((ActionBarEvent) event).getDuration());
            }

            event.execute(player);

            if (event instanceof ConditionalEvent) {
                state.blockByCondition(event);
                state.nextEvent();
                processed++;
                continue;
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

                    UUID uuid = player.getUniqueId();

                    Long hiddenUntil = actionBarHidden.get(uuid);

                    if (hiddenUntil != null) {
                        if (tickCounter < hiddenUntil)
                            continue;

                        actionBarHidden.remove(uuid);
                    }

                    String message = gui.getActionBarExitMessage();

                    if (message == null)
                        continue;

                    player.sendActionBar(Utils.colorize(message));
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

    public void endTutorial(Player player, InMemoryGui gui) {

        UUID uuid = player.getUniqueId();

        EventListMananger e =
                Main.getInstance().getEventListMananger();

        e.removeAllEvent(player);

        if (e.isProInvisibility(player))
            e.removeProInvisibility(player);

        if (player.getGameMode() != GameMode.CREATIVE) {
            if (e.isMovementLock(player))
                player.setAllowFlight(false);
            player.setFlying(false);
        }

        if (gui.getExitLocation() != null)
            player.teleport(gui.getExitLocation());

        actionBarHidden.remove(uuid);
        ActionBarEvent.stop(player);

        playerStateManager.endTutorial(uuid);
    }

    public long getTickCounter() {
        return tickCounter;
    }
}

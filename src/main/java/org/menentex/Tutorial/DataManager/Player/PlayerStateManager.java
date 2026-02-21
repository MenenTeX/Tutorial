package org.menentex.Tutorial.DataManager.Player;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStateManager {

    private final Map<String, Set<PlayerState>> guiToStates = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerState> states = new ConcurrentHashMap<>();

    public void startTutorial(Player player, String guiName){
        PlayerState state = new PlayerState(player.getUniqueId(), guiName);
        states.put(player.getUniqueId(), state);

        guiToStates.computeIfAbsent(guiName, k -> ConcurrentHashMap.newKeySet()).add(state);
    }

    public Optional<PlayerState> getState(Player player){
        return Optional.ofNullable(states.get(player.getUniqueId()));
    }

    public boolean inState(Player player){
        return states.containsKey(player.getUniqueId());
    }

    public void endTutorial(UUID uuid){
        PlayerState state = states.remove(uuid);
        if (state != null) {
            Set<PlayerState> set = guiToStates.get(state.getGuiName());
            if (set != null) {
                set.remove(state);
                if (set.isEmpty()) guiToStates.remove(state.getGuiName());
            }
        }
    }

    public void nextEvent(Player player) {
        PlayerState state = states.get(player.getUniqueId());
        if (state != null) {
            state.nextEvent();
        }
    }

    public Collection<PlayerState> getStatesForGui(String guiName){
        return guiToStates.getOrDefault(guiName, Collections.emptySet());
    }

}

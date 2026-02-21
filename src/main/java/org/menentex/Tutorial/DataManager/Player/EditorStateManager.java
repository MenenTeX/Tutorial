package org.menentex.Tutorial.DataManager.Player;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class EditorStateManager {

    private final Map<UUID, EditorState> players = new HashMap<>();

    public void enter(Player player, String guiName, String inventoryKey) {
        players.put(player.getUniqueId(),
                new EditorState(guiName, inventoryKey));
    }

    public void exit(Player player) {
        players.remove(player.getUniqueId());
    }

    public boolean isInEditor(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    public Optional<EditorState> getState(Player player) {
        return Optional.ofNullable(players.get(player.getUniqueId()));
    }

    public Set<UUID> getPlayersInGui(String guiName) {
        return players.entrySet().stream()
                .filter(entry -> entry.getValue().getGuiName().equals(guiName))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void updateEnter(Player player, String inventoryKey){
        EditorState state = players.get(player.getUniqueId());
        if (state != null){
            state.setInventoryKey(inventoryKey);
        }
    }

}

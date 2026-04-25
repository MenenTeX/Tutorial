package org.menentex.Tutorial.DataManager.WaitingEventData;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaitActionList {

    private final Map<UUID, WaitRegionData> waitingPlayers = new HashMap<>();

    public void add(Player player, WaitRegionData data){
        waitingPlayers.put(player.getUniqueId(), data);
    }

    public WaitRegionData get(Player player){
        return waitingPlayers.get(player.getUniqueId());
    }

    public void remove(Player player){
        waitingPlayers.remove(player.getUniqueId());
    }

}

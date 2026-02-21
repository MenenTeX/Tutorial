package org.menentex.Tutorial.DataManager.Gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Main;

import java.util.*;

public class EventListMananger {

    public enum Player_Interact{
        RIGHT,
        LEFT,
        BOTH,
        DISABLE
    }

    private final Set<UUID> movementLock = new HashSet<>();
    private final Set<UUID> headMovementLock = new HashSet<>();
    private final Set<UUID> normalInvisibility = new HashSet<>();
    private final Set<UUID> proInvisibility = new HashSet<>();
    private final Set<UUID> disableSendChat = new HashSet<>();
    private final Set<UUID> damageProtection = new HashSet<>();
    private final Map<UUID, Player_Interact> disablePlayerInteract = new HashMap<>();

    public void addMovementLock(Player player){
        movementLock.add(player.getUniqueId());
    }
    public void addHeadMovementLock(Player player){
        headMovementLock.add(player.getUniqueId());
    }
    public void addNormalInvisibility(Player player){
        normalInvisibility.add(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()){
            p.hidePlayer(Main.getInstance(), player);
        }
    }
    public void addProInvisibility(Player player){
        proInvisibility.add(player.getUniqueId());
        Main.getInstance().getProtocolib().hidePlayerForAll(player);
    }
    public void addDisableSendChat(Player player){
        disableSendChat.add(player.getUniqueId());
    }
    public void addDamageProtection(Player player){
        damageProtection.add(player.getUniqueId());
    }


    public void removeMovementLock(Player player){
        movementLock.remove(player.getUniqueId());
    }
    public void removeHeadMovementLock(Player player){
        headMovementLock.remove(player.getUniqueId());
    }
    public void removeNormalInvisibility(Player player){
        normalInvisibility.remove(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()){
            p.showPlayer(Main.getInstance(), player);
        }
    }
    public void removeProInvisibility(Player player){
        proInvisibility.remove(player.getUniqueId());
        Main.getInstance().getProtocolib().showPlayerToAll(player);
    }
    public void removeDisableSendChat(Player player){
        disableSendChat.remove(player.getUniqueId());
    }
    public void removeDamageProtection(Player player){
        damageProtection.remove(player.getUniqueId());
    }


    public boolean isMovementLock(Player player){
        return movementLock.contains(player.getUniqueId());
    }
    public boolean isHeadMovementLock(Player player){
        return headMovementLock.contains(player.getUniqueId());
    }
    public boolean isNormalInvisibility(Player player){
        return normalInvisibility.contains(player.getUniqueId());
    }
    public boolean isProInvisibility(Player player){
        return proInvisibility.contains(player.getUniqueId());
    }
    public boolean isDisableSendChat(Player player){
        return disableSendChat.contains(player.getUniqueId());
    }
    public boolean isDamageProtection(Player player){
        return damageProtection.contains(player.getUniqueId());
    }

    public void addDisablePlayerInteract(Player player, Player_Interact player_interact){
        disablePlayerInteract.put(player.getUniqueId(), player_interact);
    }

    public boolean isDisablePlayerInteract(Player player){
        return disablePlayerInteract.containsKey(player.getUniqueId());
    }

    public Player_Interact getDisablePlayerInteract(Player player){
        return disablePlayerInteract.get(player.getUniqueId());
    }

    public void removeDisablePlayerInteract(Player player){
        disablePlayerInteract.remove(player.getUniqueId());
    }

    public void removeAllEvent(Player player){
        movementLock.remove(player.getUniqueId());
        headMovementLock.remove(player.getUniqueId());
        normalInvisibility.remove(player.getUniqueId());
        proInvisibility.remove(player.getUniqueId());
        disableSendChat.remove(player.getUniqueId());
        damageProtection.remove(player.getUniqueId());
    }
}

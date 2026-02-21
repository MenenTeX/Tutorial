package org.menentex.Tutorial.Dependencie;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.menentex.Tutorial.Main;

public class ProtocollibDepend {

    private final ProtocolManager protocolManager;

    public ProtocollibDepend(){
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void hidePlayerForAll(Player target) {
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.equals(target)) continue;

            try {
                PacketContainer destroyPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                destroyPacket.getIntegerArrays().write(0, new int[]{target.getEntityId()});
                protocolManager.sendServerPacket(player, destroyPacket);
            } catch (Exception e) {
            e.printStackTrace();
        }

        }
    }

    public void showPlayerToAll(Player target) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(target)) continue;
            player.showPlayer(Main.getInstance(), target);
        }
    }
}

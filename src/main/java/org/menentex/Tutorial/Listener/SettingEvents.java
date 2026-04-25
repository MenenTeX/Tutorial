package org.menentex.Tutorial.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.menentex.Tutorial.DataManager.EventListMananger;
import org.menentex.Tutorial.DataManager.Player.PlayerState;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;

public class SettingEvents implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event){
        EventListMananger e = Main.getInstance().getEventListMananger();
        Player player = event.getPlayer();
        PlayerState playerState = Main.getInstance().getPlayerStateManager().getState(player).orElse(null);
        if (playerState == null) return;
        EventListMananger.Player_Interact interact = e.getDisablePlayerInteract(player);
        if (Main.getInstance().getPlayerStateManager().inState(player)){
            if (interact == EventListMananger.Player_Interact.BOTH || interact == EventListMananger.Player_Interact.LEFT)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickLeft(PlayerInteractEvent event){
        Player player = event.getPlayer();
        EventListMananger e = Main.getInstance().getEventListMananger();

        if (e.isDisablePlayerInteract(player) && Main.getInstance().getPlayerStateManager().inState(player)){

            EventListMananger.Player_Interact interact = e.getDisablePlayerInteract(player);

            switch (interact) {
                case LEFT:
                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                        event.setCancelled(true);
                    break;
                case RIGHT:
                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                        event.setCancelled(true);
                    break;
                case BOTH:
                    event.setCancelled(true);
                    break;
                default:
                    event.setCancelled(false);
                    break;
            }
        }
    }

    @EventHandler
    public void onSendMessage(AsyncChatEvent event){
        EventListMananger e = Main.getInstance().getEventListMananger();

        Player player = event.getPlayer();

        if (e.isDisableSendChat(player) && Main.getInstance().getPlayerStateManager().inState(player)){
            if (event.message().contains(Component.text("/exit")) || event.message().contains(Component.text("/tutorial exit"))) return;
            event.setCancelled(true);
            player.sendMessage(Messages.Usage.DISABLE_SENDMESSAGE);
        }
    }


    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event){
        EventListMananger e = Main.getInstance().getEventListMananger();
        Player player = event.getPlayer();

        if (e.isMovementLock(player) && Main.getInstance().getPlayerStateManager().inState(player)){
            Location to = event.getTo();

            to.setX(event.getFrom().getX());
            to.setY(event.getFrom().getY());
            to.setZ(event.getFrom().getZ());
            event.setTo(to);

            Block blockBelow = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

            if (blockBelow.getType() == Material.AIR) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
        }
    }

    @EventHandler
    public void onMoveHeadEvent(PlayerMoveEvent event){
        EventListMananger e = Main.getInstance().getEventListMananger();
        Player player = event.getPlayer();

        if (e.isHeadMovementLock(player) && Main.getInstance().getPlayerStateManager().inState(player)) {

            Location to = event.getTo();

            to.setYaw(event.getFrom().getYaw());
            to.setPitch(event.getFrom().getPitch());
            event.setTo(to);
        }
    }

    @EventHandler
    public void onDamageProtectionEvent(EntityDamageEvent event){
        if (event.getEntity() instanceof Player player){
            EventListMananger e = Main.getInstance().getEventListMananger();
            if (e.isDamageProtection(player) && Main.getInstance().getPlayerStateManager().inState(player)){
                event.setCancelled(true);
            }
        }
    }
}

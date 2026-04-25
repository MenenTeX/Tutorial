package org.menentex.Tutorial.Listener;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.menentex.Tutorial.Action.ActionSession;
import org.menentex.Tutorial.Commands.Permissions;
import org.menentex.Tutorial.DataManager.Player.EditorState;
import org.menentex.Tutorial.DataManager.Player.PlayerState;
import org.menentex.Tutorial.DataManager.WaitingEventData.WaitActionList;
import org.menentex.Tutorial.DataManager.WaitingEventData.WaitRegionData;
import org.menentex.Tutorial.DataManager.WaitingEventData.WaitRegionParticle;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class OnWaitActions implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (!(Utils.hasPermission(player, false, Permissions.ADMIN, Permissions.EDIT))) return;
        EditorState editorState = Main.getInstance().getEditorStateManager().getState(player).orElse(null);
        if (editorState == null) return;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemInHandMeta = itemInHand.getItemMeta();
        if (itemInHandMeta == null) return;
        PersistentDataContainer container = itemInHandMeta.getPersistentDataContainer();
        if (!(container.has(new NamespacedKey(Main.getInstance(), "tutorial_axe"), PersistentDataType.BYTE))) return;
        ActionSession session = Main.getInstance().getActionEditorState().getSession(player);
        if (session == null) return;
        event.setCancelled(true);
        Location loc = event.getClickedBlock().getLocation().add(0,1,0);
        if (action == Action.LEFT_CLICK_BLOCK) {
            session.setPos1(loc);
            Utils.sendMessage(player, List.of(
                    "&aFirst Location Set to:",
                    "&7X: &a" + loc.getBlockX(),
                    "&7Y: &a" + loc.getBlockY(),
                    "&7Z: &a" + loc.getBlockZ()
            ));
        } else {
            session.setPos2(loc);
            Utils.sendMessage(player, List.of(
                    "&aSecond Location Set to:",
                    "&7X: &a" + loc.getBlockX(),
                    "&7Y: &a" + loc.getBlockY(),
                    "&7Z: &a" + loc.getBlockZ()
            ));
        }
    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event){

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        PlayerState playerState = Main.getInstance().getPlayerStateManager().getState(player).orElse(null);
        if (playerState == null) return;

        WaitActionList waitList = Main.getInstance().getWaitActionList();
        WaitRegionData data = waitList.get(player);
        if (data == null) return;
        if (!data.region().isInside(player.getLocation())) return;

        waitList.remove(player);
        WaitRegionParticle.getInstance().stopRegionParticles(player);

        Main.getInstance()
                .getPlayerStateManager()
                .getState(player).ifPresent(PlayerState::unblockCondition);
    }
}

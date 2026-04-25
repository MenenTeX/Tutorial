package org.menentex.Tutorial.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.menentex.Tutorial.Commands.Permissions;
import org.menentex.Tutorial.DataManager.EventListMananger;
import org.menentex.Tutorial.DataManager.Gui.GuiInventoryHolder;
import org.menentex.Tutorial.DataManager.Gui.GuiKeys;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.InventoryBuilder;
import org.menentex.Tutorial.DataManager.Player.EditorState;
import org.menentex.Tutorial.DataManager.Player.EditorStateManager;
import org.menentex.Tutorial.Events.TutorialEvent;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class OpenInventoryHandler implements Listener {

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent event){

        Player player = (Player) event.getPlayer();

        if (!(Utils.hasPermission(player, false, Permissions.ADMIN, Permissions.EDIT))){
            player.closeInventory();
            return;
        }

        EditorStateManager editorStateManager = Main.getInstance().getEditorStateManager();
        if (!(editorStateManager.isInEditor(player))) return;

        Inventory openedInv = event.getView().getTopInventory();

        if (!(openedInv.getHolder() instanceof GuiInventoryHolder holder))
            return;

        RegistryGui registryGui = Main.getInstance().getRegistryGui();

        String inventoryId = holder.inventoryKey();
        String guiName = holder.guiName();

        long lastPage = Math.max(
                1,
                registryGui.getGui(guiName)
                        .map(InMemoryGui::countActionLists)
                        .orElse(1L)
        );

        int currentPage = 1;

        if (inventoryId.startsWith(GuiKeys.ACTION_LIST)){
            String formatPage = inventoryId.replace(GuiKeys.ACTION_LIST, "");
            currentPage = Integer.parseInt(formatPage);
        }

        if (inventoryId.startsWith(GuiKeys.ACTION_LIST)){

            ItemStack nextPageItem = openedInv.getItem(52);
            ItemStack previousPageItem = openedInv.getItem(53);

            if (nextPageItem == null || previousPageItem == null) return;

            ItemMeta nextPageMeta = nextPageItem.getItemMeta();
            ItemMeta previousPageMeta = previousPageItem.getItemMeta();

            if (nextPageMeta == null || previousPageMeta == null) return;

            nextPageMeta.lore(List.of(Utils.colorize("&7Page : " + currentPage + "&2/&7" + lastPage)));
            previousPageMeta.lore(List.of(Utils.colorize("&7Page : " + currentPage + "&4/&7" + lastPage)));

            nextPageItem.setItemMeta(nextPageMeta);
            previousPageItem.setItemMeta(previousPageMeta);

        } else if (inventoryId.equals(GuiKeys.SETTING_GUI_2)){
            registryGui.getGui(guiName).ifPresent(gui -> {
                boolean normalInvisible = gui.getNormalInvisible();
                if (normalInvisible){
                    openedInv.setItem(21, Utils.itemCreate(Material.LIME_DYE, "&aEnable", null, false));
                } else {
                    openedInv.setItem(21, Utils.itemCreate(Material.GRAY_DYE, "&cDisable", null, false));
                }

                if (gui.getInteract() == EventListMananger.Player_Interact.RIGHT)
                    openedInv.setItem(22, Utils.itemCreate(Material.LIME_DYE, "&aRight Click Blocked", null, false));
                else if (gui.getInteract() == EventListMananger.Player_Interact.LEFT)
                    openedInv.setItem(22, Utils.itemCreate(Material.LIME_DYE, "&aLeft Click Blocked", null, false));
                else if (gui.getInteract() == EventListMananger.Player_Interact.BOTH)
                    openedInv.setItem(22, Utils.itemCreate(Material.LIME_DYE, "&aBoth Click Blocked", null, false));
                else
                    openedInv.setItem(22, Utils.itemCreate(Material.GRAY_DYE, "&cDisable", null, false));


                boolean proInvisible = gui.getProInvisible();
                if (proInvisible){
                    openedInv.setItem(23, Utils.itemCreate(Material.LIME_DYE, "&aEnable", null, false));
                } else {
                    openedInv.setItem(23, Utils.itemCreate(Material.GRAY_DYE, "&cDisable", null, false));
                }
            });
        }


        else if (inventoryId.equals(GuiKeys.SETTING_GUI_1)){

            registryGui.getGui(guiName).ifPresent(gui -> {

                boolean allowExitCommand = gui.getAllowExitCommand();
                if (allowExitCommand){
                    openedInv.setItem(20, Utils.itemCreate(Material.LIME_DYE, "&aEnable", null, false));
                } else {
                    openedInv.setItem(20, Utils.itemCreate(Material.GRAY_DYE, "&cDisable", null, false));
                }

                boolean LockHeadMovement = gui.getLockHeadMovement();
                if (LockHeadMovement){
                    openedInv.setItem(21, Utils.itemCreate(Material.LIME_DYE, "&aEnable", null, false));
                } else {
                    openedInv.setItem(21, Utils.itemCreate(Material.GRAY_DYE, "&cDisable", null, false));
                }

                boolean LockMovement = gui.getLockMovement();
                if (LockMovement){
                    openedInv.setItem(22, Utils.itemCreate(Material.LIME_DYE, "&aEnable", null, false));
                } else {
                    openedInv.setItem(22, Utils.itemCreate(Material.GRAY_DYE, "&cDisable", null, false));
                }

                boolean disableSendChat = gui.getDisableSendChat();
                if (disableSendChat){
                    openedInv.setItem(23, Utils.itemCreate(Material.LIME_DYE, "&aEnable", null, false));
                } else {
                    openedInv.setItem(23, Utils.itemCreate(Material.GRAY_DYE, "&cDisable", null, false));
                }

                boolean DamageProtection = gui.getDamageProtection();
                if (DamageProtection){
                    openedInv.setItem(24,Utils.itemCreate(Material.LIME_DYE, "&aEnable", null, false));
                } else {
                    openedInv.setItem(24, Utils.itemCreate(Material.GRAY_DYE, "&cDisable", null, false));
                }

            });
        } else if (inventoryId.equals(GuiKeys.MOVE_EVENT)) {

            EditorState editorState = editorStateManager.getState(player).orElse(null);
            if (editorState == null) return;

            InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
            if (gui == null) return;

            int n = editorState.getCurrentEventSelected();
            List<TutorialEvent> events = gui.getEvents();

            if (events.isEmpty() || n < 0 || n >= events.size()) {
                player.closeInventory();
                return;
            }

            InventoryBuilder inventoryBuilder = new InventoryBuilder();
            inventoryBuilder.createItemMoveEvent(openedInv);

            int[] slots = {10, 11, 13, 15, 16};
            for (int slot : slots) {
                openedInv.setItem(slot, null);
            }

            for (int i = 0; i < 2; i++) {
                int prevIndex = n - 2 + i;
                if (prevIndex >= 0) {
                    openedInv.setItem(10 + i, Utils.convertEventToItem(events.get(prevIndex)));
                }
            }

            openedInv.setItem(13, Utils.convertEventToItem(events.get(n)));

            for (int i = 0; i < 2; i++) {
                int nextIndex = n + 1 + i;
                if (nextIndex < events.size()) {
                    openedInv.setItem(15 + i, Utils.convertEventToItem(events.get(nextIndex)));
                }
            }

            ItemStack indexItem = openedInv.getItem(4);
            if (indexItem != null && indexItem.hasItemMeta()) {
                ItemMeta indexMeta = indexItem.getItemMeta();
                indexMeta.displayName(Utils.colorize("&fIndex &e: &f" + n));
                indexItem.setItemMeta(indexMeta);
            }

        }

    }

}

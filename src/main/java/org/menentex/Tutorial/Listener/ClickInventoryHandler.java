package org.menentex.Tutorial.Listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.menentex.Tutorial.Action.ActionEditorState;
import org.menentex.Tutorial.Action.ActionManager;
import org.menentex.Tutorial.Commands.Permissions;
import org.menentex.Tutorial.DataManager.Gui.*;
import org.menentex.Tutorial.DataManager.Player.EditorState;
import org.menentex.Tutorial.DataManager.Player.EditorStateManager;
import org.menentex.Tutorial.DataManager.Player.PlayerStateManager;
import org.menentex.Tutorial.Events.*;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;
import org.menentex.Tutorial.Tasks.GuiTask;
import org.menentex.Tutorial.Tasks.GuiTaskManager;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;
import java.util.UUID;

public class ClickInventoryHandler implements Listener{

    @EventHandler
    public void onDragInventory(InventoryDragEvent event){

        Player player = (Player) event.getWhoClicked();
        if (!(Utils.hasPermission(player, false, Permissions.ADMIN, Permissions.EDIT))) return;

        EditorStateManager editorStateManager = new EditorStateManager();
        if (!(editorStateManager.isInEditor(player))) return;

        Inventory openedInv = event.getView().getTopInventory();

        for (int slot : event.getRawSlots()) {
            if (slot < openedInv.getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event){

        if (event.getClickedInventory() == null)
            return;

        Player player = (Player) event.getWhoClicked();

        if (!(Utils.hasPermission(player, false, Permissions.EDIT, Permissions.ADMIN))) {
            player.closeInventory();
            return;
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR){
            return;
        }

        EditorStateManager editorStateManager = Main.getInstance().getEditorStateManager();

        if (!(editorStateManager.isInEditor(player))) return;

        Inventory openedInv = event.getView().getTopInventory();

        if (!(openedInv.getHolder() instanceof GuiInventoryHolder holder)) return;

        int slot = event.getRawSlot();
        if (slot < openedInv.getSize()){
            event.setCancelled(true);
        }

        String inventoryId = holder.getInventoryKey();
        String guiName = holder.getGuiName();

        RegistryGui registryGui = Main.getInstance().getRegistryGui();

        long lastPage = Math.max(
                1,
                registryGui.getGui(guiName)
                        .map(InMemoryGui::countActionLists)
                        .orElse(1L)
        );

        EditorState editorState = editorStateManager.getState(player).orElse(null);
        if (editorState == null) return;

        int currentPage = editorState.getCurrentPage();

        if (inventoryId.startsWith(GuiKeys.ACTION_LIST)){
            String formatPage = inventoryId.replace(GuiKeys.ACTION_LIST, "");
            editorState.setCurrentPage(Integer.parseInt(formatPage));
            currentPage = editorState.getCurrentPage();
        }

        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if (gui == null) return;

        Main plugin = Main.getInstance();
        ActionEditorState actionEditorState = Main.getInstance().getActionEditorState();

        if (inventoryId.startsWith(GuiKeys.ACTION_LIST)){

            editorStateManager.updateEnter(player, GuiKeys.ACTION_LIST + currentPage);

            //Event Click

            if (Utils.eventSlots().contains(slot)){

                int index = (currentPage - 1) * 36 + slot;
                editorState.setCurrentEventSelected(index);
                if (gui.getEvent(slot) != null)
                    gui.open(player, GuiKeys.MOVE_EVENT);

            }

            //Main Action List
            switch (slot){

                case 45 -> gui.open(player, GuiKeys.ACTION_ADD_1);

                case 46 -> gui.open(player, GuiKeys.SETTING_GUI_1);

                case 47 -> gui.open(player, GuiKeys.TELEPORT_GUI);

                case 48 -> gui.open(player, GuiKeys.DELETE_GUI);

                case 49 -> {
                    player.closeInventory();
                    gui.saveToConfigSectionAsync();
                    String msg = Messages.Usage.SAVE_TUTORIAL;
                    msg = msg.replace("%tutorial%", guiName);
                    player.sendMessage(msg);
                }

                case 50 -> {
                    player.closeInventory();
                    PlayerStateManager playerStateManager = Main.getInstance().getPlayerStateManager();
                    GuiTaskManager guiTaskManager = Main.getInstance().getGuiTaskManager();
                    if (guiTaskManager.isRunning(guiName)){
                        playerStateManager.startTutorial(player, guiName);
                    } else {
                        playerStateManager.startTutorial(player, guiName);
                        GuiTask guiTask = new GuiTask(guiName, playerStateManager, registryGui);
                        guiTask.runTaskTimer(Main.getInstance(), 0L, 1L);
                        guiTaskManager.register(guiName, guiTask);
                        EventListMananger e = Main.getInstance().getEventListMananger();

                        if (gui.getLockMovement()) e.addMovementLock(player);
                        if (gui.getLockHeadMovement()) e.addHeadMovementLock(player);
                        if (gui.getDamageProtection()) e.addDamageProtection(player);
                        if (gui.getDisableSendChat()) e.addDisableSendChat(player);
                        if (gui.getNormalInvisible()) e.addNormalInvisibility(player);
                        if (gui.getProInvisible()) e.addProInvisibility(player);
                        if (gui.getDisablePlayerInteract()) e.addDisablePlayerInteract(player, gui.getInteract());
                    }
                }


                case 52 -> {
                    if (currentPage < lastPage){
                        currentPage++;
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }
                }

                case 53 -> {
                    if (currentPage <= lastPage && currentPage != 1){
                        currentPage--;
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }
                }

            }

        }

        switch (inventoryId) {

            case GuiKeys.ACTION_ADD_1 -> {

                editorStateManager.updateEnter(player, GuiKeys.ACTION_ADD_1);

                switch (slot){

                    case 25 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new LockMovementEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 24 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new GodModeEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 23 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new FlyEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 22 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new LockHeadMovementEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 21 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.SETGAMEMODE_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.SET_GAMEMODE);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("setgamemode", 15), editorState.getInventoryKey());
                    }

                    case 20 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.ACTIONBAR_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.ACTION_BAR);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("actionbar", 15), editorState.getInventoryKey());
                    }

                    case 19 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.CONSOLECMD_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.CONSOLE_COMMAND);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("consolecommand", 20), editorState.getInventoryKey());
                    }

                    case 16 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.PLAYERCMD_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.PLAYER_COMMAND);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("playercommand", 20), editorState.getInventoryKey());
                    }

                    case 15 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.PLAYERSOUND_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.PLAY_SOUND);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("playsound", 40), editorState.getInventoryKey());
                    }

                    case 14 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new ClearChatEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 13 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.DELAY_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.DELAY);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("delay", 15), editorState.getInventoryKey());
                    }

                    case 12 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.TITLE_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.TITLE);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("title", 60), editorState.getInventoryKey());
                    }

                    case 11 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.MESSAGE_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.MESSAGE);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("message", 60), editorState.getInventoryKey());
                    }

                    case 10 -> gui.open(player, GuiKeys.TELEPORT_GUI);

                    case 39 -> gui.open(player, GuiKeys.ACTION_LIST + currentPage);

                    case 41 -> gui.open(player, GuiKeys.ACTION_ADD_2);

                }
            }

            case GuiKeys.ACTION_ADD_2 -> {

                switch (slot) {

                    case 10 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new UnLockHeadMovementEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 11 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new UnLockMovementEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 12 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new UnFlyEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 13 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.GIVEITEM_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.GIVE_ITEM);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("giveitem", 30), editorState.getInventoryKey());
                    }

                    case 14 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new ClearInventoryEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 15 -> {
                        player.closeInventory();
                        Utils.sendMessage(player, Messages.Usage.Help.POTION_HELP);
                        actionEditorState.startSession(player, ActionManager.ActionType.POTION);
                        actionEditorState.startChatTimeout(player, guiName, getTimeOut("potion", 30), editorState.getInventoryKey());
                    }

                    case 16 -> {
                        int index = gui.getEvents().size();
                        gui.addEvent(new UnGodEvent(index));
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 40 -> gui.open(player, GuiKeys.ACTION_ADD_1);

                }

            }

            case GuiKeys.DELETE_GUI -> {

                switch (slot) {

                    case 0,1,2,9,10,11,18,19,20 -> {

                        for (UUID uuid : editorStateManager.getPlayersInGui(guiName)){
                            Player p = Bukkit.getPlayer(uuid);
                            if (p != null) {
                                p.closeInventory();
                                editorStateManager.exit(p);
                            }
                        }
                        gui.deleteGuiFromYML(true);
                        registryGui.removeGui(guiName);
                        String msg = Messages.Usage.DELETE_TUTORIAL;
                        msg = msg.replace("%tutorial%", guiName);
                        player.sendMessage(msg);
                    }

                    case 6,7,8,15,16,17,24,25,26 -> gui.open(player, GuiKeys.ACTION_LIST + currentPage);

                }

            }

            case GuiKeys.SETTING_GUI_1 -> {

                switch (slot) {

                    case 11 -> {

                        boolean allowExitCommand = !gui.getAllowExitCommand();
                        gui.setAllowExitCommand(allowExitCommand);
                        openedInv.setItem(20, Utils.itemCreate(
                                allowExitCommand ? Material.LIME_DYE : Material.GRAY_DYE,
                                allowExitCommand ? "&aEnable" : "&cDisable"
                        ));

                    }

                    case 12 -> {

                        boolean lockHeadMovement = !gui.getLockHeadMovement();
                        gui.setLockHeadMovement(lockHeadMovement);
                        openedInv.setItem(21, Utils.itemCreate(
                                lockHeadMovement ? Material.LIME_DYE : Material.GRAY_DYE,
                                lockHeadMovement ? "&aEnable" : "&cDisable"
                        ));

                    }

                    case 13 -> {

                        boolean lockMovement = !gui.getLockMovement();
                        gui.setLockMovement(lockMovement);
                        openedInv.setItem(22, Utils.itemCreate(
                                lockMovement ? Material.LIME_DYE : Material.GRAY_DYE,
                                lockMovement ? "&aEnable" : "&cDisable"
                        ));
                        if (!lockMovement){
                            if (player.getGameMode() != GameMode.CREATIVE) {
                                player.setAllowFlight(false);
                                player.setFlying(false);
                            }
                        }

                    }

                    case 14 -> {

                        boolean disableSendChat = !gui.getDisableSendChat();
                        gui.setDisableSendChat(disableSendChat);
                        openedInv.setItem(23, Utils.itemCreate(
                                disableSendChat ? Material.LIME_DYE : Material.GRAY_DYE,
                                disableSendChat ? "&aEnable" : "&cDisable"
                        ));

                    }

                    case 15 -> {

                        boolean damageProtection = !gui.getDamageProtection();
                        gui.setDamageProtection(damageProtection);
                        openedInv.setItem(24, Utils.itemCreate(
                                damageProtection ? Material.LIME_DYE : Material.GRAY_DYE,
                                damageProtection ? "&aEnable" : "&cDisable"
                        ));


                    }

                    case 27 -> gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                    case 35 -> gui.open(player, GuiKeys.SETTING_GUI_2);

                }
            }

            case GuiKeys.SETTING_GUI_2 -> {

                switch (slot) {

                    case 12 -> {
                        boolean normalInvisible = !gui.getNormalInvisible();
                        gui.setNormalInvisible(normalInvisible);
                        openedInv.setItem(21, Utils.itemCreate(
                                normalInvisible ? Material.LIME_DYE : Material.GRAY_DYE,
                                normalInvisible ? "&aEnable" : "&cDisable"
                        ));
                    }

                    case 13 -> {
                        Utils.sendMessage(player, Messages.Usage.Help.EVENT_PLAYERINTERACT_HELP);
                        player.closeInventory();
                        actionEditorState.startSession(player, ActionManager.ActionType.EVENT_PLAYERINTERACT);
                        actionEditorState.startChatTimeout(player, guiName, 20, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 14 -> {
                        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
                            boolean proInvisible = !gui.getProInvisible();
                            gui.setProInvisible(proInvisible);
                            openedInv.setItem(23, Utils.itemCreate(
                                    proInvisible ? Material.LIME_DYE : Material.GRAY_DYE,
                                    proInvisible ? "&aEnable" : "&cDisable"
                            ));
                        } else {
                            player.sendMessage("&cProtoclLib not Founded");
                        }
                    }

                    case 35 -> gui.open(player, GuiKeys.SETTING_GUI_1);


                }

            }

            case GuiKeys.TELEPORT_GUI -> {

                EditorState state = editorStateManager.getState(player).orElse(null);
                if (state == null) return;

                String stateFormat = state.getInventoryKey();

                switch (slot) {

                    case 4 -> {

                        if (stateFormat.startsWith(GuiKeys.ACTION_LIST)){
                            gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                        } else if (stateFormat.equals(GuiKeys.ACTION_ADD_1))
                            gui.open(player, GuiKeys.ACTION_ADD_1);
                    }

                    case 1 -> {
                        if (stateFormat.startsWith(GuiKeys.ACTION_LIST)){
                            gui.setExitLocation(player.getLocation());
                            gui.open(player, GuiKeys.ACTION_LIST + currentPage);
                        } else if (stateFormat.equals(GuiKeys.ACTION_ADD_1)){
                            int index = gui.getEvents().size();
                            gui.addEvent(new TeleportEvent(index, player.getLocation()));
                            gui.open(player, GuiKeys.ACTION_ADD_1);
                        }
                    }

                    case 7 -> {
                        if (stateFormat.startsWith(GuiKeys.ACTION_LIST)){
                            player.closeInventory();
                            Utils.sendMessage(player, Messages.Usage.Help.TELEPORT_WORLD_HELP);
                            actionEditorState.startSession(player, ActionManager.ActionType.TELEPORT_EXIT);
                            actionEditorState.startChatTimeout(player, guiName, 12, editorState.getInventoryKey());
                        } else if (stateFormat.equals(GuiKeys.ACTION_ADD_1)){
                            player.closeInventory();
                            Utils.sendMessage(player, Messages.Usage.Help.TELEPORT_WORLD_HELP);
                            actionEditorState.startSession(player, ActionManager.ActionType.TELEPORT);
                            actionEditorState.startChatTimeout(player, guiName, 12, editorState.getInventoryKey());
                        }

                    }

                }

            }

            case GuiKeys.MOVE_EVENT -> {

                int n = editorState.getCurrentEventSelected();

                switch (slot) {

                    case 26 -> {
                        player.closeInventory();
                        player.sendMessage(Messages.Usage.SEND_PERM);
                        actionEditorState.startSession(player, ActionManager.ActionType.PERMISSION);
                        actionEditorState.startChatTimeout(player, guiName, 12, GuiKeys.ACTION_LIST + currentPage);
                    }

                    case 24 -> {

                        player.closeInventory();
                        TutorialEvents e = gui.getEvent(n);
                        e.execute(player);

                    }

                    case 22 -> gui.open(player, GuiKeys.ACTION_LIST + currentPage);

                    case 18 -> {

                        gui.removeEvent(n, player);
                        for (int i = 0; i < openedInv.getSize(); i++) {
                            openedInv.setItem(i, null);
                        }
                        gui.open(player, GuiKeys.ACTION_LIST + currentPage);

                    }

                    case 17 -> {
                        if (n + 1 < gui.getEvents().size()) {
                            gui.swapEvent(n, n + 1);
                            editorState.setCurrentEventSelected(n + 1);
                            gui.open(player, GuiKeys.MOVE_EVENT);
                        }
                    }

                    case 9 -> {
                        if (n - 1 >= 0) {
                            gui.swapEvent(n, n - 1);
                            editorState.setCurrentEventSelected(n - 1);
                            gui.open(player, GuiKeys.MOVE_EVENT);
                        }
                    }

                }

            }

        }
    }

    public int getTimeOut(String path, int def){
        return Main.getInstance().getConfig().getInt("timeout-enter-event." + path, def);
    }

}

package org.menentex.Tutorial.DataManager.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.menentex.Tutorial.DataManager.InventoryBuilder;
import org.menentex.Tutorial.Events.TutorialEvents;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

public class InMemoryGui implements Gui {

    private final String guiName;
    private final Map<String, Inventory> inventories = new HashMap<>();
    private final List<TutorialEvents> events = new ArrayList<>();

    private boolean disableSendChat = false;
    private boolean allowExitCommand = false;
    private boolean lockHeadMovement = false;
    private boolean lockMovement = false;
    private boolean damageProtection = false;
    private boolean professionalInvisible= false;
    private boolean normalInvisible = false;
    private boolean disablePlayerInteract = false;
    private EventListMananger.Player_Interact interact = EventListMananger.Player_Interact.DISABLE;
    private String actionBarExitMessage = "&7use &c&l/exit &7to leave tutorial";
    private String permission = "";

    private Villager npc;
    private Location npcLocation;

    private Location exitLocation;


    public InMemoryGui(String guiName){
        this.guiName = guiName;
    }

    public Map<String, Inventory> getInventories (){
        return inventories;
    }

    @Override
    public String getGuiName() {
        return guiName;
    }

    @Override
    public Inventory getInventory(String getKey) {
        return inventories.get(getKey);
    }

    @Override
    public void addInventory(String key, Inventory inventory) {
        inventories.put(key, inventory);
    }

    public void createDefaultInventory(){
        InventoryBuilder inventoryBuilder = new InventoryBuilder();
        inventories.put(GuiKeys.ACTION_LIST + 1, inventoryBuilder.createActionList(guiName, 1));
        inventories.put(GuiKeys.ACTION_ADD_1, inventoryBuilder.createActionAdd1(guiName));
        inventories.put(GuiKeys.ACTION_ADD_2, inventoryBuilder.createActionAdd2(guiName));
        inventories.put(GuiKeys.TELEPORT_GUI, inventoryBuilder.createTeleport(guiName));
        inventories.put(GuiKeys.DELETE_GUI, inventoryBuilder.createDelete(guiName));
        inventories.put(GuiKeys.SETTING_GUI_1, inventoryBuilder.createSetting_1(guiName));
        inventories.put(GuiKeys.SETTING_GUI_2, inventoryBuilder.createSetting_2(guiName));
        inventories.put(GuiKeys.MOVE_EVENT, inventoryBuilder.createMoveEvent(guiName));
    }

    @Override
    public Set<String> getKeys() {
        return inventories.keySet();
    }

    public long countActionLists() {
        return inventories.keySet().stream()
                .filter(key -> key.startsWith(GuiKeys.ACTION_LIST))
                .count();
    }

    @Override
    public void open(Player player, String key) {
        Inventory inv = inventories.get(key);
        if (inv != null) {
            buildRemainingPages(Main.getInstance().getRegistryGui());
            player.openInventory(inv);
        }
    }


    public void addEvent(TutorialEvents event) {
        event.setIndex(events.size());
        events.add(event);
    }

    public void removeEvent(int index, Player player) {
        if (index < 0 || index >= events.size()) return;

        events.remove(index);

        for (int i = index; i < events.size(); i++) {
            events.get(i).setIndex(i);
        }
        inventories.keySet().removeIf(key -> key.startsWith(GuiKeys.ACTION_LIST));
        buildAfterRemove(Main.getInstance().getRegistryGui(), player, index);
    }

    public TutorialEvents getEvent(int index){
        if (index < 0 || index >= events.size()) {
            return null;
        }
        return events.get(index);
    }

    public List<TutorialEvents> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void swapEvent(int i, int j) {
        if (i < 0 || j < 0 || i >= events.size() || j >= events.size()) return;

        Collections.swap(events, i, j);

        events.get(i).setIndex(i);
        events.get(j).setIndex(j);
        buildRemainingPages(Main.getInstance().getRegistryGui());
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FutureFeature {
        String value() default "Implementation coming later";
    }

    /**
     * Future feature – implementation will come in version 0.0.2
     */
    @FutureFeature("v0.0.2")
    public void spawnNpc(Location location){

        if (location.getWorld() == null) return;

        npc = (Villager) location.getWorld().spawn(location, Villager.class);
        npc.setAI(false);
        npc.setInvulnerable(false);

    }

    public boolean getDisableSendChat() {
        return disableSendChat;
    }

    public void setDisableSendChat(boolean disableSendChat) {
        this.disableSendChat = disableSendChat;
    }

    public boolean getAllowExitCommand() {
        return allowExitCommand;
    }

    public void setAllowExitCommand(boolean allowExitCommand) {
        this.allowExitCommand = allowExitCommand;
    }

    public boolean getLockHeadMovement() {
        return lockHeadMovement;
    }

    public void setLockHeadMovement(boolean lockHeadMovement) {
        this.lockHeadMovement = lockHeadMovement;
    }

    public boolean getLockMovement() {
        return lockMovement;
    }

    public void setLockMovement(boolean lockMovement) {
        this.lockMovement = lockMovement;
    }

    public boolean getDamageProtection() {
        return damageProtection;
    }

    public void setDamageProtection(boolean damageProtection) {
        this.damageProtection = damageProtection;
    }

    public Location getExitLocation(){
        return exitLocation;
    }

    public void setExitLocation(Location exitLocation){
        this.exitLocation = exitLocation;
    }

    public boolean getProInvisible(){
        return professionalInvisible;
    }

    public void setProInvisible(boolean professionalInvisible){
        this.professionalInvisible = professionalInvisible;
    }

    public boolean getNormalInvisible(){
        return normalInvisible;
    }

    public void setNormalInvisible(boolean normalInvisible){
        this.normalInvisible = normalInvisible;
    }

    public void setDisablePlayerInteract(boolean disablePlayerInteract) {
        this.disablePlayerInteract = disablePlayerInteract;
    }

    public boolean getDisablePlayerInteract() {
        return disablePlayerInteract;
    }

    public EventListMananger.Player_Interact getInteract() {
        return interact;
    }

    public void setInteract(EventListMananger.Player_Interact interact){
        this.interact = interact;
    }

    public String getActionBarExitMessage(){
        return actionBarExitMessage;
    }

    public void setActionBarExitMessage(String msg){
        this.actionBarExitMessage = msg;
    }

    public String getPermission(){
        return permission;
    }

    public void setPermission(String permission){
        this.permission = permission;
    }

    public void buildAfterRemove(RegistryGui registryGui, Player player, int removedIndex) {
        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if (gui == null) return;

        List<TutorialEvents> e = events;
        int sizePerInv = 36;

        InventoryBuilder inventoryBuilder = new InventoryBuilder();

        if (e.isEmpty()) {
            Inventory inv = inventoryBuilder.createActionList(guiName, 1);
            gui.addInventory(GuiKeys.ACTION_LIST + 1, inv);
            player.openInventory(inv);
            return;
        }

        int totalPages = (e.size() + sizePerInv - 1) / sizePerInv;

        for (int page = 0; page < totalPages; page++) {
            String pageKey = GuiKeys.ACTION_LIST + (page + 1);
            Inventory inv = inventoryBuilder.createActionList(guiName, page + 1);

            int start = page * sizePerInv;
            int end = Math.min(start + sizePerInv, e.size());

            for (int i = start; i < end; i++) {
                inv.setItem(i - start, Utils.convertEventToItem(e.get(i)));
            }

            gui.addInventory(pageKey, inv);
        }

        int currentPage = (removedIndex / sizePerInv) + 1;

        if (removedIndex % sizePerInv == 0 && currentPage > 1) {
            currentPage--;
        }

        Inventory targetInv = gui.getInventory(GuiKeys.ACTION_LIST + currentPage);
        if (targetInv != null) {
            player.openInventory(targetInv);
        }
    }


    public void buildRemainingPages(RegistryGui registryGui) {
        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if (gui == null) return;

        List<TutorialEvents> e = events;
        if (e.isEmpty()) return;

        int sizePerInv = 36;
        int totalPages = (e.size() + sizePerInv - 1) / sizePerInv;

        InventoryBuilder inventoryBuilder = new InventoryBuilder();

        for (int page = 1; page <= totalPages; page++) {

            String pageKey = GuiKeys.ACTION_LIST + page;

            Inventory inv = gui.getInventory(pageKey);

            if (inv == null) {
                inv = inventoryBuilder.createActionList(guiName, page);
                gui.addInventory(pageKey, inv);
            }

            int start = (page - 1) * sizePerInv;
            int end = Math.min(start + sizePerInv, e.size());

            for (int i = start; i < end; i++) {
                inv.setItem(i - start, Utils.convertEventToItem(e.get(i)));
            }
        }
    }

    public void setPermissionForEvent(int index, String perm){
        if (events.contains(events.get(index)))
            events.get(index).setPermission(perm);
    }

    public void saveToConfigSectionSync() {
        saveToConfigSection(false);
    }

    public void saveToConfigSectionAsync() {
        saveToConfigSection(true);
    }

    public void saveToConfigSection(boolean async) {
        ConfigurationSection rootSection = Main.getInstance().getTutorialsConfig().getConfigurationSection("tutorials");
        if (rootSection == null) {
            rootSection = Main.getInstance().getTutorialsConfig().createSection("tutorials");
        }

        ConfigurationSection guiSection =
                rootSection.getConfigurationSection(guiName);

        if (guiSection == null) {
            guiSection = rootSection.createSection(guiName);
        }

        guiSection.set("permission", permission);
        guiSection.set("disablePlayerInteract", disablePlayerInteract);
        EventListMananger.Player_Interact interact = getInteract();
        if (interact != null) {
            guiSection.set("interact", interact.name());
        } else {
            guiSection.set("interact", EventListMananger.Player_Interact.DISABLE.name());
        }
        guiSection.set("disableSendChat", disableSendChat);
        guiSection.set("allowExitCommand", allowExitCommand);
        if (actionBarExitMessage != null)
            guiSection.set("exit-actionbar-message", actionBarExitMessage);
        guiSection.set("lockHeadMovement", lockHeadMovement);
        guiSection.set("lockMovement", lockMovement);
        guiSection.set("damageProtection", damageProtection);
        guiSection.set("professionalInvisible", professionalInvisible);
        guiSection.set("normalInvisible", normalInvisible);

        if (exitLocation != null && exitLocation.getWorld() != null) {
            guiSection.set("exitLocation.world", exitLocation.getWorld().getName());
            guiSection.set("exitLocation.x", exitLocation.getX());
            guiSection.set("exitLocation.y", exitLocation.getY());
            guiSection.set("exitLocation.z", exitLocation.getZ());
            guiSection.set("exitLocation.yaw", exitLocation.getYaw());
            guiSection.set("exitLocation.pitch", exitLocation.getPitch());
        }

        ConfigurationSection eventsSection = guiSection.createSection("events");
        for (TutorialEvents event : events) {
            ConfigurationSection eventSection = eventsSection.createSection(String.valueOf(event.getIndex()));
            event.serialize(eventSection);

            if (event.getPermission() != null) {
                eventSection.set("permission", event.getPermission());
            }
        }

        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), Main.getInstance()::saveTutorials);
        } else {
            Main.getInstance().saveTutorials();
        }

    }

    public void deleteGuiFromYML(boolean async){
        String path = "tutorials." + guiName;
        Main.getInstance().getTutorialsConfig().set(path, null);
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), Main.getInstance()::saveTutorials);
        } else {
            Main.getInstance().saveTutorials();
        }
    }


}

package org.menentex.Tutorial.Listener;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.Events.*;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;


public class ActionHandler {

    private static ActionHandler instance;
    private final RegistryGui registryGui = Main.getInstance().getRegistryGui();

    private ActionHandler() {}

    public static ActionHandler getInstance() {
        if (instance == null) instance = new ActionHandler();
        return instance;
    }


    /* ===================== SIMPLE ACTIONS ===================== */

    public void createSetRotation(String guiName, float yaw, float pitch){
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new SetRotationEvent(gui.getEvents().size(), yaw, pitch));
    }

    public void createMessage(String guiName, String message) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new MessageEvent(gui.getEvents().size(), message));
    }

    public void createPermission(String guiName, int index, String message) {
        InMemoryGui gui = getGui(guiName);
        gui.setPermissionForEvent(index, message);
    }

    public void createDelay(String guiName, long ticks) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new DelayEvent(gui.getEvents().size(), ticks));
    }

    public void createPlayerCommand(String guiName, String command) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new PlayerCmdEvent(gui.getEvents().size(), command));
    }

    public void createConsoleCommand(String guiName, String command) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new ConsoleCmdEvent(gui.getEvents().size(), command));
    }

    public void createSetGameMode(String guiName, GameMode mode) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new SetGameModeEvent(gui.getEvents().size(), mode));
    }

    /* ===================== COMPLEX ACTIONS ===================== */

    public void createStrikeLightning(String guiName, Location location){
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new StrikeLightningEvent(gui.getEvents().size(), location));
    }

    public void createVector(String guiName, Utils.Direction direction, float multiply, float strength){
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new PushEvent(gui.getEvents().size(), direction, multiply, strength));
    }

    public void createBossBar(String guiName, String barMessage, BossBar.Overlay barStyle, BossBar.Color barColor, long duration){
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new BossBarEvent(gui.getEvents().size(), barMessage, barStyle, barColor, duration));
    }

    public void createCinematic(String guiName, Location from, Location to, float yaw, float pitch, long duration){
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new CinematicEvent(gui.getEvents().size(), from, to, yaw, pitch, duration));
    }

    public void createWaitRegionEnter(String guiName, Location pos1, Location pos2){
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new WaitRegionEnterEvent(gui.getEvents().size(), new CuboidRegion(pos1, pos2), guiName));
    }

    public void createTitle(String guiName, String title, String subtitle, int fadeIn, int duration, int fadeOut) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new TitleEvent(
                gui.getEvents().size(),
                title,
                subtitle,
                duration,
                fadeIn,
                fadeOut
        ));
    }

    public void createActionBar(String guiName, String message, long ticks) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new ActionBarEvent(gui.getEvents().size(), message, ticks));
    }

    public void createPotion(String guiName, PotionEffectType type, int level, long ticks) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new PotionEvent(gui.getEvents().size(), type, level, ticks));
    }

    public void createGiveItem(String guiName, Material material, int amount) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new GiveItemEvent(gui.getEvents().size(), material, amount));
    }

    public void createPlaySound(String guiName, Sound sound, float volume, float pitch) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new PlaySoundEvent(gui.getEvents().size(), sound, volume, pitch));
    }

    public void createTeleport(String guiName, Location location) {
        InMemoryGui gui = getGui(guiName);
        gui.addEvent(new TeleportEvent(gui.getEvents().size(), location));
    }


    public InMemoryGui getGui(String guiName){
        return registryGui.getGui(guiName).orElseThrow();
    }

}

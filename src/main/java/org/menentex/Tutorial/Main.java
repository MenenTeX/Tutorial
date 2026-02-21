package org.menentex.Tutorial;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.menentex.Tutorial.Commands.EditorCommands;
import org.menentex.Tutorial.Commands.PlayerCommands;
import org.menentex.Tutorial.Commands.TabComplete;
import org.menentex.Tutorial.DataManager.Gui.EventListMananger;
import org.menentex.Tutorial.DataManager.Gui.GuiLoader;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.Action.ActionEditorState;
import org.menentex.Tutorial.DataManager.Player.EditorStateManager;
import org.menentex.Tutorial.DataManager.Player.PlayerStateManager;
import org.menentex.Tutorial.Dependencie.ProtocollibDepend;
import org.menentex.Tutorial.Listener.*;
import org.menentex.Tutorial.Tasks.GuiTaskManager;
import org.menentex.Tutorial.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private static Main instance;

    private File messageFile;
    private FileConfiguration messageConfig;

    private File tutorialsFile;
    private FileConfiguration tutorialsConfig;

    public File blockedCommandFile;
    public FileConfiguration blockedCommandConfig;

    private RegistryGui registryGui;
    private EditorStateManager editorStateManager;
    private ActionEditorState actionEditorState;
    private GuiTaskManager guiTaskManager;
    private PlayerStateManager playerStateManager;
    private EventListMananger eventListMananger;

    private static ProtocollibDepend protocollibDepend;

    private Villager npc;

    @Override
    public void onEnable(){
        instance = this;
        Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        if (protocolLib == null || !protocolLib.isEnabled()) {
            getLogger().warning("ProtocolLib not Founf! Some features will be disabled.");
        } else {
            getLogger().info(Utils.colorize("&aProtocolLib detected successfully."));
            protocollibDepend = new ProtocollibDepend();
        }
        Plugin placeHolderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (placeHolderApi == null || !placeHolderApi.isEnabled()){
            getLogger().warning("PlaceholderAPI not Found ! You can't use PlaceHolders");
        } else
            getLogger().info(Utils.colorize("PlaceholderAPI detected successfully."));

        initBstats();

        registryGui = new RegistryGui();
        editorStateManager = new EditorStateManager();
        actionEditorState = new ActionEditorState();
        guiTaskManager = new GuiTaskManager();
        playerStateManager = new PlayerStateManager();
        eventListMananger = new EventListMananger();
        saveDefaultConfig();
        reloadConfig();
        loadTutorials();
        loadMessages();
        loadBlockCommand();
        new Messages();
        registerCommands();
        registerEvents();
        GuiLoader.loadGuisToRegistry(registryGui);
    }

    @Override
    public void onDisable(){
        if (getRegistryGui() == null) return;
        getLogger().info("TutorialCreator has been disabled. All data saved.");
        RegistryGui registryGui = getRegistryGui();
        for (InMemoryGui gui : registryGui.getAllGuis()){
            gui.saveToConfigSectionSync();
        }

    }

    public void initBstats() {
        new Metrics(this, 29627);
    }

    public void loadMessages(){
        messageFile = new File(getDataFolder(), "message.yml");
        if (!messageFile.exists()){
            saveResource("message.yml", false);
        }
        messageConfig = YamlConfiguration.loadConfiguration(messageFile);
    }

    public void loadTutorials(){
        tutorialsFile = new File(getDataFolder(), "tutorials.yml");
        if (!tutorialsFile.exists()){
            saveResource("tutorials.yml", false);
        }
        tutorialsConfig = YamlConfiguration.loadConfiguration(tutorialsFile);
    }

    public void loadBlockCommand(){
        blockedCommandFile = new File(getDataFolder(), "blockedcommand.yml");
        if (!blockedCommandFile.exists()){
            saveResource("blockedcommand.yml", false);
        }
        blockedCommandConfig = YamlConfiguration.loadConfiguration(blockedCommandFile);
    }

    private void registerCommands(){
        this.getCommand("tutoedit").setExecutor(new EditorCommands());
        this.getCommand("tutorial").setExecutor(new PlayerCommands());
        this.getCommand("tutoedit").setTabCompleter(new TabComplete());
        this.getCommand("tutorial").setTabCompleter(new TabComplete());
        this.getCommand("exit").setExecutor(new PlayerCommands());
    }


    private void registerEvents(){
        Bukkit.getPluginManager().registerEvents(new OpenInventoryHandler(), this);
        Bukkit.getPluginManager().registerEvents(new ClickInventoryHandler(), this);
        Bukkit.getPluginManager().registerEvents(new ActionMessage(), this);
        Bukkit.getPluginManager().registerEvents(new SettingEvents(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitServer(), this);

    }

    public void saveTutorial() {
        try {
            getTutorialsConfig().save(tutorialsFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + tutorialsFile, ex);
        }
    }

    public static Main getInstance(){
        return instance;
    }

    public FileConfiguration getMessageConfig(){
        return messageConfig;
    }

    public FileConfiguration getTutorialsConfig(){
        return tutorialsConfig;
    }

    public FileConfiguration getBlockedCommandConfig(){
        return blockedCommandConfig;
    }

    public RegistryGui getRegistryGui(){
        return registryGui;
    }

    public EditorStateManager getEditorStateManager(){
        return editorStateManager;
    }

    public ActionEditorState getActionEditorState() {
        return actionEditorState;
    }

    public GuiTaskManager getGuiTaskManager() {
        return guiTaskManager;
    }

    public PlayerStateManager getPlayerStateManager(){
        return playerStateManager;
    }

    public EventListMananger getEventListMananger() {
        return eventListMananger;
    }

    public ProtocollibDepend getProtocolib() {
        return protocollibDepend;
    }
}

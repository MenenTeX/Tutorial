package org.menentex.Tutorial.DataManager.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.menentex.Tutorial.Events.TutorialEvent;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiLoader {

    private static Map<String, List<TutorialEvent>> loadAllTutorials() {
        Map<String, List<TutorialEvent>> tutorials = new HashMap<>();

        ConfigurationSection root = Main.getInstance().getTutorialsConfig()
                .getConfigurationSection("tutorials");

        if (root == null) return tutorials;

        for (String guiName : root.getKeys(false)) {
            ConfigurationSection guiSection = root.getConfigurationSection(guiName);
            if (guiSection == null) continue;

            List<TutorialEvent> events = new ArrayList<>();
            ConfigurationSection eventsSection = guiSection.getConfigurationSection("events");
            if (eventsSection != null) {
                for (String key : eventsSection.getKeys(false)) {
                    ConfigurationSection eventSection = eventsSection.getConfigurationSection(key);
                    if (eventSection == null) continue;

                    TutorialEvent event = TutorialEvent.deserialize(eventSection);
                    if (event != null) {
                        events.add(event);
                    }
                }
            }

            tutorials.put(guiName, events);
        }

        return tutorials;
    }

    public static void loadGuisToRegistry(RegistryGui registry) {
        Map<String, List<TutorialEvent>> tutorials = GuiLoader.loadAllTutorials();

        for (Map.Entry<String, List<TutorialEvent>> entry : tutorials.entrySet()) {
            String guiName = entry.getKey();
            List<TutorialEvent> events = entry.getValue();

            InMemoryGui gui = new InMemoryGui(guiName);

            ConfigurationSection guiSection = Main.getInstance().getTutorialsConfig()
                    .getConfigurationSection("tutorials." + guiName);
            if (guiSection != null) {
                if (guiSection.getString("permission") != null)
                    gui.setPermission(guiSection.getString("permission"));
                gui.setDisableSendChat(guiSection.getBoolean("disableSendChat", false));
                gui.setDisablePlayerInteract(guiSection.getBoolean("disablePlayerInteract", false));
                gui.setInteract(Utils.nameToInteract(guiSection.getString("interact")));
                gui.setAllowExitCommand(guiSection.getBoolean("allowExitCommand", false));
                if (guiSection.getString("exit-actionbar-message") != null)
                    gui.setActionBarExitMessage(guiSection.getString("exit-actionbar-message"));
                gui.setActionBarExitMessage(guiSection.getString("exit-actionbar-message"));
                gui.setLockHeadMovement(guiSection.getBoolean("lockHeadMovement", false));
                gui.setLockMovement(guiSection.getBoolean("lockMovement", false));
                gui.setDamageProtection(guiSection.getBoolean("damageProtection", false));
                gui.setProInvisible(guiSection.getBoolean("professionalInvisible", false));
                gui.setNormalInvisible(guiSection.getBoolean("normalInvisible", false));

                if (guiSection.contains("exitLocation.world")) {
                    String worldName = guiSection.getString("exitLocation.world");
                    double x = guiSection.getDouble("exitLocation.x");
                    double y = guiSection.getDouble("exitLocation.y");
                    double z = guiSection.getDouble("exitLocation.z");
                    float yaw = (float) guiSection.getDouble("exitLocation.yaw");
                    float pitch = (float) guiSection.getDouble("exitLocation.pitch");

                    if (Bukkit.getWorld(worldName) != null) {
                        gui.setExitLocation(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
                    }
                }
            }

            for (TutorialEvent event : events) {
                gui.addEvent(event);
            }

            gui.createDefaultInventory();

            registry.registerGui(gui);
        }
    }


}

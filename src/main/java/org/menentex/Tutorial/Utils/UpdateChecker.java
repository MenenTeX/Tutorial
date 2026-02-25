package org.menentex.Tutorial.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.menentex.Tutorial.Commands.Permissions;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void check() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String latestVersion = reader.readLine();
                reader.close();

                String currentVersion = plugin.getDescription().getVersion();

                if (!currentVersion.equalsIgnoreCase(latestVersion)) {

                    String msg = Messages.UPDATE;
                    msg = msg.replace("%new-version%", latestVersion);
                    msg = msg.replace("%version%", currentVersion);

                    plugin.getLogger().info("There is a newer plugin version available:" + latestVersion);
                    plugin.getLogger().info("Current: " + currentVersion);
                    String finalMsg = msg;
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> {
                        if (Utils.hasPermission(player, false, Permissions.ADMIN))
                            Utils.sendMessage(player, Messages.PREFIX + finalMsg);
                    }));
                } else {
                    plugin.getLogger().info("You are on the highest version.");
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> {
                        if (Utils.hasPermission(player, false, Permissions.ADMIN))
                            Utils.sendMessage(player, Messages.PREFIX + Messages.LASTEST);
                    }));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates.");
            }

        });
    }
}

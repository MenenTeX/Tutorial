package org.menentex.Tutorial.Utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.menentex.Tutorial.Commands.Permissions;
import org.menentex.Tutorial.Messages;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    private boolean updateAvailable = false;
    private String latestVersion = null;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void check() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId)
                                .openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String latestVersionRaw = reader.readLine();
                reader.close();

                if (latestVersionRaw == null || latestVersionRaw.isBlank()) {
                    plugin.getLogger().warning("Update check returned empty version.");
                    return;
                }

                latestVersionRaw = normalize(latestVersionRaw);
                String currentVersionRaw = normalize(plugin.getDescription().getVersion());

                if (isNewer(latestVersionRaw, currentVersionRaw)) {
                    updateAvailable = true;
                    latestVersion = latestVersionRaw;

                    plugin.getLogger().info("New update available: " + latestVersionRaw);
                    plugin.getLogger().info("Current version: " + currentVersionRaw);
                } else {
                    updateAvailable = false;
                    plugin.getLogger().info("Plugin is up to date.");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates.");
            }
        });
    }

    public void notifyPlayer(Player player) {
        if (!updateAvailable) return;
        if (!player.hasPermission(Permissions.ADMIN)) return;

        String currentVersion = normalize(plugin.getDescription().getVersion());


        Component msg = Utils.applyPlaceholder(Messages.UPDATE, "%new-version%", latestVersion);
        player.sendMessage(
                Utils.applyPlaceholder(Messages.Usage.SAVE_TUTORIAL, "%version%", currentVersion)
        );

        Utils.sendMessagePrefix(player, msg);
    }

    private String normalize(String v) {
        if (v == null) return "";
        v = v.trim();
        if (v.startsWith("v") || v.startsWith("V")) v = v.substring(1);
        return v;
    }

    private boolean isNewer(String candidate, String current) {
        ParsedVersion c1 = parse(candidate);
        ParsedVersion c2 = parse(current);


        if (c1.major != c2.major) return c1.major > c2.major;
        if (c1.minor != c2.minor) return c1.minor > c2.minor;
        if (c1.patch != c2.patch) return c1.patch > c2.patch;

        if (!c1.beta && c2.beta) return true;

        if (c1.beta && c2.beta) return c1.betaNum > c2.betaNum;

        return false;
    }

    private ParsedVersion parse(String v) {

        String[] parts = v.split("-", 2);
        String core = parts[0];

        int major = 0, minor = 0, patch = 0;
        String[] nums = core.split("\\.");
        try {
            if (nums.length > 0) major = Integer.parseInt(nums[0]);
            if (nums.length > 1) minor = Integer.parseInt(nums[1]);
            if (nums.length > 2) patch = Integer.parseInt(nums[2]);
        } catch (NumberFormatException ignored) {
        }

        boolean beta = false;
        int betaNum = 0;

        if (parts.length == 2) {
            String suffix = parts[1].toLowerCase();
            if (suffix.startsWith("beta")) {
                beta = true;

                int dot = suffix.indexOf('.');
                if (dot != -1 && dot + 1 < suffix.length()) {
                    try {
                        betaNum = Integer.parseInt(suffix.substring(dot + 1));
                    } catch (NumberFormatException ignored) {
                        betaNum = 0;
                    }
                }
            }
        }

        return new ParsedVersion(major, minor, patch, beta, betaNum);
    }

    private static class ParsedVersion {
        final int major, minor, patch;
        final boolean beta;
        final int betaNum;

        ParsedVersion(int major, int minor, int patch, boolean beta, int betaNum) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.beta = beta;
            this.betaNum = betaNum;
        }
    }
}
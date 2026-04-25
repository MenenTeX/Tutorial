package org.menentex.Tutorial.Events;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class BossBarEvent extends TutorialEvent {

    private final String barMessage;
    private final BossBar.Overlay barStyle;
    private final BossBar.Color barColor;
    private final long duration;

    public BossBarEvent(int index, String barMessage,
                        BossBar.Overlay barStyle,
                        BossBar.Color barColor,
                        long duration) {

        super(index);
        this.barMessage = barMessage;
        this.barStyle = barStyle;
        this.barColor = barColor;
        this.duration = duration;
    }

    @Override
    public void execute(Player player) {

        String perm = getPermission();
        if (perm != null && !perm.isEmpty() &&
                !Utils.hasPermission(player, false, perm)) return;

        if (duration <= 0) return;

        BossBar bossBar = BossBar.bossBar(
                Utils.colorize(barMessage),
                1.0f,
                barColor,
                barStyle
        );

        player.showBossBar(bossBar);

        long totalSeconds = duration / 20;
        if (totalSeconds <= 0) totalSeconds = 1;

        long finalTotalSeconds = totalSeconds;

        new BukkitRunnable() {

            long secondsPassed = 0;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    cleanup();
                    cancel();
                    return;
                }

                secondsPassed++;

                double progress = 1.0 - ((double) secondsPassed / finalTotalSeconds);
                bossBar.progress((float) Math.max(progress, 0));

                if (secondsPassed >= finalTotalSeconds) {
                    cleanup();
                    cancel();
                }
            }

            private void cleanup() {
                player.hideBossBar(bossBar);
            }

        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }


    @Override
    public String getDisplayName() {
        return "BossBar";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("bar-message", barMessage);
        section.set("bar-style", barStyle.name());
        section.set("bar-color", barColor.name());
        section.set("duration", duration);
    }

    @Override
    public ItemStack createItemForInv() {
        return Utils.itemCreate(Material.WHEAT,
                "&6Boss Bar",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + index,
                        "&#3F9AAEBarMessage &#3F9AAE: &#F6CE71" + barMessage,
                        "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + Utils.formatTick(duration),
                        "&#3F9AAEBarColor &#3F9AAE: &#F6CE71" + barColor.name(),
                        "&#3F9AAEBarStyle &#3F9AAE: &#F6CE71" + barStyle.name()
                ), true);
    }

    public static BossBarEvent deserialize(int index, ConfigurationSection section) {

        String styleStr = section.getString("bar-style");
        String colorStr = section.getString("bar-color");
        String message = section.getString("bar-message");

        if (styleStr == null || colorStr == null || message == null)
            return null;

        BossBar.Overlay style;
        BossBar.Color color;

        try {
            style = BossBar.Overlay.valueOf(styleStr.toUpperCase());
            color = BossBar.Color.valueOf(colorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }

        long duration = section.getLong("duration");

        return new BossBarEvent(index, message, style, color, duration);
    }

}
package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.menentex.Tutorial.Messages;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class PotionEvent extends TutorialEvents{

    private final PotionEffectType potion;
    private final int level;
    private final long time;

    public PotionEvent (int index, PotionEffectType potion ,int level, long time){
        super((index));
        this.potion = potion;
        this.level = level;
        this.time = time;
    }

    public String getPotionName() {
        return potion.getName();
    }

    public PotionEffectType getPotion() { return potion;}

    public long getTime(){
        return time;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String getDisplayName() {
        return "Potion";
    }

    @Override
    public void execute(Player player) {
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        if (potion == null) {
            player.sendMessage(Utils.colorize(Messages.PREFIX + "&cInvalid potion type") + getPotionName());
            return;
        }

        if (level <= 0 || level > 256){
            player.sendMessage(Utils.colorize(Messages.PREFIX + "&cInvalid level"));
            return;
        }

        PotionEffect effect = new PotionEffect(
                potion,
                (int) time,
                level,
                false,
                true,
                true
        );

        player.addPotionEffect(effect);

    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("potion", getPotionName());
        section.set("level", level);
        section.set("time", time);
    }

    public static PotionEvent deserialize(int index, ConfigurationSection section) {
        String potionName = section.getString("potion");
        if (potionName == null) return null;

        PotionEffectType potion = PotionEffectType.getByName(potionName);
        if (potion == null) return null;

        int level = section.getInt("level");
        long time = section.getLong("time");

        return new PotionEvent(index, potion, level, time);
    }

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.POTION,
                "&6Potion",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex(),
                        "&#3F9AAEPotion &#3F9AAE: &#F6CE71" + getPotionName(),
                        "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + Utils.formatTick(getTime())
                ), false, ItemFlag.HIDE_ITEM_SPECIFICS);
    }

}

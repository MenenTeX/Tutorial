package org.menentex.Tutorial.Events;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class PlaySoundEvent extends TutorialEvent {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public PlaySoundEvent(int index, Sound sound, float volume, float pitch){
        super(index);
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSoundName() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public void execute(Player player){
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    @Override
    public String getDisplayName(){
        return "PlaySound";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("sound", sound.toString());
        section.set("volume", volume);
        section.set("pitch", pitch);
    }

    public static PlaySoundEvent deserialize(int index, ConfigurationSection section) {
        String soundName = section.getString("sound");
        if (soundName == null) return null;

        Sound sound;
        try {
            sound = Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            return null;
        }

        float volume = (float) section.getDouble("volume", 1.0);
        float pitch = (float) section.getDouble("pitch", 1.0);

        return new PlaySoundEvent(index, sound, volume, pitch);
    }

    @Override
    public ItemStack createItemForInv() {
        return Utils.itemCreate(Material.NOTE_BLOCK,
                "&6Play Sound",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex(),
                        "&#3F9AAESound &#3F9AAE: &#F6CE71" + getSoundName(),
                        "&#3F9AAEVolume &#3F9AAE: &#F6CE71" + getVolume(),
                        "&#3F9AAEPitch &#3F9AAE: &#F6CE71" + getPitch()

                ), false);
    }
}

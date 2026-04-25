package org.menentex.Tutorial.Events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class SetGameModeEvent extends TutorialEvent {

    private final GameMode gameMode;

    public SetGameModeEvent(int index, GameMode gameMode){
        super(index);
        this.gameMode = gameMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void execute(Player player) {
        String perm = getPermission();
        if (perm != null && !perm.isEmpty() && !Utils.hasPermission(player, false, perm)) return;
        player.setGameMode(gameMode);
    }

    @Override
    public String getDisplayName(){
        return "SetGameMode";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("gamemode", gameMode.name());
    }

    public static SetGameModeEvent deserialize(int index, ConfigurationSection section) {
        String gmName = section.getString("gamemode");
        if (gmName == null) return null;

        GameMode gameMode;
        try {
            gameMode = GameMode.valueOf(gmName);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return new SetGameModeEvent(index, gameMode);
    }

    @Override
    public ItemStack createItemForInv(){
        return Utils.itemCreate(Material.NETHER_STAR,
                "&6Set GameMode",
                List.of(
                        "",
                        "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + getIndex(),
                        "&#3F9AAEGameMode &#3F9AAE: &#F6CE71" + getGameMode().name()
                ), false);
    }


}

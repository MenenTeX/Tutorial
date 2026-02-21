package org.menentex.Tutorial.DataManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.menentex.Tutorial.DataManager.Gui.GuiInventoryHolder;
import org.menentex.Tutorial.DataManager.Gui.GuiKeys;

import java.util.ArrayList;
import java.util.List;

import static org.menentex.Tutorial.Utils.Utils.colorize;
import static org.menentex.Tutorial.Utils.Utils.itemCreate;

public class InventoryBuilder {

    public Inventory createActionList(String guiName, int index){

        Inventory inventory = Bukkit.createInventory(new GuiInventoryHolder(guiName, GuiKeys.ACTION_LIST + index), 54, colorize("&3&lTutorial Menu"));

        inventory.setItem(45, itemCreate(Material.ARROW, "&#f538f2Add Action", List.of("","&7With menu you can","&7add action to your tutorial")));
        inventory.setItem(46, itemCreate(Material.REPEATER, "&#3861f5Tutorial Settings"));
        inventory.setItem(47, itemCreate(Material.RED_BED, "&6Exit Location", List.of("",
                "&7With this feature you can",
                "&7determine where the player",
                "&7will spawn when exiting the Tutorial.")));
        inventory.setItem(48, itemCreate(Material.LAVA_BUCKET, "&cDelete Tutorial"));
        inventory.setItem(49, itemCreate(Material.CHAIN, "&bSave Tutorial", List.of("",
                "&7With this features you can",
                "&7save your tutorial in",
                "&7tutorial.yml")));
        inventory.setItem(50, itemCreate(Material.STRING,  "&#29e802Test Tutorial"));
        inventory.setItem(51, itemCreate(Material.PAPER, "&7Tutorial Name &c: &f" + guiName));

        for (int i = 36;i<=44;i++)
            inventory.setItem(i, itemCreate(Material.GRAY_STAINED_GLASS_PANE, null));

        char unicode = '\u00BB';
        inventory.setItem(52, itemCreate(Material.GREEN_WOOL, "&e" + unicode + "&aNext Page"));
        inventory.setItem(53, itemCreate(Material.RED_WOOL, "&e" + unicode + "&cPrevious Page"));
        return inventory;
    }

    public Inventory createSetting_1(String guiName){

        Inventory inventory = Bukkit.createInventory(new GuiInventoryHolder(guiName, GuiKeys.SETTING_GUI_1), 36, colorize("&3&lTutorial Menu"));

        inventory.setItem(11, itemCreate(Material.COMPASS, "&fAllow Exit Command"));
        inventory.setItem(12, itemCreate(Material.PLAYER_HEAD, "&fLock Head Movement"));
        inventory.setItem(13, itemCreate(Material.COBWEB, "&fLock Movement"));
        inventory.setItem(14, itemCreate(Material.FEATHER, "&fDisable Send Message"));
        inventory.setItem(15, itemCreate(Material.SHIELD, "&fDamage Protection"));

        inventory.setItem(20, itemCreate(Material.GRAY_DYE, null));
        inventory.setItem(21, itemCreate(Material.GRAY_DYE, null));
        inventory.setItem(22, itemCreate(Material.GRAY_DYE, null));
        inventory.setItem(23, itemCreate(Material.GRAY_DYE, null));
        inventory.setItem(24, itemCreate(Material.GRAY_DYE, null));

        inventory.setItem(35, itemCreate(Material.ARROW, "&aNext Page"));

        inventory.setItem(27, itemCreate(Material.BARRIER, "&cBack"));

        return inventory;
    }

    public Inventory createDelete(String guiName){

        Inventory inventory = Bukkit.createInventory(new GuiInventoryHolder(guiName, GuiKeys.DELETE_GUI), 27, colorize("&3&lTutorial Menu"));

        List<Integer> deleteNum = new ArrayList<>(List.of(0,1, 2, 9, 10, 11, 18, 19, 20));
        List<Integer> cancelNum = new ArrayList<>(List.of(6,7,8,15,16,17,24,25,26));

        for (int i = 0;i <= 26;i++){
            if (deleteNum.contains(i))
                inventory.setItem(i, itemCreate(Material.EMERALD_BLOCK, "&aDelete"));
            else if (cancelNum.contains(i))
                inventory.setItem(i, itemCreate(Material.REDSTONE_BLOCK, "&cCancel"));
        }

        inventory.setItem(13, itemCreate(Material.GOLDEN_HELMET, "&7Delete Tutorial &e"+ guiName, ItemFlag.HIDE_ATTRIBUTES));

        return inventory;
    }

    public Inventory createTeleport(String guiName){
        Inventory inventory = Bukkit.createInventory(new GuiInventoryHolder(guiName, GuiKeys.TELEPORT_GUI), 9, colorize("&3&lTutorial Menu"));

        inventory.setItem(1, itemCreate(Material.GLOWSTONE_DUST, "&6Use Here Location"));
        inventory.setItem(7, itemCreate(Material.PAPER, "&fType Location In Chat"));
        inventory.setItem(4, itemCreate(Material.BARRIER, "&cBack"));

        return inventory;
    }

    public Inventory createActionAdd1(String guiName){

        Inventory inventory  = Bukkit.createInventory(new GuiInventoryHolder(guiName, GuiKeys.ACTION_ADD_1), 45, colorize("&3&lTutorial Menu"));

        inventory.setItem(10, itemCreate(Material.ENDER_PEARL, "&eTeleport"));   // teleport .
        inventory.setItem(11, itemCreate(Material.PAPER, "&eMessage"));        // chat message.
        inventory.setItem(12, itemCreate(Material.BOOK, "&eTitle"));         // title.
        inventory.setItem(13, itemCreate(Material.CLOCK, "&eDelay"));        // delay.
        inventory.setItem(14, itemCreate(Material.COBWEB, "&eClear Chat"));          // clear chat.
        inventory.setItem(15, itemCreate(Material.NOTE_BLOCK, "&ePlay Sound"));    // play sound.
        inventory.setItem(16, itemCreate(Material.STICK, "&ePlayer Command"));        // player command.

        inventory.setItem(19, itemCreate(Material.BLAZE_ROD, "&eConsole Command"));     // console command.
        inventory.setItem(20, itemCreate(Material.DIAMOND, "&eAction Bar"));      // action bar.
        inventory.setItem(21, itemCreate(Material.NETHER_STAR, "&eSet GameMode"));   // set gamemode..

        inventory.setItem(22, itemCreate(Material.LEATHER_HELMET, "&eLock Head Movement", ItemFlag.HIDE_ATTRIBUTES));   // lock head movement.
        inventory.setItem(23, itemCreate(Material.FEATHER, "&eAllow Flight"));      // allow flying.
        inventory.setItem(24, itemCreate(Material.LEATHER_CHESTPLATE, "&eSet GodMode", ItemFlag.HIDE_ATTRIBUTES));   // god mode.
        inventory.setItem(25, itemCreate(Material.LEATHER_BOOTS, "&eLock Movement", ItemFlag.HIDE_ATTRIBUTES));        // lock movement.


        inventory.setItem(39, itemCreate(Material.BARRIER, "&cBack"));      // back
        inventory.setItem(41, itemCreate(Material.ARROW, "&aNext Page"));

        return inventory;
    }

    public Inventory createActionAdd2(String guiName){
        Inventory inventory = Bukkit.createInventory(new GuiInventoryHolder(guiName, GuiKeys.ACTION_ADD_2), 45, colorize("&3&lTutorial Menu"));

        inventory.setItem(10, itemCreate(Material.GOLDEN_HELMET, "&eUnLock Head Movement", ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(11, itemCreate(Material.GOLDEN_BOOTS, "&eUnLock Movement", ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(12, itemCreate(Material.ANVIL, "&eDisable Flight"));
        inventory.setItem(13, itemCreate(Material.BEACON, "&eGive Item"));
        inventory.setItem(14, itemCreate(Material.CAMPFIRE, "&eClear Inventory"));
        inventory.setItem(15, itemCreate(Material.POTION, "&eGive Potion", null, false, ItemFlag.HIDE_POTION_EFFECTS));
        inventory.setItem(16, itemCreate(Material.DRAGON_BREATH, "&eUnGod"));

        inventory.setItem(40, itemCreate(Material.BARRIER,"&cBack"));

        return inventory;
    }

    public Inventory createSetting_2(String guiName){
        Inventory inventory = Bukkit.createInventory(new GuiInventoryHolder(guiName, GuiKeys.SETTING_GUI_2), 36, colorize("&3&lTutorial Menu"));

        inventory.setItem(12, itemCreate(Material.GLASS_PANE, "&6Normal Invisibility",null, true));
        inventory.setItem(21, itemCreate(Material.GRAY_DYE, "&cDisable"));
        inventory.setItem(13, itemCreate(Material.GOLDEN_SHOVEL, "&fDisable Player Interact", List.of("&7Disable (&6Click-Right &7& &6Click-Left&7)"), false, ItemFlag.HIDE_ATTRIBUTES));
        inventory.setItem(22, itemCreate(Material.GRAY_DYE, "&cDisable"));
        inventory.setItem(14, itemCreate(Material.LECTERN, "&#FF3B30Professional Invisibility",List.of("&6&o&n(Recommended)","", "&7With This Feature You Can All","&7Item,Armor And Effect of the Player Invisible.", "","&cRequires ProtocolLib"), true));
        inventory.setItem(23, itemCreate(Material.GRAY_DYE, "&cDisable"));

        inventory.setItem(35, itemCreate(Material.BARRIER,"&cBack"));

        return inventory;
    }

    public Inventory createMoveEvent(String guiName){
        Inventory inventory = Bukkit.createInventory(new GuiInventoryHolder(guiName, GuiKeys.MOVE_EVENT), 27, colorize("&3&lTutorial Menu"));

        inventory.setItem(9, itemCreate(Material.RED_DYE, "&cMove Backward"));
        inventory.setItem(17, itemCreate(Material.GREEN_DYE, "&aMove Forward"));
        inventory.setItem(26, itemCreate(Material.BOOK, "&2Set Permission for Action"));
        inventory.setItem(18, itemCreate(Material.LAVA_BUCKET, "&4Remove Event"));
        inventory.setItem(4, itemCreate(Material.GLASS_PANE, "&fIndex &e:"));
        inventory.setItem(22, itemCreate(Material.BARRIER, "&cBack"));
        inventory.setItem(24, itemCreate(Material.SNOWBALL, "&fText Action"));

        return inventory;
    }

    public void createItemMoveEvent(Inventory inventory){
        inventory.setItem(9, itemCreate(Material.RED_DYE, "&cMove Backward"));
        inventory.setItem(17, itemCreate(Material.GREEN_DYE, "&aMove Forward"));
        inventory.setItem(26, itemCreate(Material.BOOK, "&2Set Permission for Action"));
        inventory.setItem(18, itemCreate(Material.LAVA_BUCKET, "&4Remove Event"));
        inventory.setItem(4, itemCreate(Material.GLASS_PANE, "&fIndex &e:"));
        inventory.setItem(22, itemCreate(Material.BARRIER, "&cBack"));
        inventory.setItem(24, itemCreate(Material.SNOWBALL, "&fText Action"));
    }

}

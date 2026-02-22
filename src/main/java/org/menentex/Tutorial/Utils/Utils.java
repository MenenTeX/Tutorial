package org.menentex.Tutorial.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.menentex.Tutorial.DataManager.Gui.EventListMananger;
import org.menentex.Tutorial.Events.*;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static ItemStack itemCreate(Material material, String display, ItemFlag ... itemFlags){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;

        itemMeta.setDisplayName(colorize(Objects.requireNonNullElse(display, "&f")));

        for (ItemFlag i : itemFlags){
            itemMeta.addItemFlags(i);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack itemCreate(Material material, String display){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;

        itemMeta.setDisplayName(colorize(Objects.requireNonNullElse(display, "&f")));

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack itemCreate(Material material, String display, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;

        itemMeta.setDisplayName(colorize(Objects.requireNonNullElse(display, "&f")));

        if (lore != null && !lore.isEmpty()){
            itemMeta.setLore(colorize(lore));
        }

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack itemCreate(Material material, String display, List<String> lore, boolean glow){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;

        itemMeta.setDisplayName(colorize(Objects.requireNonNullElse(display, "&f")));


        if (lore != null && !lore.isEmpty()){
            itemMeta.setLore(colorize(lore));
        }

        if (glow){
            itemMeta.addEnchant(Enchantment.LUCK, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack itemCreate(Material material, String display, List<String> lore, boolean glow, ItemFlag... hideFlags){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;

        itemMeta.setDisplayName(colorize(Objects.requireNonNullElse(display, "&f")));


        if (lore != null && !lore.isEmpty()){
            itemMeta.setLore(colorize(lore));
        }

        if (glow){
            itemMeta.addEnchant(Enchantment.LUCK, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        for (ItemFlag itemFlag : hideFlags){
            itemMeta.addItemFlags(itemFlag);
        }

        item.setItemMeta(itemMeta);
        return item;
    }

    public static boolean hasPermission(CommandSender sender, boolean sendMessage, String ... permissions){
        if (permissions == null || permissions.length == 0) return true;
        boolean hasPermission = false;
        for (String permission : permissions){
            if (permission == null || permission.isEmpty()) return true;
            if (sender.hasPermission(permission))
                hasPermission = true;
        }
        if (!hasPermission)
            if (sendMessage)
                sender.sendMessage(Messages.NEED_PERMISSION);
        return hasPermission;
    }

    public static boolean hasPermissions(Player player, boolean sendMessage, String ... permissions){
        return hasPermission((CommandSender) player, sendMessage, permissions);
    }

    public static String getMessage(String path){
        return Main.getInstance().getMessageConfig().getString(path);
    }

    public static String getFormat(String msg){
        return colorize(String.format("%s%s", Messages.PREFIX, msg));
    }

    public static List<String> getMessageList(String path){
        return Main.getInstance().getMessageConfig().getStringList(path);
    }

    public static Component colorizeComponent(String msg) {
        return LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build()
                .deserialize(msg);
    }

    public static String colorize(String msg){
        if (msg == null) return "";
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(msg);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()){
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hex).toString());
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static List<String> colorize(List<String> messages){
        if (messages == null) return new ArrayList<>();

        List<String> colored = new ArrayList<>();
        for (String line : messages){
            colored.add(colorize(line));
        }
        return colored;
    }

    public static ItemStack convertEventToItem(TutorialEvents event){

        String name = event.getDisplayName();

        ItemStack item = null;

        switch (name.toLowerCase()){
            case "title" -> {
                if (event instanceof TitleEvent titleEvent){
                    item = itemCreate(Material.BOOK,
                            "&6Title",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + titleEvent.getIndex(),
                                    "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + formatTick(titleEvent.getDuration()),
                                    "&#3F9AAETitle &#3F9AAE: &#F6CE71" + titleEvent.getTitle(),
                                    "&#3F9AAESubtitle &#3F9AAE: &#F6CE71" + titleEvent.getSubtitle(),
                                    "&#3F9AAEFadeIn &#3F9AAE: &#F6CE71" + formatTick(titleEvent.getFadeIn()),
                                    "&#3F9AAEFadeOut &#3F9AAE: &#F6CE71" + formatTick(titleEvent.getFadeOut())
                            ));
                }
            }
            case "teleport" -> {
                if (event instanceof TeleportEvent teleportEvent){
                    String world = teleportEvent.getLocation().getWorld().getName();
                    int x = teleportEvent.getLocation().getBlockX();
                    int y = teleportEvent.getLocation().getBlockY();
                    int z = teleportEvent.getLocation().getBlockZ();
                    float yaw = teleportEvent.getLocation().getYaw();
                    float pitch = teleportEvent.getLocation().getPitch();
                    item = itemCreate(Material.ENDER_PEARL,
                            "&6Teleport",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + teleportEvent.getIndex(),
                                    "&#3F9AAEWorld: &#3F9AAE: &#F6CE71" + world,
                                    "&#3F9AAEX: &#3F9AAE: &#F6CE71" + x,
                                    "&#3F9AAEY: &#3F9AAE: &#F6CE71" + y,
                                    "&#3F9AAEZ: &#3F9AAE: &#F6CE71" + z,
                                    "&#3F9AAEYaw: &#3F9AAE: &#F6CE71" + Float.toString(yaw).substring(0, Math.min(Float.toString(yaw).length(), 6)),
                                    "&#3F9AAEPitch: &#3F9AAE: &#F6CE71" + Float.toString(pitch).substring(0, Math.min(Float.toString(pitch).length(), 5))
                            ));
                }
            }
            case "setgamemode" -> {
                if (event instanceof SetGameModeEvent setGameModeEvent){
                    item = itemCreate(Material.NETHER_STAR,
                            "&6Set GameMode",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + setGameModeEvent.getIndex(),
                                    "&#3F9AAEGameMode &#3F9AAE: &#F6CE71" + setGameModeEvent.getGameMode().name()
                            ));
                }
            }
            case "playsound" -> {
                if (event instanceof PlaySoundEvent playSoundEvent){
                    item = itemCreate(Material.NOTE_BLOCK,
                            "&6Play Sound",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + playSoundEvent.getIndex(),
                                    "&#3F9AAESound &#3F9AAE: &#F6CE71" + playSoundEvent.getSoundName(),
                                    "&#3F9AAEVolume &#3F9AAE: &#F6CE71" + playSoundEvent.getVolume(),
                                    "&#3F9AAEPitch &#3F9AAE: &#F6CE71" + playSoundEvent.getPitch()

                            ));
                }
            }
            case "playercommand" -> {
                if (event instanceof PlayerCmdEvent playerCmdEvent){
                    item = itemCreate(Material.STICK,
                            "&6Player Command",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + playerCmdEvent.getIndex(),
                                    "&#3F9AAECommand &#3F9AAE: &#F6CE71/" + playerCmdEvent.getCommand()
                            ));
                }
            }
            case "consolecommand" -> {
                if (event instanceof ConsoleCmdEvent consoleCmdEvent){
                    item = itemCreate(Material.BLAZE_ROD,
                            "&6Console Command",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + consoleCmdEvent.getIndex(),
                                    "&#3F9AAECommand &#3F9AAE: &#F6CE71/" + consoleCmdEvent.getCommand()
                            ));
                }
            }
            case "message" -> {
                if (event instanceof MessageEvent messageEvent){
                    item = itemCreate(Material.PAPER,
                            "&6Message",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + messageEvent.getIndex(),
                                    "&#3F9AAEMessage &#3F9AAE: &#F6CE71" + messageEvent.getMessage()
                            ));
                }
            }
            case "lockmovement" -> {
                if (event instanceof LockMovementEvent lockMovementEvent){
                    item = itemCreate(Material.LEATHER_BOOTS,
                            "&6LockMovement",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + lockMovementEvent.getIndex()
                            ),false, ItemFlag.HIDE_ATTRIBUTES);
                }
            }
            case "lockheadmovement" -> {
                if (event instanceof LockHeadMovementEvent lockHeadMovementEvent){
                    item = itemCreate(Material.LEATHER_HELMET,
                            "&6LockHeadMovement",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + lockHeadMovementEvent.getIndex()
                            ),false, ItemFlag.HIDE_ATTRIBUTES);
                }
            }
            case "godmode" -> {
                if (event instanceof GodModeEvent godModeEvent){
                    item = itemCreate(Material.LEATHER_CHESTPLATE,
                            "&6Godmode",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + godModeEvent.getIndex()
                            ),false, ItemFlag.HIDE_ATTRIBUTES
                            );
                }
            }
            case "fly" -> {
                if (event instanceof FlyEvent flyEvent){
                    item = itemCreate(Material.FEATHER,
                            "&6Fly",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + flyEvent.getIndex()
                            ));
                }
            }
            case "delay" -> {
                if (event instanceof DelayEvent delayEvent){
                    item = itemCreate(Material.CLOCK,
                            "&6Delay",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + delayEvent.getIndex(),
                                    "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + formatTick(delayEvent.getDuration())
                            ));
                }
            }
            case "clear" -> {
                if (event instanceof ClearChatEvent clearChatEvent){
                    item = itemCreate(Material.COBWEB,
                            "&6Clear Chat",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + clearChatEvent.getIndex()
                            ));
                }
            }
            case "actionbar" -> {
                if (event instanceof ActionBarEvent actionBarEvent){
                    item = itemCreate(Material.DIAMOND,
                            "&6ActionBar",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + actionBarEvent.getIndex(),
                                    "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + formatTick(actionBarEvent.getDuration()),
                                    "&#3F9AAEActionBar &#3F9AAE: &#F6CE71" + actionBarEvent.getMessage()
                            ));
                }
            }
            case "potion" -> {
                if (event instanceof  PotionEvent potionEvent){
                    item = itemCreate(Material.POTION,
                            "&6Potion",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + potionEvent.getIndex(),
                                    "&#3F9AAEPotion &#3F9AAE: &#F6CE71" + potionEvent.getPotionName(),
                                    "&#3F9AAEDuration &#3F9AAE: &#F6CE71" + formatTick(potionEvent.getTime())
                            ), false, ItemFlag.HIDE_POTION_EFFECTS);
                }
            }

            case "unlockheadmovement" -> {
                if (event instanceof UnLockHeadMovementEvent unLockHeadMovementEvent){
                    item = itemCreate(Material.GOLDEN_HELMET,
                            "&6UnLockHeadMovement",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + unLockHeadMovementEvent.getIndex()
                            ),false, ItemFlag.HIDE_ATTRIBUTES);
                }
            }

            case "unlockmovement" -> {
                if (event instanceof UnLockMovementEvent UnLockMovementEvent){
                    item = itemCreate(Material.GOLDEN_BOOTS,
                            "&6UnLockMovement",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + UnLockMovementEvent.getIndex()
                            ),false, ItemFlag.HIDE_ATTRIBUTES);
                }
            }

            case "unfly" -> {
                if (event instanceof UnFlyEvent unFlyEvent){
                    item = itemCreate(Material.ANVIL,
                            "&6UnFly",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + unFlyEvent.getIndex()
                            ));
                }
            }

            case "clearinventory" -> {
                if (event instanceof ClearInventoryEvent clearInventoryEvent){
                    item = itemCreate(Material.CAMPFIRE,
                            "&6ClearInventory",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + clearInventoryEvent.getIndex()
                            ));
                }
            }

            case "ungodmode" -> {
                if (event instanceof  UnGodEvent unGodEvent){
                    item = itemCreate(Material.DRAGON_BREATH,
                            "&6UnGod",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + unGodEvent.getIndex()
                            ));
                }
            }

            case "giveitem" -> {
                if (event instanceof  GiveItemEvent giveItemEvent){
                    item = itemCreate(Material.BEACON,
                            "&6GiveItem",
                            List.of(
                                    "",
                                    "&#3F9AAEIndex &#3F9AAE: &#F6CE71" + giveItemEvent.getIndex(),
                                    "&#3F9AAEItem &#3F9AAE: &#F6CE71" + giveItemEvent.getItemName(),
                                    "&#3F9AAEAmount &#3F9AAE: &#F6CE71" + giveItemEvent.getAmount()
                            ));
                }
            }

        }
        return item;
    }

    public static String formatTick(long ticks) {
        double totalSeconds = ticks / 20.0;

        if (totalSeconds < 60) {
            return String.format("%.2fs", totalSeconds);
        } else if (totalSeconds < 3600) {
            long minutes = (long) (totalSeconds / 60);
            double seconds = totalSeconds % 60;
            if (seconds == 0) {
                return String.format("%dm", minutes);
            } else {
                return String.format("%dm %.2fs", minutes, seconds);
            }
        } else {
            long hours = (long) (totalSeconds / 3600);
            long minutes = (long) ((totalSeconds % 3600) / 60);
            double seconds = totalSeconds % 60;
            String result = hours + "h";
            if (minutes > 0) result += " " + minutes + "m";
            if (seconds > 0) result += String.format(" %.2fs", seconds);
            return result;
        }
    }

    public static GameMode getGameMode(String gamemode){

        GameMode gameMode;

        switch (gamemode.toLowerCase()) {
            case "survival" -> gameMode = GameMode.SURVIVAL;
            case "creative" -> gameMode = GameMode.CREATIVE;
            case "spec", "spectator" -> gameMode = GameMode.SPECTATOR;
            case "adventure" -> gameMode = GameMode.ADVENTURE;
            default -> gameMode = null;
        }
        return gameMode;
    }

    public static Set<Integer> eventSlots (){
        Set<Integer> nums = new HashSet<>();
        for (int i = 0; i <= 36 ; i++){
            nums.add(i);
        }
        return nums;
    }

    public static void sendMessage(Player player, List<String> messages){
        if (messages == null) return;
        for (String line : messages){
            player.sendMessage(Messages.PREFIX + colorize(line));
        }
    }

    public static void sendMessage(Player player, String message){
        if (message == null) return;
        player.sendMessage(Utils.colorize(message));
    }

    public static long parseTimeToTick(String input) {
        if (input == null || input.isBlank())
            throw new IllegalArgumentException("Invalid Time Format");

        input = input.trim().toLowerCase();

        double multiplier = 20;

        char last = input.charAt(input.length() - 1);

        if (Character.isLetter(last)) {
            switch (last) {
                case 's' -> multiplier = 20;            // seconds
                case 'm' -> multiplier = 20 * 60;       // minutes
                case 'h' -> multiplier = 20 * 3600;     // hours
                case 't' -> multiplier = 1;             // tick
                default -> throw new IllegalArgumentException("Invalid Time Unit: " + last);
            }
            input = input.substring(0, input.length() - 1);
        }

        try {
            double value = Double.parseDouble(input);

            if (value < 0)
                throw new IllegalArgumentException("Time cannot be negative");

            double result = value * multiplier;

            if (result > Long.MAX_VALUE)
                throw new IllegalArgumentException("Time too large");

            return Math.round(result);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Number Format");
        }
    }


    public static PotionEffectType getPotionEffect(String input) {
        if (input == null) return null;
        if (!input.matches("[a-zA-Z0-9_ ]+")) return null;
        String key = input.trim().replace(" ", "_").toLowerCase();
        try {
            return PotionEffectType.getByKey(NamespacedKey.minecraft(key));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String applyPlaceholders(String text, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

    public static Map<String, String> placeholders(Player player, World world, String serverName){
        Map<String, String> list = new HashMap<>();
        list.put("%player%", player.getName());
        list.put("%world_name%", world.getName());
        list.put("%player_health%", String.valueOf(player.getHealth()));
        list.put("%server_name%", serverName);
        return list;
    }

    public static EventListMananger.Player_Interact nameToInteract(String input){
        if (input == null) return EventListMananger.Player_Interact.DISABLE;

        if (input.equalsIgnoreCase(EventListMananger.Player_Interact.LEFT.name()))
            return EventListMananger.Player_Interact.LEFT;

        if (input.equalsIgnoreCase(EventListMananger.Player_Interact.RIGHT.name()))
            return EventListMananger.Player_Interact.RIGHT;

        if (input.equalsIgnoreCase(EventListMananger.Player_Interact.BOTH.name()))
            return EventListMananger.Player_Interact.BOTH;

        if (input.equalsIgnoreCase(EventListMananger.Player_Interact.DISABLE.name()))
            return EventListMananger.Player_Interact.DISABLE;

        return EventListMananger.Player_Interact.DISABLE;
    }


}

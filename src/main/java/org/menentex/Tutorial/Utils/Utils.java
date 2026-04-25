package org.menentex.Tutorial.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.intellij.lang.annotations.RegExp;
import org.menentex.Tutorial.DataManager.EventListMananger;
import org.menentex.Tutorial.Events.*;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;

import java.util.*;

public class Utils {

    public static ItemStack itemCreate(
            Material material,
            String display,
            List<String> lore,
            Boolean glow,
            ItemFlag... flags
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.displayName(colorize(Objects.requireNonNullElse(display, "&f")));

        if (lore != null && !lore.isEmpty()) {
            meta.lore(colorize(lore));
        }

        if (glow != null && glow) {
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (flags != null) {
            for (ItemFlag flag : flags) {
                meta.addItemFlags(flag);
            }
        }

        item.setItemMeta(meta);
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

    public static boolean hasPermission(Player player, boolean sendMessage, String ... permissions){
        return hasPermission((CommandSender) player, sendMessage, permissions);
    }

    public static Component getMessage(String path){
        String msg = Main.getInstance().getMessageConfig().getString(path);
        if (msg == null) return Component.empty();
        return Component.text(msg);
    }

    public static Component getFormat(Component msg) {
        if (msg == null) return Messages.PREFIX;

        return Component.empty()
                .append(Messages.PREFIX)
                .append(colorize(PlainTextComponentSerializer.plainText().serialize(msg)));
    }

    public static List<String> getMessageList(String path){
        return Main.getInstance().getMessageConfig().getStringList(path);
    }

    public static Component colorize(String msg) {
        return LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build()
                .deserialize(msg);
    }

    public static List<Component> colorize(List<String> messages) {
        if (messages == null || messages.isEmpty()) return List.of();

        List<Component> components = new ArrayList<>();
        for (String msg : messages) {
            components.add(colorize(msg));
        }
        return components;
    }

    public static ItemStack convertEventToItem(TutorialEvent event){

        String name = event.getDisplayName();

        ItemStack item = null;

        switch (name.toLowerCase()){
            case "title" -> {
                if (event instanceof TitleEvent titleEvent){
                    item = titleEvent.createItemForInv();
                }
            }
            case "teleport" -> {
                if (event instanceof TeleportEvent teleportEvent){
                    item = teleportEvent.createItemForInv();
                }
            }
            case "setgamemode" -> {
                if (event instanceof SetGameModeEvent setGameModeEvent){
                    item = setGameModeEvent.createItemForInv();
                }
            }
            case "playsound" -> {
                if (event instanceof PlaySoundEvent playSoundEvent){
                    item = playSoundEvent.createItemForInv();
                }
            }
            case "playercommand" -> {
                if (event instanceof PlayerCmdEvent playerCmdEvent){
                    item = playerCmdEvent.createItemForInv();
                }
            }
            case "consolecommand" -> {
                if (event instanceof ConsoleCmdEvent consoleCmdEvent){
                    item = consoleCmdEvent.createItemForInv();
                }
            }
            case "message" -> {
                if (event instanceof MessageEvent messageEvent){
                    item = messageEvent.createItemForInv();
                }
            }
            case "lockmovement" -> {
                if (event instanceof LockMovementEvent lockMovementEvent){
                    item = lockMovementEvent.createItemForInv();
                }
            }
            case "lockheadmovement" -> {
                if (event instanceof LockHeadMovementEvent lockHeadMovementEvent){
                    item = lockHeadMovementEvent.createItemForInv();
                }
            }
            case "godmode" -> {
                if (event instanceof GodModeEvent godModeEvent){
                    item = godModeEvent.createItemForInv();
                }
            }
            case "fly" -> {
                if (event instanceof FlyEvent flyEvent){
                    item = flyEvent.createItemForInv();
                }
            }
            case "delay" -> {
                if (event instanceof DelayEvent delayEvent){
                    item = delayEvent.createItemForInv();
                }
            }
            case "clear" -> {
                if (event instanceof ClearChatEvent clearChatEvent){
                    item = clearChatEvent.createItemForInv();
                }
            }
            case "actionbar" -> {
                if (event instanceof ActionBarEvent actionBarEvent){
                    item = actionBarEvent.createItemForInv();
                }
            }
            case "potion" -> {
                if (event instanceof PotionEvent potionEvent){
                    item = potionEvent.createItemForInv();
                }
            }

            case "unlockheadmovement" -> {
                if (event instanceof UnLockHeadMovementEvent unLockHeadMovementEvent){
                    item = unLockHeadMovementEvent.createItemForInv();
                }
            }

            case "unlockmovement" -> {
                if (event instanceof UnLockMovementEvent unLockMovementEvent){
                    item = unLockMovementEvent.createItemForInv();
                }
            }

            case "unfly" -> {
                if (event instanceof UnFlyEvent unFlyEvent){
                    item = unFlyEvent.createItemForInv();
                }
            }

            case "clearinventory" -> {
                if (event instanceof ClearInventoryEvent clearInventoryEvent){
                    item = clearInventoryEvent.createItemForInv();
                }
            }

            case "ungodmode" -> {
                if (event instanceof UnGodEvent unGodEvent){
                    item = unGodEvent.createItemForInv();
                }
            }

            case "giveitem" -> {
                if (event instanceof GiveItemEvent giveItemEvent){
                    item = giveItemEvent.createItemForInv();
                }
            }

            case "waitregionenter" -> {
                if (event instanceof WaitRegionEnterEvent waitRegionEnterEvent){
                    item = waitRegionEnterEvent.createItemForInv();
                }
            }

            case "cinematic" -> {
                if (event instanceof CinematicEvent cinematicEvent){
                    item = cinematicEvent.createItemForInv();
                }
            }

            case "setrotation" -> {
                if (event instanceof SetRotationEvent setRotationEvent){
                    item = setRotationEvent.createItemForInv();
                }
            }

            case "bossbar" -> {
                if (event instanceof BossBarEvent bossBarEvent){
                    item = bossBarEvent.createItemForInv();
                }
            }

            case "push" -> {
                if (event instanceof PushEvent pushEvent){
                    item = pushEvent.createItemForInv();
                }
            }

            case "strikelightning" -> {
                if (event instanceof  StrikeLightningEvent strikeLightningEvent){
                    item = strikeLightningEvent.createItemForInv();
                }
            }

        }
        return item;
    }

    public static List<Integer> findAllSlotsByPDC(Player player, String keyString) {

        NamespacedKey key = new NamespacedKey(Main.getInstance(), keyString);
        List<Integer> slots = new ArrayList<>();

        PlayerInventory inventory = player.getInventory();

        for (int slot = 0; slot < inventory.getSize(); slot++) {

            ItemStack item = inventory.getItem(slot);
            if (item == null || !item.hasItemMeta()) continue;

            if (item.getItemMeta().getPersistentDataContainer().getKeys().contains(key)) {
                slots.add(slot);
            }
        }

        return slots;
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

    public static void sendMessageComponent(Player player, Component msg){
        if (msg == null) return;
        player.sendMessage(msg);
    }

    public static void sendMessageComponent(Player player, List<Component> msgs){
        if (msgs == null) return;
        for (Component component : msgs)
            player.sendMessage(component);
    }

    public static void sendMessage(Player player, List<String> messages){
        if (messages == null) return;
        for (String line : messages){
            player.sendMessage(colorize(line));
        }
    }

    public static void sendMessagePrefix(Player player, List<Component> messages){
        if (messages == null) return;
        for (Component line : messages){
            player.sendMessage(Messages.PREFIX.append(line));
        }
    }

    public static void sendMessagePrefix(Player player, Component message){
        if (message == null) return;
        player.sendMessage(Messages.PREFIX.append(message));
    }

    public static void sendMessagePrefixString(Player player, String message) {
        if (player == null || message == null || message.isEmpty())
            return;
        player.sendMessage(
                Messages.PREFIX
                        .append(colorize(message))
        );
    }

    public static void sendMessagePrefixString(Player player, List<String> messages) {
        if (player == null || messages == null || messages.isEmpty()) return;

        for (String line : messages) {
            if (line == null || line.isEmpty()) continue;

            player.sendMessage(
                    Messages.PREFIX
                            .append(colorize(line))
            );
        }
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

    public enum Direction {
        X,
        Y,
        Z,
        NEGATIVE_Z,
        NEGATIVE_X,
        NEGATIVE_Y
    }

    public static Direction nameToDirection(String name){
        if (name == null) return Direction.X;
        if (name.equalsIgnoreCase(Direction.Y.name()))
            return Direction.Y;

        if (name.equalsIgnoreCase(Direction.Z.name()))
            return Direction.Z;

        if (name.equalsIgnoreCase(Direction.X.name()))
            return Direction.X;

        if (name.equalsIgnoreCase(Direction.NEGATIVE_Y.name()))
            return Direction.NEGATIVE_Y;

        if (name.equalsIgnoreCase(Direction.NEGATIVE_Z.name()))
            return Direction.NEGATIVE_Z;

        if (name.equalsIgnoreCase(Direction.NEGATIVE_X.name()))
            return Direction.NEGATIVE_X;

        return null;
    }

    public static Vector getVector(Direction direction, float strength, float blockPush){
        Vector vector;

        switch (direction){

            case NEGATIVE_Y -> vector = new org.bukkit.util.Vector(0, -blockPush, 0).normalize().multiply(strength);
            case Y -> vector = new org.bukkit.util.Vector(0, blockPush, 0).normalize().multiply(strength);
            case NEGATIVE_Z -> vector = new org.bukkit.util.Vector(0, 0, -blockPush).multiply(strength);
            case Z -> vector = new org.bukkit.util.Vector(0, 0, blockPush).normalize().multiply(strength);
            case NEGATIVE_X -> vector = new org.bukkit.util.Vector(-blockPush, 0, 0).normalize().multiply(strength);
            default -> vector = new Vector(blockPush, 0, 0).normalize().multiply(strength);

        }
        return vector;
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

    public static ItemStack createWand(){
        ItemStack item = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.displayName(colorize("&r&bTutorial Wand"));
        itemMeta.getPersistentDataContainer().set(
                new NamespacedKey(Main.getInstance(), "tutorial_axe"),
                PersistentDataType.BYTE,
                (byte) 1
        );
        item.setItemMeta(itemMeta);
        return item;
    }

    public static Component withEvent(Component base, String eventName) {
        return base.replaceText(builder -> builder
                .match("%event%")
                .replacement(eventName)
        );
    }

    public static Component applyPlaceholder(Component base, @RegExp String key, String value) {
        return base.replaceText(builder -> builder
                .match(key)
                .replacement(value)
        );
    }
}

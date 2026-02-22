package org.menentex.Tutorial.Listener;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffectType;
import org.menentex.Tutorial.Action.ActionSession;
import org.menentex.Tutorial.DataManager.Gui.EventListMananger;
import org.menentex.Tutorial.DataManager.Gui.GuiKeys;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.Action.ActionEditorState;
import org.menentex.Tutorial.DataManager.Player.EditorState;
import org.menentex.Tutorial.DataManager.Player.EditorStateManager;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class ActionMessage implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        ActionEditorState actionEditorState = Main.getInstance().getActionEditorState();
        EditorStateManager editorStateManager = Main.getInstance().getEditorStateManager();

        ActionSession session = actionEditorState.getSession(player);
        if (session == null) return;

        EditorState editorState = editorStateManager.getState(player).orElse(null);
        if (editorState == null) return;

        String guiName = editorState.getGuiName();

        event.setCancelled(true);

        String message = event.getMessage();

        RegistryGui registryGui = Main.getInstance().getRegistryGui();
        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if (gui == null) return;

        if (message.equalsIgnoreCase("cancel") || message.equalsIgnoreCase("exit")) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> actionEditorState.endSession(player, gui));
            return;
        }

        if (message.startsWith("-") && message.length() == 1){
            message = "&7";
        }

        String finalMessage = message;

        switch (session.getType()) {

            case MESSAGE -> {

                if (session.getStep() == 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createMessage(guiName, finalMessage);
                        actionEditorState.endSession(player, gui);
                        String msg = Messages.Usage.CREATE_EVENT;
                        msg = msg.replace("%event%", "Message");
                        player.sendMessage(msg);
                    });
                }

            }

            case TELEPORT -> handleTeleport(player, session, guiName, finalMessage, gui, actionEditorState);

            case TITLE -> handleTitle(player, session, guiName, finalMessage, gui, actionEditorState);

            case DELAY -> handleDelay(player, guiName, finalMessage, gui);


            case PLAY_SOUND -> handlePlaySound(player, session, guiName, finalMessage, gui, actionEditorState);

            case PLAYER_COMMAND -> {
                if (session.getStep() == 0) {
                    for (String blocked : Main.getInstance().getBlockedCommandConfig().getStringList("blocked")){
                        if (finalMessage.toLowerCase().startsWith(blocked.toLowerCase())){
                            Utils.sendMessage(player, List.of("&cInvalid command", "&cThis command is blocked."));
                            return;
                        }
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createPlayerCommand(guiName, finalMessage);
                        actionEditorState.endSession(player, gui);
                        String msg = Messages.Usage.CREATE_EVENT;
                        msg = msg.replace("%event%", "PlayerCommand");
                        player.sendMessage(msg);
                    });
                }

            }

            case CONSOLE_COMMAND -> {
                if (session.getStep() == 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createConsoleCommand(guiName, finalMessage);
                        actionEditorState.endSession(player, gui);
                        String msg = Messages.Usage.CREATE_EVENT;
                        msg = msg.replace("%event%", "ConsoleCommand");
                        player.sendMessage(msg);
                    });
                }

            }

            case ACTION_BAR -> handleActionBar(player, session, guiName, finalMessage, gui, actionEditorState);


            case SET_GAMEMODE -> {
                GameMode gameMode = Utils.getGameMode(finalMessage);
                if (gameMode == null) {
                    Utils.sendMessage(player, "&cInvalid GameMode Name");
                    return;
                }

                if (session.getStep() == 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createSetGameMode(guiName, gameMode);
                        actionEditorState.endSession(player, gui);
                        String msg = Messages.Usage.CREATE_EVENT;
                        msg = msg.replace("%event%", "SetGameMode");
                        player.sendMessage(msg);
                    });
                }
            }

            case GIVE_ITEM -> handleGiveItem(player, session ,guiName, finalMessage, gui, actionEditorState);


            case POTION -> handlePotion(player, session, guiName, finalMessage, gui, actionEditorState);

            case PERMISSION -> {
                if (session.getStep() == 0){
                    if (!finalMessage.matches("^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
                        Utils.sendMessage(player, List.of("&cInvalid permission !", "&7Please enter permission like this (tutorial&c.&7admin)"));
                        return;
                    }
                    String msg = Messages.Usage.SET_PERM;
                    msg = msg.replace("%perm%", finalMessage);
                    player.sendMessage(msg);
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createPermission(guiName, editorState.getCurrentEventSelected(), finalMessage);
                        actionEditorState.endSession(player, gui);
                    });
                }
            }

            case EVENT_PLAYERINTERACT -> {
                if (session.getStep() == 0){
                    switch (finalMessage.toLowerCase()){
                        case "right" -> {
                            gui.setInteract(EventListMananger.Player_Interact.RIGHT);
                            gui.setDisablePlayerInteract(true);
                        }
                        case "left" -> {
                            gui.setInteract(EventListMananger.Player_Interact.LEFT);
                            gui.setDisablePlayerInteract(true);
                        }
                        case "both" -> {
                            gui.setInteract(EventListMananger.Player_Interact.BOTH);
                            gui.setDisablePlayerInteract(true);
                        }
                        case "disable" -> {
                            gui.setInteract(EventListMananger.Player_Interact.DISABLE);
                            gui.setDisablePlayerInteract(false);
                        }
                        default -> {
                            Utils.sendMessage(player, List.of("&cInvalid Input", "&7Please enter input like this (&6Right&7 , &6Left &7, &6Both&7, &6Disable &7)"));
                            return;
                        }
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        actionEditorState.endSession(player, gui);
                        gui.open(player, GuiKeys.SETTING_GUI_2);
                    });
                }
            }
        }
    }

    private void handleGiveItem(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {

        switch (session.getStep()) {

            case 0 -> {
                Material material = Material.matchMaterial(input.toUpperCase());
                if (material == null) {
                    Utils.sendMessage(player, Utils.colorize(List.of(
                            "&cInvalid Item Name",
                            "&cPlease try again or type <cancel> to cancel"
                    )));
                    return;
                }

                session.setInput1(input);
                session.nextStep();
                Utils.sendMessage(player, Messages.Usage.Help.AMOUNT_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("giveitem", 15));
            }

            case 1 -> {
                int amount;
                try {
                    amount = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    Utils.sendMessage(player, Utils.colorize(List.of(
                            "&cInvalid Number Format",
                            "&cPlease try again or type <cancel> to cancel"
                    )));
                    return;
                }

                if (amount <= 0) amount = 1;

                Material material = Material.matchMaterial(session.getInput1().toUpperCase());
                if (material == null) {
                    Utils.sendMessage(player, Utils.colorize(List.of(
                            "&cInvalid Item Name",
                            "&cPlease try again or type <cancel> to cancel"
                    )));
                    return;
                }

                int finalAmount = amount;
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    ActionHandler.getInstance().createGiveItem(guiName, material, finalAmount);
                    Main.getInstance().getActionEditorState().endSession(player, gui);

                    String msg = Messages.Usage.CREATE_EVENT;
                    msg = msg.replace("%event%", "GiveItem");
                    player.sendMessage(msg);
                });
            }
        }
    }



    private void handlePlaySound(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {

        switch (session.getStep()) {

            case 0 -> {
                // sound name
                Sound sound = null;
                for (Sound s : Sound.values()) {
                    if (s.name().equalsIgnoreCase(input)) {
                        sound = s;
                        break;
                    }
                }

                if (sound == null) {
                    Utils.sendMessage(player, Utils.colorize(List.of(
                            "&cInvalid Sound Name",
                            "&cPlease try again or type <cancel> to cancel"
                    )));
                    return;
                }

                session.setInput1(input);
                session.nextStep();
                Utils.sendMessage(player, Messages.Usage.Help.PITCH_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("playsound", 40));
            }

            case 1 -> {
                // pitch
                try {
                    Float.parseFloat(input);
                } catch (NumberFormatException e) {
                    Utils.sendMessage(player, Utils.colorize(List.of(
                            "&cInvalid Number Format",
                            "&cPlease try again or type <cancel> to cancel"
                    )));
                    return;
                }

                session.setInput2(input);
                session.nextStep();
                Utils.sendMessage(player, Messages.Usage.Help.VOLUME_HELP);
            }

            case 2 -> {
                float pitch;
                float volume;

                try {
                    pitch = Float.parseFloat(session.getInput2());
                    volume = Float.parseFloat(input);
                } catch (NumberFormatException e) {
                    Utils.sendMessage(player, Utils.colorize(List.of(
                            "&cInvalid Number Format",
                            "&cPlease try again or type <cancel> to cancel"
                    )));
                    return;
                }

                Sound sound = null;
                for (Sound s : Sound.values()) {
                    if (s.name().equalsIgnoreCase(session.getInput1())) {
                        sound = s;
                        break;
                    }
                }

                if (sound == null) {
                    Utils.sendMessage(player, Utils.colorize(List.of(
                            "&cInvalid Sound Name",
                            "&cPlease try again or type <cancel> to cancel"
                    )));
                    return;
                }

                Sound finalSound = sound;
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    ActionHandler.getInstance().createPlaySound(guiName, finalSound, volume, pitch);
                    Main.getInstance().getActionEditorState().endSession(player, gui);

                    String msg = Messages.Usage.CREATE_EVENT;
                    msg = msg.replace("%event%", "PlaySound");
                    player.sendMessage(msg);
                });
            }
        }
    }

    private void handleActionBar(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {
        if (session.getStep() == 0) {
            session.setInput1(input);
            session.nextStep();
            Utils.sendMessage(player, Messages.Usage.Help.ACTIONBAR_DURATION_HELP);
            actionEditorState.eventChatTimeout(player, guiName, getTimeOut("actionbar", 15));
            return;
        }

        long ticks;
        try {
            ticks = Utils.parseTimeToTick(input);
            if (ticks <= 0) throw new IllegalArgumentException();
        } catch (Exception e) {
            Utils.sendMessage(player, Utils.colorize(List.of(
                    "&cInvalid duration format!",
                    "&cPlease try again or type <cancel> to cancel"
            )));
            return;
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            ActionHandler.getInstance().createActionBar(guiName, session.getInput1(), ticks);
            Main.getInstance().getActionEditorState().endSession(player, gui);

            String msg = Messages.Usage.CREATE_EVENT;
            msg = msg.replace("%event%", "ActionBar");
            player.sendMessage(msg);
        });
    }


    private void handlePotion(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {

        switch (session.getStep()) {

            case 0 -> {
                PotionEffectType type = Utils.getPotionEffect(input);

                if (type == null) {
                    Utils.sendMessage(player, List.of(
                            "&cInvalid potion type!",
                            "&7Example: &eSPEED &7or &eFIRE_RESISTANCE"
                    ));
                    return;
                }

                session.setInput1(type.getKey().getKey());
                session.nextStep();

                Utils.sendMessage(player, Messages.Usage.Help.LEVEL_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("potion", 30));
            }

            case 1 -> {
                int level;

                try {
                    level = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    Utils.sendMessage(player, "&cLevel must be a number!");
                    return;
                }

                if (level < 1 || level > 255) {
                    Utils.sendMessage(player, "&cLevel must be between 1 and 255!");
                    return;
                }

                session.setInput2(String.valueOf(level));
                session.nextStep();

                Utils.sendMessage(player, Messages.Usage.Help.POTIONDURATION_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("potion", 30));
            }

            case 2 -> {
                long ticks;

                try {
                    ticks = Utils.parseTimeToTick(input);
                } catch (Exception e) {
                    Utils.sendMessage(player, List.of(
                            "&cInvalid duration format!",
                            "(example: &e5s &7or &e5m &7or &e1h &7)."
                    ));
                    return;
                }

                if (ticks <= 0) {
                    Utils.sendMessage(player, List.of(
                            "&cDuration must be greater than 0!",
                            "(example: &e5s &7or &e5m &7or &e1h &7)."
                    ));
                    return;
                }

                int level = Integer.parseInt(session.getInput2());
                PotionEffectType type =
                        PotionEffectType.getByKey(
                                NamespacedKey.minecraft(session.getInput1())
                        );

                if (type == null) {
                    Utils.sendMessage(player, "&cInvalid Potion Type!");
                    return;
                }

                int amplifier = level - 1;

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    ActionHandler.getInstance()
                            .createPotion(guiName, type, amplifier, ticks);

                    Main.getInstance()
                            .getActionEditorState()
                            .endSession(player, gui);

                    String msg = Messages.Usage.CREATE_EVENT;
                    msg = msg.replace("%event%", "Potion");
                    player.sendMessage(msg);
                });
            }
        }
    }



    private void handleDelay(Player player, String guiName, String input, InMemoryGui gui) {
        long ticks;

        try {
            ticks = Utils.parseTimeToTick(input);
            if (ticks <= 0) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            Utils.sendMessage(player, Utils.colorize(List.of(
                    "&cInvalid duration format!",
                    "&cPlease try again or type <cancel> to cancel"
            )));
            return;
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            ActionHandler.getInstance().createDelay(guiName, ticks);
            Main.getInstance().getActionEditorState().endSession(player, gui);

            String msg = Messages.Usage.CREATE_EVENT;
            msg = msg.replace("%event%", "Delay");
            player.sendMessage(msg);
        });
    }



    private void handleTitle(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {
        switch (session.getStep()) {
            case 0 -> {
                session.setInput1(input);
                session.nextStep();
                Utils.sendMessage(player, Messages.Usage.Help.SUBTITLE_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("title", 60));
            }
            case 1 -> {
                session.setInput2(input);
                session.nextStep();
                Utils.sendMessage(player, Messages.Usage.Help.TITLEDURATION_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("title", 60));
            }
            case 2 -> {
                String[] parts = input.split("\\s*,\\s*");
                if (parts.length != 3) {
                    Utils.sendMessage(player, Utils.colorize(List.of("&cInvalid timing format" , "&cPlease try again or type <cancel> to cancel")));
                    return;
                }
                try {
                    if (!parts[0].matches("^[0-9]+[smht]?$") || !parts[1].matches("^[0-9]+[smht]?$") || !parts[2].matches("^[0-9]+[smht]?$")){
                        Utils.sendMessage(player, Utils.colorize(List.of("&cInvalid Number Format" , "&cPlease try again or type <cancel> to cancel")));
                        return;
                    }
                    int fadeIn = (int) Utils.parseTimeToTick(parts[0]);
                    int duration = (int) Utils.parseTimeToTick(parts[1]);
                    int fadeOut = (int) Utils.parseTimeToTick(parts[2]);

                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createTitle(
                                guiName,
                                session.getInput1(),
                                session.getInput2(),
                                fadeIn,
                                duration,
                                fadeOut
                        );
                        Main.getInstance().getActionEditorState().endSession(player, gui);

                    });
                } catch (NumberFormatException e){
                    Utils.sendMessage(player, Utils.colorize(List.of("&cInvalid Number Format" , "&cPlease try again or type <cancel> to cancel")));
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    String msg = Messages.Usage.CREATE_EVENT;
                    msg = msg.replace("%event%", "Title");
                    player.sendMessage(msg);
                });
            }
        }
    }

    private void handleTeleport(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {
        try {
            switch (session.getStep()) {
                case 0 -> {
                    if (Bukkit.getWorld(input) == null) {
                        Utils.sendMessage(player, Utils.colorize(List.of("&cInvalid World Name", "&cPlease try again or type <cancel> to cancel")));
                        return;
                    }
                    session.setInput1(input);
                    session.nextStep();
                    Utils.sendMessage(player, Messages.Usage.Help.TELEPORT_LOCATION_HELP);
                    actionEditorState.eventChatTimeout(player, guiName, getTimeOut("teleport", 40));
                }
                case 1 -> {
                    String[] parts = input.trim().split("\\s*,\\s*");
                    if (parts.length < 3 || parts.length == 4 || parts.length > 5) {
                        Utils.sendMessage(player, Utils.colorize(List.of("&cInvalid coordinates format", "&cPlease try again or type <cancel> to cancel")));
                        return;
                    }

                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        int z = Integer.parseInt(parts[2]);

                        float yaw = (parts.length == 5) ? Float.parseFloat(parts[3]) : player.getLocation().getYaw();
                        float pitch = (parts.length == 5) ? Float.parseFloat(parts[4]) : player.getLocation().getPitch();

                        String worldName = session.getInput1();
                        World world = Bukkit.getWorld(worldName);
                        if (world == null) {
                            Utils.sendMessage(player, Utils.colorize(List.of("&cInvalid World Name", "&cPlease try again or type <cancel> to cancel")));
                            return;
                        }

                        Location loc = new Location(world, x, y, z, yaw, pitch);

                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            ActionHandler.getInstance().createTeleport(guiName, loc);
                            Main.getInstance().getActionEditorState().endSession(player, gui);
                        });

                    } catch (NumberFormatException e) {
                        Utils.sendMessage(player, Utils.colorize(List.of("&cInvalid number format in coordinates", "&cPlease try again or type <cancel> to cancel")));
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        String msg = Messages.Usage.CREATE_EVENT;
                        msg = msg.replace("%event%", "Teleport");
                        player.sendMessage(msg);
                    });
                }
            }
        } catch (Exception e) {
            Utils.sendMessage(player, Utils.colorize(List.of("&cAn error occurred", "&cPlease try again or type <cancel> to cancel")));
        }
    }

    public int getTimeOut(String path, int def){
        return Main.getInstance().getConfig().getInt("timeout-enter-event." + path, def);
    }

}

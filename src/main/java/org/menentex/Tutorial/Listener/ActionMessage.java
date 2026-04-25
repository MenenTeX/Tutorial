package org.menentex.Tutorial.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.menentex.Tutorial.Action.ActionEditorState;
import org.menentex.Tutorial.Action.ActionSession;
import org.menentex.Tutorial.DataManager.EventListMananger;
import org.menentex.Tutorial.DataManager.Gui.GuiKeys;
import org.menentex.Tutorial.DataManager.Gui.InMemoryGui;
import org.menentex.Tutorial.DataManager.Gui.RegistryGui;
import org.menentex.Tutorial.DataManager.Player.EditorState;
import org.menentex.Tutorial.DataManager.Player.EditorStateManager;
import org.menentex.Tutorial.Main;
import org.menentex.Tutorial.Messages;
import org.menentex.Tutorial.Utils.Utils;

import java.util.List;

public class ActionMessage implements Listener {

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {

        Player player = event.getPlayer();
        ActionEditorState actionEditorState = Main.getInstance().getActionEditorState();
        EditorStateManager editorStateManager = Main.getInstance().getEditorStateManager();

        ActionSession session = actionEditorState.getSession(player);
        if (session == null) return;

        EditorState editorState = editorStateManager.getState(player).orElse(null);
        if (editorState == null) return;

        String guiName = editorState.getGuiName();

        event.setCancelled(true);

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        RegistryGui registryGui = Main.getInstance().getRegistryGui();
        InMemoryGui gui = registryGui.getGui(guiName).orElse(null);
        if (gui == null) return;

        if (message.equalsIgnoreCase("cancel") || message.equalsIgnoreCase("exit")) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> actionEditorState.endSession(player, gui));
            return;
        }

        if (message.startsWith("-") && message.length() == 1) {
            message = "&7";
        }

        String finalMessage = message;

        switch (session.getType()) {

            case STRIKE_LIGHTNING -> handleStrikeLightning(player, session, guiName, finalMessage, gui, actionEditorState);

            case VECTOR -> {
                if (session.getStep() == 0) {
                    Utils.Direction dir = Utils.nameToDirection(finalMessage);
                    if (dir == null) return;
                    Utils.sendMessagePrefixString(player, List.of("&7Please Send Multiply", "&7Example &e: &7( &65 &7)"));
                    session.setInput1(dir.name());
                    session.nextStep();
                } else if (session.getStep() == 1) {
                    float pushBlock;
                    try {
                        pushBlock = Float.parseFloat(finalMessage);
                    } catch (NumberFormatException ignored) {
                        Utils.sendMessagePrefixString(player, List.of(
                                "&cInvalid Number Format",
                                "&cPlease try again or type <cancel> to cancel"
                        ));
                        return;
                    }
                    session.setInput2(String.valueOf(pushBlock));
                    session.nextStep();
                    Utils.sendMessagePrefixString(player, List.of("&7Please Send Push Amount", "&7Example &e: &7( &65 &7)"));
                } else if (session.getStep() == 2) {
                    float multiply;
                    try {
                        multiply = Float.parseFloat(finalMessage);
                    } catch (NumberFormatException ignored) {
                        Utils.sendMessagePrefixString(player, List.of(
                                "&cInvalid Number Format",
                                "&cPlease try again or type <cancel> to cancel"
                        ));
                        return;
                    }

                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        Utils.Direction dir = Utils.nameToDirection(session.getInput1());
                        float pushBlock = Float.parseFloat(session.getInput2());
                        if (dir == null) return;
                        ActionHandler.getInstance().createVector(guiName, dir, multiply, pushBlock);
                        actionEditorState.endSession(player, gui);
                    });

                }
            }

            case SETROTATION -> {
                if (session.getStep() == 0) {
                    float yaw;
                    float pitch;
                    String[] parts = message.split("\\s*,\\s*");
                    if (parts.length != 2) {
                        Utils.sendMessagePrefix(player, Messages.Usage.Help.YAW_PITCH_HELP);
                        return;
                    }
                    try {
                        yaw = Float.parseFloat(parts[0]);
                        pitch = Float.parseFloat(parts[1]);
                    } catch (NumberFormatException ignored) {
                        Utils.sendMessagePrefixString(player, List.of(
                                "&cInvalid Number Format",
                                "&cPlease try again or type <cancel> to cancel"
                        ));
                        return;
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createSetRotation(guiName, yaw, pitch);
                        player.sendMessage(
                                Utils.withEvent(Messages.Usage.CREATE_EVENT, "SetRotation")
                        );
                        actionEditorState.endSession(player, gui);
                    });
                }
            }

            case BOSS_BAR -> handleBossBar(player, session, guiName, finalMessage, gui, actionEditorState);

            case CINEMATIC -> {
                if (session.getStep() == 0) {
                    if (message.equalsIgnoreCase("end") || message.equalsIgnoreCase("submit")) {
                        if (session.getPos1() == null || session.getPos2() == null) {
                            Utils.sendMessagePrefixString(player, "&cPlease Click On the Ground");
                            return;
                        }
                        session.nextStep();
                        actionEditorState.eventChatTimeout(player, guiName, getTimeOut("cinematic", 15));
                        Utils.sendMessagePrefix(player, Messages.Usage.Help.YAW_PITCH_HELP);
                    }
                } else if (session.getStep() == 1) {
                    float yaw;
                    float pitch;
                    String[] parts = message.split("\\s*,\\s*");
                    if (parts.length != 2) return;
                    try {
                        yaw = Float.parseFloat(parts[0]);
                        pitch = Float.parseFloat(parts[1]);
                    } catch (NumberFormatException ignored) {
                        Utils.sendMessagePrefixString(player, List.of(
                                "&cInvalid Number Format",
                                "&cPlease try again or type <cancel> to cancel"
                        ));
                        return;
                    }
                    session.setInput1(String.valueOf(yaw));
                    session.setInput2(String.valueOf(pitch));
                    Utils.sendMessagePrefix(player, Messages.Usage.Help.DURATION_HELP);
                    session.nextStep();
                } else if (session.getStep() == 2) {
                    long ticks;
                    try {
                        ticks = Utils.parseTimeToTick(message);
                        if (ticks <= 0) throw new IllegalArgumentException();
                    } catch (IllegalArgumentException e) {
                        Utils.sendMessagePrefixString(player, List.of(
                                "&cInvalid duration format!",
                                "(example: &e5s &7or &e5m &7or &e1h &7).",
                                "&cPlease try again or type <cancel> to cancel"
                        ));
                        return;
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        float yaw = Float.parseFloat(session.getInput1());
                        float pitch = Float.parseFloat(session.getInput2());
                        ActionHandler.getInstance().createCinematic(guiName, session.getPos1(), session.getPos2(), yaw, pitch, ticks);
                        player.sendMessage(
                                Utils.withEvent(Messages.Usage.CREATE_EVENT, "Cinematic")
                        );
                        actionEditorState.endSession(player, gui);
                    });
                }
            }

            case WAIT_REGION_ENTER -> {
                if (session.getStep() == 0) {
                    actionEditorState.eventChatTimeout(player, guiName, getTimeOut("wait-region-enter", 15));
                    if (message.equalsIgnoreCase("end") || message.equalsIgnoreCase("submit")) {
                        if (session.getPos1() == null || session.getPos2() == null) {
                            Utils.sendMessagePrefixString(player, "&cPlease Click On the Ground");
                            return;
                        }
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            ActionHandler.getInstance().createWaitRegionEnter(guiName, session.getPos1(), session.getPos2());
                            actionEditorState.endSession(player, gui);
                            player.sendMessage(
                                    Utils.withEvent(Messages.Usage.CREATE_EVENT, "WaitRegionEnter")
                            );
                        });
                    }
                }
            }

            case MESSAGE -> {

                if (session.getStep() == 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createMessage(guiName, finalMessage);
                        actionEditorState.endSession(player, gui);
                        player.sendMessage(
                                Utils.withEvent(Messages.Usage.CREATE_EVENT, "Message")
                        );
                    });
                }
            }

            case TELEPORT -> handleTeleport(player, session, guiName, finalMessage, gui, actionEditorState);

            case TITLE -> handleTitle(player, session, guiName, finalMessage, gui, actionEditorState);

            case DELAY -> handleDelay(player, guiName, finalMessage, gui);

            case PLAY_SOUND -> handlePlaySound(player, session, guiName, finalMessage, gui, actionEditorState);

            case PLAYER_COMMAND -> {
                if (session.getStep() == 0) {

                    String cmd = finalMessage.startsWith("/") ? finalMessage.substring(1) : finalMessage;
                    String baseCommand = cmd.split(" ")[0].toLowerCase();

                    if (baseCommand.contains(":")) {
                        baseCommand = baseCommand.split(":")[1];
                    }

                    for (String blocked : Main.getInstance().getBlockedCommandConfig().getStringList("blocked")) {
                        if (baseCommand.equalsIgnoreCase(blocked.toLowerCase())) {
                            Utils.sendMessageComponent(player, Messages.Usage.BLOCKED_COMMAND);
                            return;
                        }
                    }

                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createPlayerCommand(guiName, finalMessage);
                        actionEditorState.endSession(player, gui);
                        player.sendMessage(
                                Utils.withEvent(Messages.Usage.CREATE_EVENT, "PlayerCommand")
                        );
                    });
                }
            }

            case CONSOLE_COMMAND -> {
                if (session.getStep() == 0) {

                    String cmd = finalMessage.startsWith("/") ? finalMessage.substring(1) : finalMessage;
                    String baseCommand = cmd.split(" ")[0].toLowerCase();

                    if (baseCommand.contains(":")) {
                        baseCommand = baseCommand.split(":")[1];
                    }

                    for (String blocked : Main.getInstance().getBlockedCommandConfig().getStringList("blocked")) {
                        if (baseCommand.equalsIgnoreCase(blocked.toLowerCase())) {
                            Utils.sendMessageComponent(player, Messages.Usage.BLOCKED_COMMAND);
                            return;
                        }
                    }

                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createConsoleCommand(guiName, finalMessage);
                        actionEditorState.endSession(player, gui);
                        player.sendMessage(
                                Utils.withEvent(Messages.Usage.CREATE_EVENT, "ConsoleCommand")
                        );
                    });
                }
            }

            case ACTION_BAR -> handleActionBar(player, session, guiName, finalMessage, gui, actionEditorState);

            case SET_GAMEMODE -> {
                GameMode gameMode = Utils.getGameMode(finalMessage);
                if (gameMode == null) {
                    Utils.sendMessagePrefixString(player, "&cInvalid GameMode Name");
                    return;
                }

                if (session.getStep() == 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createSetGameMode(guiName, gameMode);
                        actionEditorState.endSession(player, gui);
                        player.sendMessage(
                                Utils.withEvent(Messages.Usage.CREATE_EVENT, "SetGameMode")
                        );
                    });
                }
            }

            case GIVE_ITEM -> handleGiveItem(player, session, guiName, finalMessage, gui, actionEditorState);


            case POTION -> handlePotion(player, session, guiName, finalMessage, gui, actionEditorState);

            case PERMISSION -> {
                if (session.getStep() == 0) {
                    if (!finalMessage.matches("^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")) {
                        Utils.sendMessagePrefixString(player, List.of("&cInvalid permission !", "&7Please enter permission like this (tutorial&c.&7admin)"));
                        return;
                    }
                    player.sendMessage(
                            Utils.applyPlaceholder(Messages.Usage.SET_PERM, "%perm%", finalMessage)
                    );
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ActionHandler.getInstance().createPermission(guiName, editorState.getCurrentEventSelected(), finalMessage);
                        actionEditorState.endSession(player, gui);
                    });
                }
            }

            case EVENT_PLAYERINTERACT -> {
                if (session.getStep() == 0) {
                    switch (finalMessage.toLowerCase()) {
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
                            Utils.sendMessagePrefixString(player, List.of("&cInvalid Input", "&7Please enter input like this (&6Right&7 , &6Left &7, &6Both&7, &6Disable &7)"));
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
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid Item Name",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
                    return;
                }

                session.setInput1(input);
                session.nextStep();
                Utils.sendMessagePrefix(player, Messages.Usage.Help.AMOUNT_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("giveitem", 15));
            }

            case 1 -> {
                int amount;
                try {
                    amount = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid Number Format",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
                    return;
                }

                if (amount <= 0) amount = 1;

                Material material = Material.matchMaterial(session.getInput1().toUpperCase());
                if (material == null) {
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid Item Name",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
                    return;
                }

                int finalAmount = amount;
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    ActionHandler.getInstance().createGiveItem(guiName, material, finalAmount);
                    Main.getInstance().getActionEditorState().endSession(player, gui);

                    player.sendMessage(
                            Utils.withEvent(Messages.Usage.CREATE_EVENT, "GiveItem")
                    );
                });
            }
        }
    }


    private void handlePlaySound(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {

        switch (session.getStep()) {

            case 0 -> {
                Sound sound = null;
                for (Sound s : Sound.values()) {
                    if (s.name().equalsIgnoreCase(input)) {
                        sound = s;
                        break;
                    }
                }

                if (sound == null) {
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid Sound Name",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
                    return;
                }

                session.setInput1(input);
                session.nextStep();
                Utils.sendMessagePrefix(player, Messages.Usage.Help.PITCH_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("playsound", 40));
            }

            case 1 -> {
                try {
                    Float.parseFloat(input);
                } catch (NumberFormatException e) {
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid Number Format",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
                    return;
                }

                session.setInput2(input);
                session.nextStep();
                Utils.sendMessagePrefix(player, Messages.Usage.Help.VOLUME_HELP);
            }

            case 2 -> {
                float pitch;
                float volume;

                try {
                    pitch = Float.parseFloat(session.getInput2());
                    volume = Float.parseFloat(input);
                } catch (NumberFormatException e) {
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid Number Format",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
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
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid Sound Name",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
                    return;
                }

                Sound finalSound = sound;
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    ActionHandler.getInstance().createPlaySound(guiName, finalSound, volume, pitch);
                    Main.getInstance().getActionEditorState().endSession(player, gui);

                    player.sendMessage(
                            Utils.withEvent(Messages.Usage.CREATE_EVENT, "PlaySound")
                    );
                });
            }
        }
    }

    private void handleActionBar(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {
        if (session.getStep() == 0) {
            session.setInput1(input);
            session.nextStep();
            Utils.sendMessagePrefix(player, Messages.Usage.Help.DURATION_HELP);
            actionEditorState.eventChatTimeout(player, guiName, getTimeOut("actionbar", 15));
            return;
        }

        long ticks;
        try {
            ticks = Utils.parseTimeToTick(input);
            if (ticks <= 0) throw new IllegalArgumentException();
        } catch (Exception e) {
            Utils.sendMessagePrefixString(player, List.of(
                    "&cInvalid duration format!",
                    "(example: &e5s &7or &e5m &7or &e1h &7).",
                    "&cPlease try again or type <cancel> to cancel"
            ));
            return;
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            ActionHandler.getInstance().createActionBar(guiName, session.getInput1(), ticks);
            Main.getInstance().getActionEditorState().endSession(player, gui);

            player.sendMessage(
                    Utils.withEvent(Messages.Usage.CREATE_EVENT, "ActionBar")
            );
        });
    }


    private void handlePotion(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {

        switch (session.getStep()) {

            case 0 -> {
                PotionEffectType type = Utils.getPotionEffect(input);

                if (type == null) {
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid potion type!",
                            "&7Example: &eSPEED &7or &eFIRE_RESISTANCE"
                    ));
                    return;
                }

                session.setInput1(type.getKey().getKey());
                session.nextStep();

                Utils.sendMessagePrefix(player, Messages.Usage.Help.LEVEL_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("potion", 30));
            }

            case 1 -> {
                int level;

                try {
                    level = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    Utils.sendMessagePrefixString(player, "&cLevel must be a number!");
                    return;
                }

                if (level < 1 || level > 255) {
                    Utils.sendMessagePrefixString(player, "&cLevel must be between 1 and 255!");
                    return;
                }

                session.setInput2(String.valueOf(level));
                session.nextStep();

                Utils.sendMessagePrefix(player, Messages.Usage.Help.POTIONDURATION_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("potion", 30));
            }

            case 2 -> {
                long ticks;

                try {
                    ticks = Utils.parseTimeToTick(input);
                } catch (Exception e) {
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid duration format!",
                            "(example: &e5s &7or &e5m &7or &e1h &7).",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
                    return;
                }

                if (ticks <= 0) {
                    Utils.sendMessagePrefixString(player, List.of(
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
                    Utils.sendMessagePrefixString(player, "&cInvalid Potion Type!");
                    return;
                }

                int amplifier = level - 1;

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    ActionHandler.getInstance()
                            .createPotion(guiName, type, amplifier, ticks);

                    Main.getInstance()
                            .getActionEditorState()
                            .endSession(player, gui);

                    player.sendMessage(
                            Utils.withEvent(Messages.Usage.CREATE_EVENT, "Potion")
                    );
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
            Utils.sendMessagePrefixString(player, List.of(
                    "&cInvalid duration format!",
                    "(example: &e5s &7or &e5m &7or &e1h &7).",
                    "&cPlease try again or type <cancel> to cancel"
            ));
            return;
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            ActionHandler.getInstance().createDelay(guiName, ticks);
            Main.getInstance().getActionEditorState().endSession(player, gui);

            player.sendMessage(
                    Utils.withEvent(Messages.Usage.CREATE_EVENT, "Delay")
            );
        });
    }


    private void handleTitle(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {
        switch (session.getStep()) {
            case 0 -> {
                session.setInput1(input);
                session.nextStep();
                Utils.sendMessagePrefix(player, Messages.Usage.Help.SUBTITLE_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("title", 60));
            }
            case 1 -> {
                session.setInput2(input);
                session.nextStep();
                Utils.sendMessagePrefix(player, Messages.Usage.Help.TITLEDURATION_HELP);
                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("title", 60));
            }
            case 2 -> {
                String[] parts = input.split("\\s*,\\s*");
                if (parts.length != 3) {
                    Utils.sendMessagePrefixString(player, List.of("&cInvalid timing format", "&cPlease try again or type <cancel> to cancel"));
                    return;
                }
                try {
                    if (!parts[0].matches("^[0-9]+[smht]?$") || !parts[1].matches("^[0-9]+[smht]?$") || !parts[2].matches("^[0-9]+[smht]?$")) {
                        Utils.sendMessagePrefixString(player, List.of("&cInvalid Number Format", "&cPlease try again or type <cancel> to cancel"));
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
                } catch (NumberFormatException e) {
                    Utils.sendMessagePrefixString(player, List.of("&cInvalid Number Format", "&cPlease try again or type <cancel> to cancel"));
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(
                        Utils.withEvent(Messages.Usage.CREATE_EVENT, "Title")
                ));
            }
        }
    }

    private void handleTeleport(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {
        try {
            switch (session.getStep()) {
                case 0 -> {
                    if (Bukkit.getWorld(input) == null) {
                        Utils.sendMessagePrefixString(player, List.of("&cInvalid World Name", "&cPlease try again or type <cancel> to cancel"));
                        return;
                    }
                    session.setInput1(input);
                    session.nextStep();
                    Utils.sendMessagePrefix(player, Messages.Usage.Help.TELEPORT_LOCATION_HELP);
                    actionEditorState.eventChatTimeout(player, guiName, getTimeOut("teleport", 40));
                }
                case 1 -> {
                    String[] parts = input.trim().split("\\s*,\\s*");
                    if (parts.length < 3 || parts.length == 4 || parts.length > 5) {
                        Utils.sendMessagePrefixString(player, List.of("&cInvalid coordinates format", "&cPlease try again or type <cancel> to cancel"));
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
                            Utils.sendMessagePrefixString(player, List.of("&cInvalid World Name", "&cPlease try again or type <cancel> to cancel"));
                            return;
                        }

                        Location loc = new Location(world, x, y, z, yaw, pitch);

                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            ActionHandler.getInstance().createTeleport(guiName, loc);
                            Main.getInstance().getActionEditorState().endSession(player, gui);
                        });

                    } catch (NumberFormatException e) {
                        Utils.sendMessagePrefixString(player, List.of("&cInvalid number format in coordinates", "&cPlease try again or type <cancel> to cancel"));
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(
                            Utils.withEvent(Messages.Usage.CREATE_EVENT, "Teleport")
                    ));
                }
            }
        } catch (Exception e) {
            Utils.sendMessagePrefixString(player, List.of("&cAn error occurred", "&cPlease try again or type <cancel> to cancel"));
        }
    }

    private void handleStrikeLightning(Player player, ActionSession session, String guiName, String input, InMemoryGui gui, ActionEditorState actionEditorState) {
        try {
            switch (session.getStep()) {
                case 0 -> {
                    if (Bukkit.getWorld(input) == null) {
                        Utils.sendMessagePrefixString(player, List.of("&cInvalid World Name", "&cPlease try again or type <cancel> to cancel"));
                        return;
                    }
                    session.setInput1(input);
                    session.nextStep();
                    Utils.sendMessagePrefix(player, Messages.Usage.Help.TELEPORT_LOCATION_HELP);
                    actionEditorState.eventChatTimeout(player, guiName, getTimeOut("strike-lightning", 40));
                }
                case 1 -> {
                    String[] parts = input.trim().split("\\s*,\\s*");
                    if (parts.length < 3 || parts.length == 4 || parts.length > 5) {
                        Utils.sendMessagePrefixString(player, List.of("&cInvalid coordinates format", "&cPlease try again or type <cancel> to cancel"));
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
                            Utils.sendMessagePrefixString(player, List.of("&cInvalid World Name", "&cPlease try again or type <cancel> to cancel"));
                            return;
                        }

                        Location loc = new Location(world, x, y, z, yaw, pitch);

                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            ActionHandler.getInstance().createStrikeLightning(guiName, loc);
                            Main.getInstance().getActionEditorState().endSession(player, gui);
                        });

                    } catch (NumberFormatException e) {
                        Utils.sendMessagePrefixString(player, List.of("&cInvalid number format in coordinates", "&cPlease try again or type <cancel> to cancel"));
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.sendMessage(
                            Utils.withEvent(Messages.Usage.CREATE_EVENT, "StrikeLightning")
                    ));
                }
            }
        } catch (Exception e) {
            Utils.sendMessagePrefixString(player, List.of("&cAn error occurred", "&cPlease try again or type <cancel> to cancel"));
        }
    }

    private void handleBossBar(Player player,
                               ActionSession session,
                               String guiName,
                               String input,
                               InMemoryGui gui,
                               ActionEditorState actionEditorState) {

        switch (session.getStep()) {

            case 0 -> {
                session.setInput1(input);
                session.nextStep();

                Utils.sendMessagePrefix(player, Messages.Usage.Help.BOSSBAR_BARCOLOR_HELP);

                actionEditorState.eventChatTimeout(player, guiName, getTimeOut("bossbar", 30));
            }

            case 1 -> {
                BossBar.Color color;

                try {
                    color = BossBar.Color.valueOf(input.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Utils.sendMessagePrefixString(player, "&cInvalid BarColor!");
                    return;
                }

                session.setInput2(color.name());
                session.nextStep();

                Utils.sendMessagePrefix(player, Messages.Usage.Help.BOSSBAR_BARSTYLE_HELP);
            }

            case 2 -> {
                BossBar.Overlay style;

                try {
                    style = BossBar.Overlay.valueOf(input.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Utils.sendMessagePrefixString(player, "&cInvalid BarStyle!");
                    return;
                }

                session.setInput3(style.name());
                session.nextStep();

                Utils.sendMessagePrefix(player, Messages.Usage.Help.DURATION_HELP);
            }

            case 3 -> {
                long ticks;

                try {
                    ticks = Utils.parseTimeToTick(input);
                    if (ticks <= 0) throw new IllegalArgumentException();
                } catch (Exception e) {
                    Utils.sendMessagePrefixString(player, List.of(
                            "&cInvalid duration format!",
                            "(example: &e5s &7or &e5m &7or &e1h &7).",
                            "&cPlease try again or type <cancel> to cancel"
                    ));
                    return;
                }

                String message = session.getInput1();
                BossBar.Color color = BossBar.Color.valueOf(session.getInput2());
                BossBar.Overlay style = BossBar.Overlay.valueOf(session.getInput3());

                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {

                    ActionHandler.getInstance()
                            .createBossBar(guiName, message, style, color, ticks);

                    actionEditorState.endSession(player, gui);
                    player.sendMessage(
                            Utils.withEvent(Messages.Usage.CREATE_EVENT, "BossBar")
                    );
                });
            }
        }
    }

    public int getTimeOut(String path, int def) {
        return Main.getInstance().getConfig().getInt("timeout-enter-event." + path, def);
    }

}

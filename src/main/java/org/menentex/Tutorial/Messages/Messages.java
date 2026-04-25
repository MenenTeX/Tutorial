package org.menentex.Tutorial;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;

import static org.menentex.Tutorial.Utils.Utils.*;

public class Messages {

    public static Component PREFIX;
    public static Component CONSOLE_SENDER;
    public static List<Component> HELP;
    public static Component NEED_PERMISSION;
    public static Component RELOAD;
    public static Component UPDATE;
    public static Component LATEST;

    public Messages(){
        initialize();
        Usage.initialize();
        Usage.Help.initialize();
    }

    private static void initialize(){

        PREFIX = colorize(PlainTextComponentSerializer.plainText().serialize(getMessage("prefix")));
        CONSOLE_SENDER = getFormat(getMessage("console-sender"));
        HELP = new ArrayList<>();
        for (String l : getMessageList("help-command"))
            HELP.add(colorize(l));
        RELOAD = getFormat(getMessage("reload-success"));
        NEED_PERMISSION = getFormat(getMessage("need-permission"));
        UPDATE = getMessage("check-update");
        LATEST = getMessage("lastes-version");
    }


    public static class Usage {

        public static Component CREATE_TUTORIAL;
        public static Component DELETE_TUTORIAL;
        public static Component SAVE_TUTORIAL;
        public static Component SET_PERM;
        public static Component CREATE_EVENT;
        public static Component EXIT_COMMAND;
        public static Component DISABLE_SENDMESSAGE;
        public static Component SEND_PERM;
        public static Component NOTINTUTORIAL;
        public static Component NOT_FOUND;
        public static Component ALREADY_TUTORIAL;
        public static Component BLOCKED_COMMAND;
        public static Component FIRST_TUTORIAL;
        public static Component TUTORIAL_EMPTY;

        private static void initialize() {
            TUTORIAL_EMPTY = getFormat(getMessage("tutorial-empty"));
            DISABLE_SENDMESSAGE = getFormat(getMessage("disable-sendMessage"));
            CREATE_TUTORIAL = getFormat(getMessage("create-tutorial"));
            DELETE_TUTORIAL = getFormat(getMessage("delete-tutorial"));
            CREATE_EVENT = getFormat(getMessage("create-event"));
            SAVE_TUTORIAL = getFormat(getMessage("save-tutorial"));
            EXIT_COMMAND = getFormat(getMessage("exit-command"));
            SET_PERM = getFormat(getMessage("set-permission"));
            SEND_PERM = getFormat(getMessage("send-permission"));
            NOTINTUTORIAL = getFormat(getMessage("not-in-tutorial"));
            NOT_FOUND = getFormat(getMessage("not-found-tutorial"));
            ALREADY_TUTORIAL = getFormat(getMessage("already-tutorial"));
            BLOCKED_COMMAND = getFormat(getMessage("blocked-command"));
            FIRST_TUTORIAL = getFormat(getMessage("first-tutorial"));
        }


        public static class Help {

            public static List<Component> MESSAGE_HELP;
            public static List<Component> ACTIONBAR_HELP;
            public static List<Component> DURATION_HELP;
            public static List<Component> TITLE_HELP;
            public static List<Component> SUBTITLE_HELP;
            public static List<Component> TITLEDURATION_HELP;
            public static List<Component> DELAY_HELP;
            public static List<Component> CONSOLECMD_HELP;
            public static List<Component> PLAYERCMD_HELP;
            public static List<Component> PLAYERSOUND_HELP;
            public static List<Component> PITCH_HELP;
            public static List<Component> VOLUME_HELP;
            public static List<Component> GIVEITEM_HELP;
            public static List<Component> AMOUNT_HELP;
            public static List<Component> POTION_HELP;
            public static List<Component> LEVEL_HELP;
            public static List<Component> POTIONDURATION_HELP;
            public static List<Component> TELEPORT_WORLD_HELP;
            public static List<Component> TELEPORT_LOCATION_HELP;
            public static List<Component> SETGAMEMODE_HELP;
            public static List<Component> EVENT_PLAYERINTERACT_HELP;
            public static List<Component> YAW_PITCH_HELP;
            public static List<Component> BOSSBAR_BARCOLOR_HELP;
            public static List<Component> BOSSBAR_BARSTYLE_HELP;
            public static List<Component> DIRECTION_HELP;

            private static void initialize() {

                DIRECTION_HELP = new ArrayList<>();

                BOSSBAR_BARCOLOR_HELP = new ArrayList<>();
                BOSSBAR_BARSTYLE_HELP = new ArrayList<>();

                MESSAGE_HELP = new ArrayList<>();
                ACTIONBAR_HELP = new ArrayList<>();
                DURATION_HELP = new ArrayList<>();

                TITLE_HELP = new ArrayList<>();
                SUBTITLE_HELP = new ArrayList<>();
                TITLEDURATION_HELP = new ArrayList<>();

                DELAY_HELP = new ArrayList<>();

                CONSOLECMD_HELP = new ArrayList<>();
                PLAYERCMD_HELP = new ArrayList<>();

                PLAYERSOUND_HELP = new ArrayList<>();
                PITCH_HELP = new ArrayList<>();
                VOLUME_HELP = new ArrayList<>();

                GIVEITEM_HELP = new ArrayList<>();
                AMOUNT_HELP = new ArrayList<>();

                POTION_HELP = new ArrayList<>();
                LEVEL_HELP = new ArrayList<>();
                POTIONDURATION_HELP = new ArrayList<>();

                TELEPORT_WORLD_HELP = new ArrayList<>();
                TELEPORT_LOCATION_HELP = new ArrayList<>();

                SETGAMEMODE_HELP = new ArrayList<>();

                EVENT_PLAYERINTERACT_HELP = new ArrayList<>();

                YAW_PITCH_HELP = new ArrayList<>();

                for (String l : getMessageList("help.direction"))
                    DIRECTION_HELP.add(colorize(l));

                for (String l : getMessageList("help.bossbar-barcolor"))
                    BOSSBAR_BARCOLOR_HELP.add(colorize(l));

                for (String l : getMessageList("help.bossbar-barstyle"))
                    BOSSBAR_BARSTYLE_HELP.add(colorize(l));

                for (String l : getMessageList("help.message"))
                    MESSAGE_HELP.add(colorize(l));

                for (String l : getMessageList("help.actionbar-message"))
                    ACTIONBAR_HELP.add(colorize(l));

                for (String l : getMessageList("help.duration"))
                    DURATION_HELP.add(colorize(l));

                for (String l : getMessageList("help.title-title"))
                    TITLE_HELP.add(colorize(l));

                for (String l : getMessageList("help.title-subtitle"))
                    SUBTITLE_HELP.add(colorize(l));

                for (String l : getMessageList("help.title-timing"))
                    TITLEDURATION_HELP.add(colorize(l));

                for (String l : getMessageList("help.delay"))
                    DELAY_HELP.add(colorize(l));

                for (String l : getMessageList("help.console-command"))
                    CONSOLECMD_HELP.add(colorize(l));

                for (String l : getMessageList("help.player-command"))
                    PLAYERCMD_HELP.add(colorize(l));

                for (String l : getMessageList("help.play-sound-name"))
                    PLAYERSOUND_HELP.add(colorize(l));

                for (String l : getMessageList("help.play-sound-pitch"))
                    PITCH_HELP.add(colorize(l));

                for (String l : getMessageList("help.play-sound-volume"))
                    VOLUME_HELP.add(colorize(l));

                for (String l : getMessageList("help.give-item-name"))
                    GIVEITEM_HELP.add(colorize(l));

                for (String l : getMessageList("help.give-item-amount"))
                    AMOUNT_HELP.add(colorize(l));

                for (String l : getMessageList("help.potion-type"))
                    POTION_HELP.add(colorize(l));

                for (String l : getMessageList("help.potion-level"))
                    LEVEL_HELP.add(colorize(l));

                for (String l : getMessageList("help.potion-duration"))
                    POTIONDURATION_HELP.add(colorize(l));

                for (String l : getMessageList("help.teleport-world"))
                    TELEPORT_WORLD_HELP.add(colorize(l));

                for (String l : getMessageList("help.teleport-location"))
                    TELEPORT_LOCATION_HELP.add(colorize(l));

                for (String l : getMessageList("help.set-gamemode"))
                    SETGAMEMODE_HELP.add(colorize(l));

                for (String l : getMessageList("help.event-playerinteract"))
                    EVENT_PLAYERINTERACT_HELP.add(colorize(l));

                for (String l : getMessageList("help.yaw-pitch"))
                    YAW_PITCH_HELP.add(colorize(l));
            }
        }
    }

}

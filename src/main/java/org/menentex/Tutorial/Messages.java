package org.menentex.Tutorial;

import java.util.ArrayList;
import java.util.List;

import static org.menentex.Tutorial.Utils.Utils.*;

public class Messages {

    public static String PREFIX;
    public static String CONSOLE_SENDER;
    public static List<String> HELP;
    public static String NEED_PERMISSION;
    public static String RELOAD;
    public static String UPDATE;
    public static String LASTEST;

    public Messages(){
        initialize();
        Usage.initialize();
        Usage.Help.initialize();
    }

    private static void initialize(){

        PREFIX = colorize(getMessage("prefix"));
        CONSOLE_SENDER = getFormat(getMessage("console-sender"));
        HELP = new ArrayList<>();
        for (String l : getMessageList("help-command"))
            HELP.add(colorize(l));
        RELOAD = getFormat(getMessage("reload-success"));
        NEED_PERMISSION = getFormat(getMessage("need-permission"));
        UPDATE = getMessage("check-update");
        LASTEST = getMessage("lastes-version");
    }


    public static class Usage {

        public static String CREATE_TUTORIAL;
        public static String DELETE_TUTORIAL;
        public static String SAVE_TUTORIAL;
        public static String SET_PERM;
        public static String CREATE_EVENT;
        public static String EXIT_COMMAND;
        public static String DISABLE_SENDMESSAGE;
        public static String SEND_PERM;
        public static String NOTINTUTORIAL;
        public static String NOT_FOUND;

        public static class Help {

            public static List<String> MESSAGE_HELP;
            public static List<String> ACTIONBAR_HELP;
            public static List<String> ACTIONBAR_DURATION_HELP;
            public static List<String> TITLE_HELP;
            public static List<String> SUBTITLE_HELP;
            public static List<String> TITLEDURATION_HELP;
            public static List<String> DELAY_HELP;
            public static List<String> CONSOLECMD_HELP;
            public static List<String> PLAYERCMD_HELP;
            public static List<String> PLAYERSOUND_HELP;
            public static List<String> PITCH_HELP;
            public static List<String> VOLUME_HELP;
            public static List<String> GIVEITEM_HELP;
            public static List<String> AMOUNT_HELP;
            public static List<String> POTION_HELP;
            public static List<String> LEVEL_HELP;
            public static List<String> POTIONDURATION_HELP;
            public static List<String> TELEPORT_WORLD_HELP;
            public static List<String> TELEPORT_LOCATION_HELP;
            public static List<String> SETGAMEMODE_HELP;
            public static List<String> EVENT_PLAYERINTERACT_HELP;

            private static void initialize() {

                MESSAGE_HELP = new ArrayList<>();
                ACTIONBAR_HELP = new ArrayList<>();
                ACTIONBAR_DURATION_HELP = new ArrayList<>();

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

                for (String l : getMessageList("help.message"))
                    MESSAGE_HELP.add(colorize(l));

                for (String l : getMessageList("help.actionbar-message"))
                    ACTIONBAR_HELP.add(colorize(l));

                for (String l : getMessageList("help.actionbar-duration"))
                    ACTIONBAR_DURATION_HELP.add(colorize(l));

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
            }
        }

        private static void initialize() {
            DISABLE_SENDMESSAGE = getFormat(getMessage("disable-sendMessage"));
            CREATE_TUTORIAL = getFormat(getMessage("create-tutorial"));
            DELETE_TUTORIAL = getFormat(getMessage("delete-tutorial"));
            CREATE_EVENT = getFormat(getMessage("create-event"));
            SAVE_TUTORIAL = getFormat(getMessage("save-tutorial"));
            EXIT_COMMAND = getFormat(getMessage("exit-command"));
            SET_PERM = getFormat(getMessage("set-permission"));
            SEND_PERM = getFormat(getMessage("send-permission"));
            NOTINTUTORIAL = getFormat(getMessage("notintutorial"));
            NOT_FOUND = getFormat(getMessage("not-found-tutorial"));
        }
    }

}

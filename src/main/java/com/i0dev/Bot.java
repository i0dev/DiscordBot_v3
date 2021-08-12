package com.i0dev;

import com.i0dev.config.CommandsConfig;
import com.i0dev.config.GeneralConfig;
import com.i0dev.config.MiscConfig;
import com.i0dev.object.AdvancedDiscordCommand;
import com.i0dev.object.BasicCommand;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.ConfigUtil;
import com.i0dev.utility.SQLUtil;
import com.i0dev.utility.Utility;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {

    @Getter
    @Setter
    public static boolean pluginMode = false;
    @Getter
    public static long startupTime = 0;
    @Getter
    public static JDA jda = null;
    @Getter
    public static List<BasicCommand> registeredCommands;
    @Getter
    public static ScheduledExecutorService asyncService = Executors.newScheduledThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 2, 10));
    @Getter
    public static Map<Class<?>, String> configMap = new HashMap<>();
    @Getter
    public static Logger logger = LoggerFactory.getLogger(Bot.class.getName());


    @SneakyThrows
    public static void main(String[] args) {
        if (startupTime == 0) startupTime = System.currentTimeMillis();
        Utility.createFile(getBasicConfigPath());
        Utility.createFile(getConfigPath());
        Utility.createFile(getMiscConfigPath());
        Utility.createDirectory(getTicketLogsPath());
        Utility.createDirectory(getStoragePath());
        configMap.put(CommandsConfig.class, getBasicConfigPath());
        configMap.put(GeneralConfig.class, getConfigPath());
        configMap.put(MiscConfig.class, getMiscConfigPath());
        ConfigUtil.reloadConfig();
        SQLUtil.connect();
        SQLUtil.makeTable(DPlayer.class);
        createJDA();
        registerCommands();
        registerListeners();
        Engine.run();

        System.out.println("Successfully loaded DiscordBot");
    }

    @SneakyThrows
    public static void createJDA() {
        try {
            jda = JDABuilder.create(GeneralConfig.get().getDiscordToken(),
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_BANS,
                            GatewayIntent.GUILD_EMOJIS,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_INVITES,
                            GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGE_TYPING,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.DIRECT_MESSAGE_TYPING,
                            GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setContextEnabled(true)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(
                            CacheFlag.ACTIVITY,
                            CacheFlag.VOICE_STATE,
                            CacheFlag.MEMBER_OVERRIDES,
                            CacheFlag.EMOTE,
                            CacheFlag.CLIENT_STATUS)
                    .build()
                    .awaitReady();
        } catch (Exception ignored) {
            System.out.println("\n\nCould not load previous settings. If this is the first time you launched the bot, it will generate a folder with config and storage.\n");
            if (pluginMode)
                com.i0dev.BotPlugin.get().onDisable();
        }
    }

    @SneakyThrows
    public static void registerListeners() {
        int count = 0;
        for (Class<? extends EventListener> listener : new Reflections("com.i0dev").getSubTypesOf(EventListener.class)) {
            if (listener.getName().equals(ListenerAdapter.class.getName())) continue;
            if (listener.getName().equals(DiscordCommand.class.getName())) continue;
            if (listener.getSuperclass().getName().equals(DiscordCommand.class.getName())) count--;
            jda.addEventListener(listener.newInstance());
            count++;
        }
        System.out.println("Registered [" + count + "] total event listener(s).");
    }


    @SneakyThrows
    public static void registerCommands() {
        registeredCommands = new ArrayList<>();
        int count = 0;
        List<Class<? extends DiscordCommand>> superCmds = new ArrayList<>();
        for (Class<? extends DiscordCommand> command : new Reflections("com.i0dev").getSubTypesOf(DiscordCommand.class)) {
            // System.out.println(command.getName());
            if (command.getName().equals(AdvancedDiscordCommand.class.getName())) continue;
            if (command.getName().equals(SuperDiscordCommand.class.getName())) continue;
            if (command.getSuperclass().getName().equals(SuperDiscordCommand.class.getName())) {
                superCmds.add(command);
                continue;
            }
            if (command.getSuperclass().getName().equals(AdvancedDiscordCommand.class.getName()))
                registeredCommands.add(AdvancedDiscordCommand.getAdvancedCommand(command).setClazz(command));
            else
                registeredCommands.add(DiscordCommand.getBasicCommand(command).setClazz(command));
            Utility.loadClass(command);
            count++;
        }

        superCmds.forEach(Utility::loadClass);
        System.out.println("Registered [" + count + "] total command(s).");
    }

    private static File getDataFolder() {
        return BotPlugin.get().getDataFolder();
    }

    public static String getConfigPath() {
        return pluginMode ? getDataFolder() + "/Config.json" : "DiscordBot/Config.json";
    }

    public static String getMiscConfigPath() {
        return pluginMode ? getDataFolder() + "/miscConfig.json" : "DiscordBot/miscConfig.json";
    }

    public static String getBasicConfigPath() {
        return pluginMode ? getDataFolder() + "/commandConfig.json" : "DiscordBot/commandConfig.json";
    }

    public static String getTicketLogsPath() {
        return pluginMode ? getDataFolder() + "/ticketLogs/" : "DiscordBot/ticketLogs/";
    }

    public static String getMainFolder() {
        return pluginMode ? getDataFolder() + "" : "DiscordBot";
    }
}
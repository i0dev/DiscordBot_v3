package com.i0dev;

import com.i0dev.config.GeneralConfig;
import com.i0dev.object.AdvancedDiscordCommand;
import com.i0dev.object.BasicCommand;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.managers.*;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.PlaceholderUtil;
import com.i0dev.utility.Utility;
import lombok.Data;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Data
public class DiscordBot {

    boolean pluginMode = false;
    long startupTime = 0;
    JDA jda;
    List<BasicCommand> registeredCommands;
    ScheduledExecutorService asyncService = Executors.newScheduledThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 2, 10));
    final List<Manager> managers = new ArrayList<>();

    @SneakyThrows
    void initialize() {
        System.out.println("    _    ____        __                                                            \n" +
                "   (_)  / __ \\  ____/ /  ___  _   __                                               \n" +
                "  / /  / / / / / __  /  / _ \\| | / /                                               \n" +
                " / /  / /_/ / / /_/ /  /  __/| |/ /                                                \n" +
                "/_/   \\____/  \\__,_/   \\___/ |___/                                                 \n" +
                "    ____     _                                      __           ____           __ \n" +
                "   / __ \\   (_)   _____  _____  ____    _____  ____/ /          / __ )  ____   / /_\n" +
                "  / / / /  / /   / ___/ / ___/ / __ \\  / ___/ / __  /          / __  | / __ \\ / __/\n" +
                " / /_/ /  / /   (__  ) / /__  / /_/ / / /    / /_/ /          / /_/ / / /_/ // /_  \n" +
                "/_____/  /_/   /____/  \\___/  \\____/ /_/     \\__,_/          /_____/  \\____/ \\__/  \n" +
                "                                                                                   \n");
        if (startupTime == 0) startupTime = System.currentTimeMillis();
        Utility.createFile(getBasicConfigPath());
        Utility.createFile(getConfigPath());
        Utility.createFile(getMiscConfigPath());
        Utility.createFile(getCustomCommandsConfigPath());
        Utility.createDirectory(getTicketLogsPath());
        Utility.createDirectory(getStoragePath());
        managers.addAll(Arrays.asList(
                new ConfigManager(this),
                new DPlayerManager(this),
                new SQLManager(this)
        ));
        managers.forEach(Manager::initialize);
        if (GeneralConfig.get().getDiscordToken().equals("Enter your token here!")) {
            System.out.println("\n\nWelcome to the i0dev DiscordBot, configuration files have been generated for you!\nGo fill them out and then re-enable the bot.\n\n");
            if (pluginMode)
                BotPlugin.get().onDisable();
            else shutdown();
            return;
        }
        Bot.getBot().getManager(SQLManager.class).makeTable(DPlayer.class);
        jda = JDABuilder.create(GeneralConfig.get().getDiscordToken(), EnumSet.allOf(GatewayIntent.class))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setContextEnabled(true)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(EnumSet.allOf(CacheFlag.class))
                .build()
                .awaitReady();
        registerCommands();
        registerListeners();
        Engine.run();
        System.out.println("Successfully loaded DiscordBot");
    }

    @SneakyThrows
    public void shutdown() {
        LogUtil.log(PlaceholderUtil.convert("Shutting down DiscordBot v{version}", null, null));
        startupTime = 0;
        asyncService.shutdown();
        if (registeredCommands != null) registeredCommands.clear();
        managers.forEach(Manager::deinitialize);
        jda = null;
        System.gc();
        if (!pluginMode) System.exit(0);
    }

    @SneakyThrows
    void registerListeners() {
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
    public void registerCommands() {
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

    public <T> T getManager(Class<T> clazz) {
        return (T) managers.stream().filter(manager -> manager.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public ConfigManager getConfigManager() {
        return (ConfigManager) managers.stream().filter(manager -> manager.getClass().equals(ConfigManager.class)).findFirst().orElse(null);
    }

    public DPlayerManager getDPlayerManager() {
        return (DPlayerManager) managers.stream().filter(manager -> manager.getClass().equals(DPlayerManager.class)).findFirst().orElse(null);
    }

    // File Paths

    File getDataFolder() {
        return BotPlugin.get().getDataFolder();
    }

    final String configPath = pluginMode ? getDataFolder() + "/Config.json" : "DiscordBot/Config.json";
    final String miscConfigPath = pluginMode ? getDataFolder() + "/miscConfig.json" : "DiscordBot/miscConfig.json";
    final String customCommandsConfigPath = pluginMode ? getDataFolder() + "/customCommands.json" : "DiscordBot/customCommands.json";
    final String basicConfigPath = pluginMode ? getDataFolder() + "/commandConfig.json" : "DiscordBot/commandConfig.json";
    final String ticketLogsPath = pluginMode ? getDataFolder() + "/ticketLogs/" : "DiscordBot/ticketLogs/";
    final String storagePath = pluginMode ? getDataFolder() + "/storage/" : "DiscordBot/storage/";
    final String mainFolder = pluginMode ? getDataFolder() + "" : "DiscordBot";

}

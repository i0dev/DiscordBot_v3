package com.i0dev;


import com.i0dev.modules.basic.CommandDiscordBot;
import com.i0dev.modules.linking.CommandLink;
import com.i0dev.modules.linking.EventHandler;
import com.i0dev.modules.misc.InGamePunishmentLogs;
import com.i0dev.modules.twoFactorAuthentication.Command2fa;
import com.i0dev.modules.twoFactorAuthentication.TwoFactorAuthentication;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.MessageUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BotPlugin extends Plugin {

    @Override
    public void onEnable() {
        instance = this;
        Bot.bot = new DiscordBot(true);
        Bot.bot.initialize();
        server.getPluginManager().registerCommand(this, new CommandLink("link"));
        server.getPluginManager().registerCommand(this, new Command2fa("2fa"));
        server.getPluginManager().registerCommand(this, new CommandDiscordBot("discordBot"));
        server.getPluginManager().registerListener(BotPlugin.get(), new TwoFactorAuthentication());
        server.getPluginManager().registerListener(BotPlugin.get(), new EventHandler());
        server.getScheduler().runAsync(get(), () -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InGamePunishmentLogs.initialize();
        });
        getLogger().info(ChatColor.GREEN + "DiscordBot Enabled!");
    }


    @Override
    public void onDisable() {
        server.getScheduler().cancel(this);
        server.getPluginManager().unregisterCommands(this);
        server.getPluginManager().unregisterListeners(this);
        Bot.getBot().shutdown();
        instance = null;
        getLogger().info(ChatColor.RED + "DiscordBot Disabled!");
    }


    private static BotPlugin instance;
    public static final ProxyServer server = ProxyServer.getInstance();

    public static BotPlugin get() {
        return instance;
    }

    public static void runCommand(String command) {
        server.getPluginManager().dispatchCommand(server.getConsole(), command);
        LogUtil.log("Ran the command: [" + command + "] in game.");
    }

    public static void runCommand(String srv, String command) {
        if (srv.equalsIgnoreCase("bungee")) {
            runCommand(command);
            return;
        }
        MessageUtil.runCommandOnServer("server_command", srv, command);
    }

}

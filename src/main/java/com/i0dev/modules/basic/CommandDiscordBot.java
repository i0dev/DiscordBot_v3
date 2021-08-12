package com.i0dev.modules.basic;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandDiscordBot extends Command {

    public CommandDiscordBot(String name) {
        super(name);
    }

    String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("version")) {
            commandSender.sendMessage(c("&f"));
            commandSender.sendMessage(c("&c&lDiscordBot Information"));
            commandSender.sendMessage(c("&fHelp Command: &c/DiscordBot help"));
            commandSender.sendMessage(c("&fVersion: &c{version}"));
            commandSender.sendMessage(c("&fAuthor: &c{botAuthor}"));
            commandSender.sendMessage(c("&f"));
        }
    }
}

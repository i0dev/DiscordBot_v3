package com.i0dev.modules.basic;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.ConfigUtil;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.MessageUtil;

public class CmdReload extends DiscordCommand {

    @CommandData(commandID = "cmd_reload", messageLength = 1, identifier = "Reload Config")
    public static void run(CommandEvent e) {
        ConfigUtil.reloadConfig();
        Bot.registerCommands();
        MessageUtil.sendPluginMessage("bot_command","reload");
        e.reply(EmbedMaker.builder().content("You have reloaded the configuration.").embedColor(EmbedColor.SUCCESS).build());
    }
}
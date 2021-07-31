package com.i0dev.modules.fun;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CmdHey extends DiscordCommand {

    public static void load() {
        addMessage("main", ":wave: Hello {tag}, How is your day going? I hope its swell!");
    }

    @CommandData(identifier = "Hey", commandID = "cmd_hey", messageLength = 1)
    public static void run(CommandEvent e) {
        e.reply(EmbedMaker.builder().content(getMessage("main")).user(e.getAuthor()).build());
    }
}

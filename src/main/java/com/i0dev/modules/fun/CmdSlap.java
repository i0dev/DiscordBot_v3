package com.i0dev.modules.fun;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.User;

public class CmdSlap extends DiscordCommand {

    public static void load() {
        addMessage("main", "{authorTag}, you slapped the living shit out of {tag}. :middle_finger:");
    }

    @CommandData(commandID = "cmd_slap", identifier = "Slap", messageLength = 2, usage = "<user>")
    public static void run(CommandEvent e) {
        User user = e.getAuthor();
        if (e.getSplit().length > 1) {
            if ((user = FindUtil.getUser(e.getSplit()[1], e.getMessage())) == null) return;
        }
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content(getMessage("main")).user(user).author(e.getAuthor()).build());
    }

}

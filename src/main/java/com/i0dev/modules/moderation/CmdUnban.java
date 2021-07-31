package com.i0dev.modules.moderation;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.User;

public class CmdUnban extends DiscordCommand {


    @CommandData(commandID = "cmd_unban", messageLength = 2, usage = "<user>", identifier = "Unban")
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.retrieveUser(e.getSplit()[1], e.getMessage())) == null) return;


        if (Utility.getBan(e.getGuild(), user) == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("{tag} is not currently banned.").user(user).build());
            return;
        }

        e.getGuild().unban(user).queue();
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have unbanned {tag}").user(user).build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} has unbanned {tag}").user(user).author(e.getAuthor()).build());

    }

}

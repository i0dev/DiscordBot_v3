package com.i0dev.modules.moderation;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.User;

public class CmdKick extends DiscordCommand {


    @CommandData(commandID = "cmd_kick", minMessageLength = 2, usage = "<user> [reason]", identifier = "Kick")
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getSplit()[1], e.getMessage())) == null) return;

        String reason = Utility.remainingArgFormatter(e.getSplit(), 2);

        e.getGuild().kick(e.getGuild().getMember(user), reason).queue();
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have kicked {tag}").user(user).build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} has kicked {tag}\nReason: *{reason}*".replace("{reason}", reason)).user(user).author(e.getAuthor()).build());

    }

}

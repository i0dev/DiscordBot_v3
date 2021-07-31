package com.i0dev.modules.moderation;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class CmdDirectMessage extends DiscordCommand {

    @CommandData(commandID = "cmd_directMessage", minMessageLength = 3, usage = "<user> <content>", identifier = "Direct Message")
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getSplit()[1], e.getMessage())) == null) return;

        String content = Utility.remainingArgFormatter(e.getSplit(), 2);
        PrivateChannel channel = user.openPrivateChannel().complete();
        if (content.endsWith(" -normal")) {
            content = content.substring(0, content.length() - " -normal".length());
            channel.sendMessage(content).queue();
        } else {
            channel.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().content(content).authorName("Message from {tag}").authorImg(e.getAuthor().getEffectiveAvatarUrl()).user(e.getAuthor()).build())).queue();
        }

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have direct messaged {tag}").user(user).build());
    }
}
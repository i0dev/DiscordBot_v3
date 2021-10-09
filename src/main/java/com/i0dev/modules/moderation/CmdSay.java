package com.i0dev.modules.moderation;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.TextChannel;

public class CmdSay extends DiscordCommand {

    @CommandData(commandID = "cmd_say", minMessageLength = 3, identifier = "Say", usage = "<channel> <message>")
    public static void run(CommandEvent e) {
        TextChannel channel;
        if ((channel = FindUtil.getTextChannel(e.getSplit()[1], e.getMessage())) == null) return;
        String content = Utility.remainingArgFormatter(e.getSplit(), 2);
        if (content.endsWith(" -normal")) {
            content = content.substring(0, content.length() - " -normal".length());
            channel.sendMessage(content).queue();
        } else {
            EmbedColor color = EmbedColor.NORMAL;
            String[] split = content.split(" ");
            int len = split.length;
            if (split[len - 1].contains("color:")) {
                color.setCustom(split[len - 1].substring("color:".length()));
                content = content.substring(0, content.length() - split[len - 1].length() - 1);
            }
            channel.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().embedColor(color).content(content).user(e.getAuthor()).build())).queue();
        }

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You said a message in {channel}".replace("{channel}", channel.getAsMention())).build());

    }
}

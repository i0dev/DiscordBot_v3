package com.i0dev.modules.moderation;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.TextChannel;

public class CmdAnnounce extends DiscordCommand {

    public void load() {
        addOption("includeHeader", true);
    }

    @CommandData(commandID = "cmd_announce", minMessageLength = 3, identifier = "Announce", usage = "<channel> <announcement>")
    public static void run(CommandEvent e) {
        TextChannel channel;
        if ((channel = FindUtil.getTextChannel(e.getSplit()[1], e.getMessage())) == null) return;


        String content = Utility.remainingArgFormatter(e.getSplit(), 2);
        boolean includeHeader = getOption("includeHeader").getAsBoolean();

        if (content.endsWith(" -normal")) {
            content = content.substring(0, content.length() - " -normal".length());
            channel.sendMessage(content).queue();
        } else {
            EmbedColor color = EmbedColor.NORMAL;
            String[] split = content.split(" ");
            int len = split.length;
            if (split[len - 1].contains("color:")) {
                color.setCustom(split[len - 1].substring("color:".length()));
                content = content.substring(0, content.length() - split[len - 1].length() + 1);
            }
            String authorImg = e.getAuthor().getEffectiveAvatarUrl();
            String authorName = "Announcement from {tag}";
            if (!includeHeader) {
                authorImg = null;
                authorName = null;
            }
            channel.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().embedColor(color).content(content).authorName(authorName).authorImg(authorImg).user(e.getAuthor()).build())).queue();
        }

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have made an announcement in {channel}".replace("{channel}", channel.getAsMention())).build());

    }
}

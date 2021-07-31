package com.i0dev.modules.moderation;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.TextChannel;

public class CmdChangelog extends DiscordCommand {

    public static void load() {
        addOption("channel", 766330396832432150L);
    }

    @CommandData(commandID = "cmd_changelog", usage = "<content>", identifier = "Changelog", minMessageLength = 2)
    public static void run(CommandEvent e) {
        TextChannel channel = Bot.getJda().getTextChannelById(getOption("channel").getAsLong());
        String content = Utility.remainingArgFormatter(e.getSplit(), 1);
        channel.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().user(e.getAuthor()).content(content).authorImg(e.getAuthor().getEffectiveAvatarUrl()).authorName("Changelog post from {tag}").build())).queue();

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully made a changelog post.").build());

    }
}

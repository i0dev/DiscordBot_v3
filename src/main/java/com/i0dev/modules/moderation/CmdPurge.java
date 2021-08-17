package com.i0dev.modules.moderation;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class CmdPurge extends DiscordCommand {

    @CommandData(commandID = "cmd_purge", messageLength = 2, usage = "<amount>", identifier = "Purge Messages")
    public static void run(CommandEvent e) {
        Integer amount;
        if ((amount = FindUtil.getInteger(e.getSplit()[1], e.getMessage())) == null) return;

        e.getChannel().purgeMessages(e.getChannel().getHistory().retrievePast(amount).complete());

        e.replyComplete(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have purged {amt} messages.".replace("{amt}", amount + "")).build()).delete().queueAfter(10, TimeUnit.SECONDS);
        LogUtil.logDiscord(EmbedMaker.builder().content("{tag} has pruned {amt} messages in {channel}".replace("{channel}", ((TextChannel) e.getChannel()).getAsMention()).replace("{amt}", amount + "")).user(e.getAuthor()).build());

    }

}

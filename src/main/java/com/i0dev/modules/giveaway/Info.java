package com.i0dev.modules.giveaway;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.TimeUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Info extends SuperDiscordCommand {

    @CommandData(commandID = "info", parentClass = GiveawayManager.class, identifier = "Giveaway Info", usage = "<message>", messageLength = 2)
    public static void run(CommandEvent e) {
        Giveaway giveaway = Giveaway.getGiveaway(e.getOffsetSplit().get(1));
        if (giveaway == null) {
            e.reply(EmbedMaker.builder().content("Could not find that giveaway.").embedColor(EmbedColor.FAILURE).build());
            return;
        }

        TextChannel channel = Bot.getBot().getJda().getTextChannelById(giveaway.getChannelID());
        User host = Bot.getBot().getJda().retrieveUserById(giveaway.getHostID()).complete();

        StringBuilder msg = new StringBuilder();
        msg.append("Channel: ").append(channel.getAsMention()).append("\n");
        msg.append("Message ID: `").append(giveaway.getMessageID()).append("`\n");
        msg.append("Winner Amount: `").append(giveaway.getWinnerAmount()).append("`\n");
        msg.append("Host Tag: `").append(host.getAsTag()).append("`\n");
        boolean ended = giveaway.ended;
        msg.append("Ended: `").append(ended ? "Yes" : "No").append("`\n");

        if (ended)
            msg.append("Ended At: `").append(Utility.formatDate(giveaway.getEndTime())).append("`\n");
        else
            msg.append("Time Remaining: `").append(TimeUtil.formatTime(giveaway.getEndTime() - System.currentTimeMillis())).append("`\n");

        e.reply(EmbedMaker.builder()
                .authorURL("https://discordapp.com/channels/" + channel.getGuild().getId() + "/" + giveaway.channelID + "/" + giveaway.messageID)

                .authorName("Giveaway Information:").embedColor(ended ? EmbedColor.FAILURE : EmbedColor.SUCCESS).authorImg(Bot.getBot().getJda().getSelfUser().getEffectiveAvatarUrl()).content(msg.toString()).build());
    }
}

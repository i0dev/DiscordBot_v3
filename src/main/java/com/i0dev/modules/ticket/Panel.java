package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.EmojiUtil;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

public class Panel extends SuperDiscordCommand {

    public static void load() {
        addOption("pin", true);
        addOption("image", "https://cdn.discordapp.com/attachments/766330396832432150/856722791494320158/McRivals2.png");
    }

    @CommandData(commandID = "panel", identifier = "Ticket Panel", messageLength = 1, parentClass = TicketManager.class)
    public static void run(CommandEvent e) {
        StringBuilder msg = new StringBuilder();
        msg.append("React with the emoji that corresponds with the ticket you wish to create.").append("\n\n");


        TicketManager.options.forEach(ticketOption -> {
            msg.append(EmojiUtil.getSimpleEmoji(Emoji.fromMarkdown(ticketOption.getEmoji()).getAsMention())).append("** - ").append(ticketOption.getDisplayName()).append("**\n");
        });

        String image = getOption("image").getAsString().equals("") ? null : "https://cdn.discordapp.com/attachments/766330396832432150/856722791494320158/McRivals2.png";
        Message panel = e.getChannel().sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder()
                        .authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl())
                        .authorName("Ticket Creation Panel")
                        .image(image)
                        .content(msg.toString())
                        .build()))
                .complete();


        TicketManager.options.forEach(ticketOption -> panel.addReaction(EmojiUtil.getEmojiWithoutArrow(ticketOption.getEmoji())).queue());
        if (getOption("pin").getAsBoolean()) panel.pin().queue();

    }
}
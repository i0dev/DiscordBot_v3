package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.entities.User;

public class Info extends SuperDiscordCommand {

    @CommandData(commandID = "info", identifier = "Ticket Info", messageLength = 1, parentClass = TicketManager.class)
    public static void run(CommandEvent e) {
        if (!TicketManager.isTicket(e.getChannel())) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("This command can only be used in a ticket.").build());
            return;
        }

        Ticket ticket = Ticket.getTicket(e.getChannel());
        User user = Bot.getBot().getJda().retrieveUserById(ticket.getTicketOwnerID()).complete();

        StringBuilder msg = new StringBuilder();
        msg.append("Channel Name: `").append(ticket.getTicketName()).append("`\n");
        msg.append("Owner Tag: `").append(user.getAsTag()).append("`\n");
        msg.append("Ticket ID: `").append(ticket.getChannelID()).append("`\n");
        msg.append("Admin Only: `").append(ticket.isAdminOnlyMode() ? "Yes" : "No").append("`\n");

        e.reply(EmbedMaker.builder()
                .authorImg(user.getEffectiveAvatarUrl())
                .content(msg.toString())
                .authorName("Ticket information for: " + ticket.getTicketName())
                .build());
    }
}

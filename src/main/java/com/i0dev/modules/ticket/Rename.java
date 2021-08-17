package com.i0dev.modules.ticket;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.TextChannel;

public class Rename extends SuperDiscordCommand {

    @CommandData(commandID = "rename", identifier = "Ticket Rename", minMessageLength = 2, usage = "<name>", parentClass = TicketManager.class)
    public static void run(CommandEvent e) {
        if (!TicketManager.isTicket(e.getChannel())) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("This command can only be used in a ticket.").build());
            return;
        }
        Ticket ticket = Ticket.getTicket(e.getChannel());
        String newTicketName = Utility.remainingArgFormatter(e.getOffsetSplit(), 1).replace(" ", "-") + "-" + ticket.getTicketNumber();
        ((TextChannel) e.getChannel()).getManager().setName(newTicketName).queue();
        ticket.setTicketName(newTicketName);
        ticket.save();
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have renamed this ticket to: `{name}`".replace("{name}", newTicketName)).build());

    }
}

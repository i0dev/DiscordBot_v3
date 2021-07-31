package com.i0dev.modules.ticket;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;

public class Close extends SuperDiscordCommand {

    public static void load() {
        addOption("defaultReason", "Have a good day!");
    }

    @CommandData(commandID = "close", identifier = "Ticket Close", minMessageLength = 1, usage = "[reason]", parentClass = TicketManager.class)
    public static void run(CommandEvent e) {
        if (!TicketManager.isTicket(e.getChannel())) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("This command can only be used in a ticket.").build());
            return;
        }

        TicketCloseHandler.closeTicket(Ticket.getTicket(e.getChannel()), Utility.ticketRemainingArgFormatter(e.getOffsetSplit(), 1), e.getAuthor());
    }
}

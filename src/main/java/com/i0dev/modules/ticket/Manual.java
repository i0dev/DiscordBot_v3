package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.Engine;
import com.i0dev.config.MiscConfig;
import com.i0dev.object.*;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class Manual extends SuperDiscordCommand {

    @CommandData(commandID = "manual", identifier = "Ticket Manual", messageLength = 2, usage = "<user> (Ticket Owner)", parentClass = TicketManager.class)
    public static void run(CommandEvent e) {
        if (TicketManager.isTicket(e.getChannel())) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("This channel is already a ticket.").build());
            return;
        }
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;

        Ticket ticket = new Ticket();
        ticket.setAdminOnlyMode(false);
        ticket.setTicketOwnerID(user.getIdLong());
        ticket.setChannelID(e.getChannel().getIdLong());
        ticket.setTicketName(e.getChannel().getName());
        ticket.setTicketNumber(MiscConfig.get().ticketNumber);
        ticket.save();

        File ticketLogsFile = new File(Bot.getBot().getTicketLogsPath() + e.getChannel().getId() + ".log");

        String Zone = ZonedDateTime.now().getZone().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        Long ticketOwnerID = ticket.getTicketOwnerID();
        String ticketOwnerAvatarURL = user.getEffectiveAvatarUrl();
        String ticketOwnerTag = user.getAsTag();
        boolean adminOnlyMode = ticket.isAdminOnlyMode();

        StringBuilder toFile = new StringBuilder();
        toFile.append("Ticket information:").append("\n");
        toFile.append("     Channel ID: ").append(e.getChannel().getId()).append("\n");
        toFile.append("     Channel Name: ").append(e.getChannel().getName()).append("\n");
        toFile.append("     Ticket Owner ID: ").append(ticketOwnerID).append("\n");
        toFile.append("     Ticket Owner Tag: ").append(ticketOwnerTag).append("\n");
        toFile.append("     Ticket Owner Avatar: ").append(ticketOwnerAvatarURL).append("\n");
        toFile.append("     Admin Only Mode: ").append(adminOnlyMode).append("\n");
        toFile.append("Ticket logs (TimeZone: ").append(Zone).append("):");

        Engine.getToLog().add(new LogObject(toFile.toString(), ticketLogsFile));


        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have now marked this channel as a ticket.").user(user).build());

    }
}
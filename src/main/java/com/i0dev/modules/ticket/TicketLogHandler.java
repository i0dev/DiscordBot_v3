package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.Engine;
import com.i0dev.object.LogObject;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

public class TicketLogHandler extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!TicketManager.isTicket(e.getChannel())) return;
        try {
            File ticketLogsFile = new File(Bot.getBot().getTicketLogsPath() + e.getChannel().getId() + ".log");
            StringBuilder toFile = new StringBuilder();
            for (Message.Attachment attachment : e.getMessage().getAttachments()) {
                toFile.append(Utility.formatDate(System.currentTimeMillis())).append(" [").append(e.getAuthor().getAsTag()).append("]: ")
                        .append("[FILE]" + attachment.getUrl())
                        .append("\n");
            }
            if (e.getMessage().getEmbeds().size() != 0) {
                MessageEmbed embed = e.getMessage().getEmbeds().get(0);
                toFile.append(Utility.formatDate(System.currentTimeMillis())).append(" [").append(e.getAuthor().getAsTag()).append("]: ")
                        .append("[EMBED]" + "\n   Title: ")
                        .append(embed.getTitle()).append("\n   Desc: ")
                        .append(embed.getDescription()).append("\n");
                if (embed.getFooter() != null) {
                    toFile.append("   Footer: ").append(embed.getFooter().getText());
                }
            } else {
                if (!e.getMessage().getContentDisplay().equals("")) {
                    toFile.append(Utility.formatDate(System.currentTimeMillis())).append(" [").append(e.getAuthor().getAsTag()).append("]: ").append(e.getMessage().getContentDisplay());
                }
            }

            Engine.getToLog().add(new LogObject(toFile.toString(), ticketLogsFile));
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent e) {
        if (!TicketManager.isTicket(e.getChannel())) return;
        try {
            File ticketLogsFile = new File(Bot.getBot().getTicketLogsPath() + e.getChannel().getId() + ".log");
            StringBuilder toFile = new StringBuilder();

            toFile.append(Utility.formatDate(System.currentTimeMillis())).append(" [").append(e.getAuthor().getAsTag()).append("]: ")
                    .append("[MESSAGE EDIT]" + e.getMessage().getContentDisplay())
                    .append("\n");

            Engine.getToLog().add(new LogObject(toFile.toString(), ticketLogsFile));
        } catch (Exception ignored) {

        }
    }
}

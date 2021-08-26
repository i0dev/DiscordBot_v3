package com.i0dev.modules.ticket;

import com.google.gson.JsonObject;
import com.i0dev.Bot;
import com.i0dev.Engine;
import com.i0dev.modules.CommandManager;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.LogObject;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerFieldType;
import com.i0dev.object.managers.ConfigManager;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class TicketCloseHandler extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getButton() == null) return;
        if (!"BUTTON_TICKET_CLOSE".equalsIgnoreCase(e.getButton().getId())) return;
        if (e.getUser().isBot()) return;
        if (!Bot.getBot().getManager(ConfigManager.class).getObjectFromInternalPath("cmd_ticket.parts.close.enabled", Bot.getBot().getConfigManager().getJsonObject(Bot.getBot().getBasicConfigPath())).getAsBoolean())
            return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        if (Bot.getBot().getDPlayerManager().getDPlayer(e.getUser()).isBlacklisted()) return;
        JsonObject ob = Bot.getBot().getManager(ConfigManager.class).getObjectFromInternalPath("cmd_ticket.parts.close.permission", Bot.getBot().getConfigManager().getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonObject();
        if (!CommandManager.hasPermission(e.getMember(), ob.get("strict").getAsBoolean(), ob.get("lite").getAsBoolean(), ob.get("admin").getAsBoolean()))
            return;
        if (!TicketManager.isTicket(e.getChannel())) return;
        closeTicket(Ticket.getTicket(e.getChannel()), Close.getOption("defaultReason", Close.class).getAsString(), e.getUser());
        e.deferEdit().queue();
    }


    @SneakyThrows
    static void closeTicket(Ticket ticket, String reason, User closer) {
        File ticketLogsFile = new File(Bot.getBot().getTicketLogsPath() + ticket.getChannelID() + ".log");
        TextChannel channel = Bot.getBot().getJda().getTextChannelById(ticket.getChannelID());
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(closer);
        String toFile = "\n\nClosed Ticket Information:\n " +
                "  Ticket Closer Tag: " + closer.getAsTag() + "\n" +
                "   Ticket Closer ID: " + closer.getId() + "\n" +
                "   Ticket Close reason: " + reason;
        Engine.getToLog().add(new LogObject(toFile, ticketLogsFile));
        Engine.taskAppendToFile.run();
        ticketLogsFile = new File(Bot.getBot().getTicketLogsPath() + ticket.getChannelID() + ".log");
        User ticketOwner = Bot.getBot().getJda().retrieveUserById(ticket.getTicketOwnerID()).complete();

        dPlayer.increase(DPlayerFieldType.TICKETS_CLOSED);
        channel.delete().queueAfter(5, TimeUnit.SECONDS);

        channel.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("This ticket will close in 5 seconds.").build())).queue();

        StringBuilder msg = new StringBuilder();

        EmbedMaker.EmbedMakerBuilder embedMaker = EmbedMaker.builder()
                .authorImg(ticketOwner.getEffectiveAvatarUrl())
                .user(ticketOwner)
                .author(closer)
                .authorName("{tag}'s ticket was closed by {authorTag}")
                .field(new MessageEmbed.Field("Ticket " + ticket.getTicketName(), "Close reason: `{reason}`".replace("{reason}", reason), false));

        TextChannel logs;
        if (ticket.isAdminOnlyMode())
            logs = Bot.getBot().getJda().getTextChannelById(TicketManager.getOption("adminLogsChannel", TicketManager.class).getAsLong());
        else
            logs = Bot.getBot().getJda().getTextChannelById(TicketManager.getOption("ticketLogsChannel", TicketManager.class).getAsLong());

        if (logs != null) {
            logs.sendMessageEmbeds(EmbedMaker.create(embedMaker.build())).queueAfter(5, TimeUnit.MILLISECONDS);
            logs.sendFile(ticketLogsFile).queueAfter(5 + 1000, TimeUnit.MILLISECONDS);
        }

        ticket.remove();
        try {
            ticketOwner.openPrivateChannel().complete().sendMessageEmbeds(EmbedMaker.create(embedMaker.build())).completeAfter(5, TimeUnit.SECONDS);
            embedMaker.authorName("Your ticket was closed by {authorTag}");
            ticketOwner.openPrivateChannel().complete().sendFile(ticketLogsFile).queueAfter(6, TimeUnit.SECONDS);
        } catch (Exception ignored) {

        }
    }
}

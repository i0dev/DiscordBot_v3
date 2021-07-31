package com.i0dev.utility;

import com.i0dev.Bot;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import net.dv8tion.jda.api.entities.*;

import java.util.List;

public class MessageUtil {

    public static void sendMessageInGame(net.md_5.bungee.api.connection.ProxiedPlayer player, String message) {
        try {
            player.sendMessage(Utility.c(message));
        } catch (Exception ignored) {

        }
    }

    public static void sendMessageInGame(net.md_5.bungee.api.CommandSender sender, String message) {
        try {
            sender.sendMessage(Utility.c(message));
        } catch (Exception ignored) {

        }
    }

    public static void sendMessageInGame(net.md_5.bungee.api.connection.ProxiedPlayer player, List<String> messages) {
        for (String message : messages) {
            sendMessageInGame(player, message);
        }
    }

    public static void sendPrivateMessage(Message message, User who, EmbedMaker msgToSend) {
        try {
            who.openPrivateChannel().complete().sendMessageEmbeds(EmbedMaker.create(msgToSend)).queue();
        } catch (Exception e) {
            if (message == null) return;
            CommandEvent.replyStatic(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Failed to send a direct message to you. Please make sure they are enabled.").build(), message);
        }
    }

    public static Message sendPrivateMessageComplete(Message message, User who, EmbedMaker msgToSend) {
        try {
            return who.openPrivateChannel().complete().sendMessageEmbeds(EmbedMaker.create(msgToSend)).complete();
        } catch (Exception e) {
            if (message != null)
                CommandEvent.replyStatic(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Failed to send a direct message to you. Please make sure they are enabled.").build(), message);
            return null;
        }
    }

}

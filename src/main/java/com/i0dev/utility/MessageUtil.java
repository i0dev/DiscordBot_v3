package com.i0dev.utility;

import com.i0dev.Bot;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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

    @SneakyThrows
    public static void sendPluginMessage(String specialChannel, String data) {
        if (Bot.getBot().isPluginMode()) {
            com.google.common.io.ByteArrayDataOutput out = com.google.common.io.ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF(specialChannel);
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(data);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            LogUtil.log("Sent Plugin message: [" + data + "]");
            com.i0dev.BotPlugin.get().getProxy().getServersCopy().forEach((s, serverInfo) -> serverInfo.sendData("BungeeCord", out.toByteArray()));
        }
    }

    @SneakyThrows
    public static void runCommandOnServer(String specialChannel, String server, String data) {
        if (Bot.getBot().isPluginMode()) {
            com.google.common.io.ByteArrayDataOutput out = com.google.common.io.ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF(server);
            out.writeUTF(specialChannel);
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(data);
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            LogUtil.log("Sent Plugin message: [" + data + "] to server: [" + server + "]");
            com.i0dev.BotPlugin.get().getProxy().getServersCopy().forEach((s, serverInfo) -> serverInfo.sendData("BungeeCord", out.toByteArray()));
        }
    }

}

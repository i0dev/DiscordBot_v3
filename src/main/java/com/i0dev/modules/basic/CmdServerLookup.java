package com.i0dev.modules.basic;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.APIUtil;
import com.i0dev.utility.EmbedMaker;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class CmdServerLookup extends DiscordCommand {

    @SneakyThrows
    @CommandData(commandID = "cmd_serverLookup", messageLength = 2, identifier = "Server Lookup", usage = "<server ip>")
    public static void run(CommandEvent e) {
        String ip = e.getSplit()[1];
        JSONObject json = APIUtil.MinecraftServerLookup(ip);
        if (json == null) {
            e.reply(EmbedMaker.builder().content("Cannot find target server!").embedColor(EmbedColor.FAILURE).build());
            return;
        }

        boolean online = (boolean) json.get("online");
        StringBuilder msg = new StringBuilder();
        msg.append("__**Server Information**__").append("\n");
        msg.append("Online Status: `").append(online ? "Online" : "Offline").append("`").append("\n");
        msg.append("Numerical IP: `").append(json.get("ip")).append("`").append("\n");
        msg.append("Port: `").append(json.get("port")).append("`").append("\n");
        if (online) {
            StringBuilder motd = new StringBuilder();
            JSONObject players = ((JSONObject) json.get("players"));
            ((ArrayList<String>) ((JSONObject) json.get("motd")).get("clean")).forEach(s -> motd.append(s).append("\n"));

            msg.append("Online Players: `").append(players.get("online")).append(" / ").append(players.get("max")).append("`").append("\n");
            msg.append("Supported Versions: `").append(json.get("version")).append("`").append("\n");
            msg.append("\n__**Message Of The Day**__\n").append(motd).append("\n");
        }


        e.reply(EmbedMaker.builder().thumbnail("https://api.mcsrvstat.us/icon/" + ip).title("Looking up server: " + ip).embedColor(online ? EmbedColor.SUCCESS : EmbedColor.FAILURE).content(msg.toString()).build());
    }
}
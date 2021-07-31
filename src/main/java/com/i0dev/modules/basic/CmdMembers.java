package com.i0dev.modules.basic;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public class CmdMembers extends DiscordCommand {

    @CommandData(commandID = "cmd_members", messageLength = 1, identifier = "Members")
    public static void run(CommandEvent e) {
        List<Member> members = e.getGuild().getMembers();
        StringBuilder info = new StringBuilder();
        info.append("Members: ").append("`").append(members.size()).append("`").append("\n");
        info.append("Humans: ").append("`").append(members.stream().filter(member -> !member.getUser().isBot()).count()).append("`").append("\n");
        info.append("Bots: ").append("`").append(members.stream().filter(member -> member.getUser().isBot()).count()).append("`").append("\n");
        StringBuilder status = new StringBuilder();
        status.append("Online: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE)).count()).append("`").append("\n");
        status.append("DND: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)).count()).append("`").append("\n");
        status.append("Idle: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.IDLE)).count()).append("`").append("\n");
        status.append("Offline: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count()).append("`").append("\n");
        e.reply(EmbedMaker.builder()
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__Member Information__", info.toString(), true),
                        new MessageEmbed.Field("__Member Status__", status.toString(), true)
                })
                .author(e.getAuthor()).build());
    }
}
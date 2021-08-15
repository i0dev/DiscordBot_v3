package com.i0dev.modules.basic;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CmdServerInfo extends DiscordCommand {


    @CommandData(commandID = "cmd_serverInfo", messageLength = 1, identifier = "Server Info")
    public static void run(CommandEvent e) {
        Guild guild = e.getGuild();

        StringBuilder general = new StringBuilder();
        general.append("Server Owner: ").append(guild.getOwner().getAsMention()).append("\n");
        general.append("Server Region: `").append(guild.getRegion().getName()).append("`\n");
        general.append("Member Count: `").append(guild.getMemberCount()).append("`\n");
        general.append("Categories: `").append(guild.getCategories().size()).append("`\n");
        general.append("Text Channels: `").append(guild.getTextChannels().size()).append("`\n");
        general.append("Voice Channels: `").append(guild.getVoiceChannels().size()).append("`\n");
        general.append("Roles: `").append(guild.getRoles().size()).append("`\n");
        general.append("Creation Date: `").append(guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)).append("`\n");
        List<Member> members = e.getGuild().getMembers();
        StringBuilder memberInfo = new StringBuilder();
        memberInfo.append("Members: ").append("`").append(members.size()).append("`").append("\n");
        memberInfo.append("Humans: ").append("`").append(members.stream().filter(member -> !member.getUser().isBot()).count()).append("`").append("\n");
        memberInfo.append("Bots: ").append("`").append(members.stream().filter(member -> member.getUser().isBot()).count()).append("`").append("\n");
        memberInfo.append("\n");
        memberInfo.append("Online: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE)).count()).append("`").append("\n");
        memberInfo.append("DND: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)).count()).append("`").append("\n");
        memberInfo.append("Idle: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.IDLE)).count()).append("`").append("\n");
        memberInfo.append("Offline: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count()).append("`").append("\n");

        List<String> emotes = new ArrayList<>();
        guild.getEmotes().forEach(emote -> emotes.add(emote.getAsMention()));
        int length = 0;
        List<String> toRemove = new ArrayList<>();
        for (String emote : emotes) {
            length += emote.length();
            if (length > 950)
                toRemove.add(emote);
        }
        toRemove.forEach(emotes::remove);
        e.reply(EmbedMaker.builder()
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__General Info__", general.toString(), true),
                        new MessageEmbed.Field("__Member Info__", memberInfo.toString(), true),
                        new MessageEmbed.Field("__Server Emotes (Showing {x} out of {max} emojis)__".replace("{max}", guild.getEmotes().size() + "").replace("{x}", (guild.getEmotes().size() - toRemove.size()) + ""), Utility.FormatListStringComma(emotes), false)
                })
                .authorName(guild.getName() + "'s Server Information")
                .authorImg(guild.getIconUrl())
                .build());

    }
}

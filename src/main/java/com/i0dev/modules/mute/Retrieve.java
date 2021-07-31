package com.i0dev.modules.mute;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.stream.Collectors;

public class Retrieve extends SuperDiscordCommand {

    @CommandData(commandID = "list", parentClass = MuteManager.class, messageLength = 1, identifier = "Mute List")
    public static void run(CommandEvent e) {
        if (MuteManager.mutedRole == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Muted role is not yet setup. You can create one with {prefix}mute create").build());
            return;
        }

        List<Member> list = e.getGuild().getMembers().stream().filter(member -> member.getRoles().contains(MuteManager.mutedRole)).collect(Collectors.toList());
        if (list.isEmpty()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("There are currently not any muted users.").build());
            return;
        }

        StringBuilder msg = new StringBuilder();
        list.forEach(member -> msg.append(member.getUser().getAsTag()).append(" `(").append(member.getIdLong()).append(")`\n"));

        e.reply(EmbedMaker.builder().field(new MessageEmbed.Field("Muted Users:", msg.toString(), true)).build());
    }
}

package com.i0dev.modules.mute;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.concurrent.TimeUnit;

public class Create extends SuperDiscordCommand {

    @CommandData(commandID = "create", parentClass = MuteManager.class, messageLength = 1, identifier = "Mute Create")
    public static void run(CommandEvent e) {
        Role role = e.getGuild().createRole().setName("Muted").setColor(java.awt.Color.darkGray).complete();
        for (TextChannel channel : e.getGuild().getTextChannels()) {
            channel.putPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queueAfter(5, TimeUnit.SECONDS);
        }
        for (VoiceChannel channel : e.getGuild().getVoiceChannels()) {
            channel.putPermissionOverride(role).setDeny(Permission.VOICE_SPEAK).queueAfter(5, TimeUnit.SECONDS);
        }

        MuteManager.saveRole(role.getIdLong());
        MuteManager.mutedRole = role;
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully created a muted role.\nRole: {role}".replace("{role}", role.getAsMention())).build());

    }
}

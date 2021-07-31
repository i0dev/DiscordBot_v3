package com.i0dev.modules.basic;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class CmdRoles extends DiscordCommand {

    @CommandData(commandID = "cmd_roles", identifier = "Roles", messageLength = 1)
    public static void run(CommandEvent e) {
        e.reply(EmbedMaker.builder()
                .field(new MessageEmbed.Field("__List of all roles__", Utility.FormatList(e.getGuild().getRoles()), true))
                .build());
    }

}
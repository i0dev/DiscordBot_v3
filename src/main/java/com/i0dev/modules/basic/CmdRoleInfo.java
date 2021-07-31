package com.i0dev.modules.basic;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;

public class CmdRoleInfo extends DiscordCommand {


    @CommandData(commandID = "cmd_roleInfo", messageLength = 2, identifier = "Role Info", usage = "<role>")
    public static void run(CommandEvent e) {
        Role role;
        if ((role = FindUtil.getRole(e.getSplit()[1], e.getMessage())) == null) return;
        Color color = role.getColor();
        String format = "#99aab5";
        if (color != null)
            format = String.format("#%02x%02x%02x", color.getRed(), color.getBlue(), color.getGreen());


        StringBuilder msg = new StringBuilder();
        msg.append("Name: `").append(role.getName()).append("`\n");
        msg.append("Role ID: `").append(role.getId()).append("`\n");
        msg.append("Color: `").append(format).append("`\n");
        msg.append("Mention: ").append(role.getAsMention()).append("\n");
        msg.append("Position: `").append(role.getPosition()).append("`\n");
        msg.append("Mentionable: `").append(role.isMentionable() ? "Yes" : "No").append("`\n");
        msg.append("Hoisted: `").append(role.isHoisted() ? "Yes" : "No").append("`\n");

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.NORMAL.setCustom(format)).field(new MessageEmbed.Field("__Role Information__ ", msg.toString(), true)).build());

    }
}

package com.i0dev.modules.basic;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.User;

public class CmdAvatar extends DiscordCommand {

    @CommandData(identifier = "Avatar", commandID = "cmd_avatar", usage = "[user]", maxMessageLength = 2)
    public static void run(CommandEvent e) {
        User user = e.getAuthor();
        if (e.getSplit().length > 1) {
            if ((user = FindUtil.getUser(e.getSplit()[1], e.getMessage())) == null) return;
        }

        e.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getAuthor())
                .image(user.getEffectiveAvatarUrl())
                .title("**{tag}**'s Avatar").build());
    }
}
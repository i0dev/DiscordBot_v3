package com.i0dev.modules.invite;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class Invites extends SuperDiscordCommand {

    @CommandData(commandID = "invites", identifier = "Invite Invites", maxMessageLength = 2, usage = "[user]", parentClass = InviteManager.class)
    public static void run(CommandEvent e) {
        User user = e.getAuthor();
        if (e.getOffsetSplit().size() > 1) {
            if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        }
        e.reply(EmbedMaker.builder().user(user).content("{tag}'s total invited users: {invites}").build());
    }
}

package com.i0dev.modules.points;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.User;

public class Balance extends SuperDiscordCommand {

    @CommandData(commandID = "balance", messageLength = 2, usage = "[user]", identifier = "Points Balance", parentClass = PointsManager.class)
    public static void run(CommandEvent e) {
        User user = e.getAuthor();
        if (e.getOffsetSplit().size() > 1) {
            if ((user = FindUtil.getUser(e.getSplit()[1], e.getMessage())) == null) return;
        }

        e.reply(EmbedMaker.builder().user(user).content("{tag} has `{points}` points").build());

    }
}
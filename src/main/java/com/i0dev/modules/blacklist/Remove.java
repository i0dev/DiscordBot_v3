package com.i0dev.modules.blacklist;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class Remove extends SuperDiscordCommand {

    @CommandData(commandID = "remove", identifier = "Blacklist Remove", messageLength = 2, usage = "<user>", parentClass = BlacklistManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;


        DPlayer dp = DPlayer.getDPlayer(user.getIdLong());
        if (!dp.isBlacklisted()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(user).content("{tag} is not blacklisted!").build());
            return;
        }

        dp.used().setBlacklisted(false);
        dp.save();
        e.reply(EmbedMaker.builder().user(user).embedColor(EmbedColor.SUCCESS).content("You have successfully un-blacklisted {tag}").build());
    }
}

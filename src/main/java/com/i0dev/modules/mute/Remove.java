package com.i0dev.modules.mute;

import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class Remove extends SuperDiscordCommand {

    @CommandData(commandID = "remove", identifier = "Mute Remove", messageLength = 2, usage = "<user>", parentClass = MuteManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        String reason = Utility.remainingArgFormatter(e.getOffsetSplit(), 3);
        Member member = e.getGuild().getMember(user);
        if (MuteManager.mutedRole == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Muted role is not yet setup. You can create one with {prefix}mute create").build());
            return;
        }

        if (!member.getRoles().contains(MuteManager.mutedRole)) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(user).content("{tag} is not currently muted.").build());
            return;
        }

        DPlayer dPlayer = DPlayer.getDPlayer(user);
        dPlayer.setMuted(false);
        dPlayer.save();

        new RoleQueueObject(member.getIdLong(), MuteManager.mutedRole.getIdLong(), Type.REMOVE_ROLE).add();

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).content("You have un-muted {tag}").build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} has un-muted **{tag}**").author(e.getAuthor()).user(user).build());

    }
}
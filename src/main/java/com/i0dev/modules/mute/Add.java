package com.i0dev.modules.mute;

import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class Add extends SuperDiscordCommand {

    @CommandData(commandID = "add", identifier = "Mute Add", minMessageLength = 2, usage = "<user> [reason]", parentClass = MuteManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        String reason = Utility.remainingArgFormatter(e.getOffsetSplit(), 2);
        Member member = e.getGuild().getMember(user);
        if (MuteManager.mutedRole == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Muted role is not yet setup. You can create one with {prefix}mute create").build());
            return;
        }

        if (member.getRoles().contains(MuteManager.mutedRole)) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(user).content("{tag} is already muted.").build());
            return;
        }

        new RoleQueueObject(user.getIdLong(), MuteManager.mutedRole.getIdLong(), Type.ADD_ROLE).add();
        DPlayer dPlayer = DPlayer.getDPlayer(user);
        dPlayer.setMuted(true);
        dPlayer.save();
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).content("You have muted {tag}\nReason: `{reason}`".replace("{reason}", reason)).build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} has muted **{tag}** with the reason: `{reason}`".replace("{reason}", reason)).author(e.getAuthor()).user(user).build());
    }
}

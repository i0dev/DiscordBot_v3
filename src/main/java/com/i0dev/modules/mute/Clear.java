package com.i0dev.modules.mute;

import com.i0dev.object.*;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import net.dv8tion.jda.api.entities.Member;

public class Clear extends SuperDiscordCommand {

    @CommandData(commandID = "clear", messageLength = 1, identifier = "Mute Clear", parentClass = MuteManager.class)
    public static void run(CommandEvent e) {
        if (MuteManager.mutedRole == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Muted role is not yet setup. You can create one with {prefix}mute create").build());
            return;
        }

        for (Member member : e.getGuild().getMembers()) {
            if (member.getRoles().contains(MuteManager.mutedRole)) {
                new RoleQueueObject(member.getIdLong(), MuteManager.mutedRole.getIdLong(), Type.REMOVE_ROLE).add();
            }
        }
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully cleared all muted members").build());
        LogUtil.logDiscord(EmbedMaker.builder().user(e.getAuthor()).content("{tag} has cleared all muted members").build());
    }
}

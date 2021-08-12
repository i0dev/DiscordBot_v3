package com.i0dev.modules.movement;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.NicknameUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class Remove extends SuperDiscordCommand {


    @CommandData(commandID = "remove", parentClass = MovementManager.class, messageLength = 2, usage = "<user>", identifier = "Movement Remove")
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        Member member = e.getGuild().getMember(user);
        DPlayer dPlayer = DPlayer.getDPlayer(user);

        if (!MovementManager.isAlreadyStaff(member)) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(user).content("{tag} is not currently a staff member.").build());
            return;
        }

        Role currentParentRole = MovementManager.getParentStaff(member);
        MovementManager.removeOldRoles(member, Long.valueOf(currentParentRole.getId()));
        NicknameUtil.modifyNicknameGlobally(user, "");

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content("You removed {tag} from the staff team.").user(user).build());
        MovementManager.sendMsg(EmbedMaker.builder().author(e.getAuthor()).thumbnail(dPlayer.getMinecraftSkin()).embedColor(EmbedColor.FAILURE).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Demotion").content("**{tag}** has been removed from the staff team.").build());


    }
}

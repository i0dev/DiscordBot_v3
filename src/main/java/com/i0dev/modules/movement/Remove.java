package com.i0dev.modules.movement;

import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.NicknameUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class Remove extends SuperDiscordCommand {

    public void load() {
        addOption("removeAllRoles", false);
    }

    @CommandData(commandID = "remove", parentClass = MovementManager.class, messageLength = 2, usage = "<user>", identifier = "Movement Remove")
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        Member member = e.getGuild().getMember(user);
        DPlayer dPlayer = DPlayer.getDPlayer(user);

        Role currentParentRole = MovementManager.getParentStaff(member);
        if (getOption("removeAllRoles").getAsBoolean())
            member.getRoles().forEach(role -> new RoleQueueObject(member.getIdLong(), role.getIdLong(), Type.REMOVE_ROLE).add());
        else MovementManager.removeOldRoles(member, Long.valueOf(currentParentRole.getId()));
        NicknameUtil.modifyNicknameGlobally(user, "");

        MovementObject current = MovementManager.getObject(currentParentRole);
        if (Bot.isPluginMode() && current != null && current.getLuckPermsRank() != null && current.getLuckPermsRank() != null && dPlayer.isLinked()) {
            com.i0dev.BotPlugin.runCommand("lp user {ign} parent remove ".replace("{ign}", dPlayer.getMinecraftIGN()) + current.getLuckPermsRank());
        }

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content("You removed {tag} from the staff team.").user(user).build());
        MovementManager.sendMsg(EmbedMaker.builder().author(e.getAuthor()).thumbnail(dPlayer.getMinecraftSkin()).embedColor(EmbedColor.FAILURE).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Demotion").content("**{tag}** has been removed from the staff team.").build());


    }
}

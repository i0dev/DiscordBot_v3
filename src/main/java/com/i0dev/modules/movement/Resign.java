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

public class Resign extends SuperDiscordCommand {

    public void load() {
        addOption("removeAllRoles", false);
        addMessage("resigned", "You have resigned {tag}");
        addMessage("resignedAnnounce", "**{tag}** has resigned from the staff team.");
        addOption("ingameCmd", "lp user {ign} parent remove {rank}");
    }

    @CommandData(commandID = "resign", identifier = "Movement resign", usage = "<user>", messageLength = 2, parentClass = MovementManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        Member member = e.getGuild().getMember(user);

        boolean removeAllRoles = getOption("removeAllRoles").getAsBoolean();
        if (!MovementManager.isAlreadyStaff(member) && !removeAllRoles) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(user).content("{tag} is not currently a staff member.").build());
            return;
        }

        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(user);
        Role currentParentRole = MovementManager.getParentStaff(member);
        if (removeAllRoles) {
            member.getRoles().forEach(role -> new RoleQueueObject(member.getIdLong(), role.getIdLong(), Type.REMOVE_ROLE).add());
        } else MovementManager.removeOldRoles(member, Long.valueOf(currentParentRole.getId()));
        NicknameUtil.modifyNicknameGlobally(user, "");

        MovementObject current = MovementManager.getObject(currentParentRole);
        if (Bot.getBot().isPluginMode() && current != null && current.getLuckPermsRank() != null && current.getLuckPermsRank() != null && dPlayer.isLinked()) {
            com.i0dev.BotPlugin.runCommand(getOption("ingameCmd", Resign.class).getAsString().replace("{ign}", dPlayer.getMinecraftIGN()).replace("{rank}", current.getLuckPermsRank()));
        }


        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content(getMessage("resigned", Resign.class)).user(user).build());
        MovementManager.sendMsg(EmbedMaker.builder().author(e.getAuthor()).thumbnail(dPlayer.getMinecraftSkin()).embedColor(EmbedColor.FAILURE).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Resignation").content(getMessage("resignedAnnounce", Resign.class)).build());

    }
}

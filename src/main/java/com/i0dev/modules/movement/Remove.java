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
        addMessage("removed", "You removed {tag} from the staff team.");
        addMessage("removedAnnounce", "**{tag}** has been removed from the staff team.");
        addOption("ingameCmd", "lpb user {ign} parent remove {rank}");
    }

    @CommandData(commandID = "remove", parentClass = MovementManager.class, messageLength = 2, usage = "<user>", identifier = "Movement Remove")
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        Member member = e.getGuild().getMember(user);
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(user);

        Role currentParentRole = MovementManager.getParentStaff(member);
        if (getOption("removeAllRoles").getAsBoolean())
            member.getRoles().forEach(role -> new RoleQueueObject(member.getIdLong(), role.getIdLong(), Type.REMOVE_ROLE).add());
        else MovementManager.removeOldRoles(member, Long.valueOf(currentParentRole.getId()));
        NicknameUtil.modifyNicknameGlobally(user, "");

        MovementObject current = MovementManager.getObject(currentParentRole);
        if (Bot.getBot().isPluginMode() && current != null && current.getLuckPermsRank() != null && dPlayer.isLinked()) {
            com.i0dev.BotPlugin.runCommand(getOption("serverToRunCommandsOn", MovementManager.class).getAsString(), getOption("ingameCmd", Remove.class).getAsString().replace("{ign}", dPlayer.getMinecraftIGN()).replace("{rank}", current.getLuckPermsRank()));
        }

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content(getMessage("removed", Remove.class)).user(user).build());
        MovementManager.sendMsg(EmbedMaker.builder().author(e.getAuthor()).thumbnail(dPlayer.getMinecraftSkin()).embedColor(EmbedColor.FAILURE).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Demotion").content(getMessage("removedAnnounce", Remove.class)).build());


    }
}

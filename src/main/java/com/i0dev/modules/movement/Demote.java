package com.i0dev.modules.movement;

import com.i0dev.Bot;
import com.i0dev.BotPlugin;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.NicknameUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class Demote extends SuperDiscordCommand {

    public static void load() {
        addMessage("notStaff", "{tag} is not currently a staff member.");
        addMessage("removed", "You removed {tag} from the staff team.");
        addMessage("demoted", "You demoted {tag} to {role}");
        addMessage("demotedAnnounce", "**{tag}** has been demoted to {role}");
        addMessage("removedAnnounce", "**{tag}** has been removed from the staff team.");
        addOption("ingameCommandDemoteNew", "lpb user {ign} parent add {newRank}");
        addOption("ingameCommandRemoveOld", "lpb user {ign} parent remove {oldRank}");

    }

    @CommandData(commandID = "demote", parentClass = MovementManager.class, messageLength = 2, usage = "<user>", identifier = "Movement Demote")
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        Member member = e.getGuild().getMember(user);
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(user);

        if (!MovementManager.isAlreadyStaff(member)) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content(getMessage("notStaff", Demote.class)).user(user).build());
            return;
        }

        if (MovementManager.isLowestStaff(member)) {
            Role currentParentRole = MovementManager.getParentStaff(member);
            if (getOption("removeAllRoles").getAsBoolean())
                member.getRoles().forEach(role -> new RoleQueueObject(member.getIdLong(), role.getIdLong(), Type.REMOVE_ROLE).add());
            else MovementManager.removeOldRoles(member, Long.valueOf(currentParentRole.getId()));
            NicknameUtil.modifyNicknameGlobally(user, "");

            MovementObject current = MovementManager.getObject(currentParentRole);
            if (Bot.getBot().isPluginMode() && current != null && current.getLuckPermsRank() != null && current.getLuckPermsRank() != null && dPlayer.isLinked()) {
                com.i0dev.BotPlugin.runCommand(getOption("serverToRunCommandsOn", MovementManager.class).getAsString(), getOption("ingameCommandDemoteNew", Demote.class).getAsString().replace("{ign}", dPlayer.getMinecraftIGN()).replace("{newRank}", current.getLuckPermsRank()));
            }

            e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content(getMessage("removed", Demote.class)).user(user).build());
            MovementManager.sendMsg(EmbedMaker.builder().author(e.getAuthor()).thumbnail(dPlayer.getMinecraftSkin()).embedColor(EmbedColor.FAILURE).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Demotion").content(getMessage("removedAnnounce", Demote.class)).build());
            return;
        }

        Role currentParentRole = MovementManager.getParentStaff(member);
        Role previousRole = MovementManager.getPreviousRole(currentParentRole);

        MovementManager.removeOldRoles(member, Long.valueOf(currentParentRole.getId()));
        MovementManager.giveNewRoles(member, Long.valueOf(previousRole.getId()));


        MovementObject previousRoleObject = MovementManager.getPreviousRoleObject(currentParentRole);
        NicknameUtil.modifyNicknameGlobally(user, MovementManager.getOption("nicknameFormat", MovementManager.class).getAsString().replace("{ignOrName}", dPlayer.isLinked() ? Bot.getBot().getDPlayerManager().getDPlayer(user).getMinecraftIGN() : user.getName()).replace("{displayName}", previousRoleObject.getDisplayName()));

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content(getMessage("demoted", Demote.class).replace("{role}", previousRole.getAsMention())).user(user).build());
        MovementManager.sendMsg(EmbedMaker.builder().author(e.getAuthor()).thumbnail(dPlayer.getMinecraftSkin()).embedColor(EmbedColor.FAILURE).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Demotion").content(getMessage("demotedAnnounce", Demote.class).replace("{role}", previousRole.getAsMention())).build());

        MovementObject current = MovementManager.getObject(currentParentRole);
        if (Bot.getBot().isPluginMode() && current != null && current.getLuckPermsRank() != null && dPlayer.isLinked()) {
            com.i0dev.BotPlugin.runCommand(getOption("serverToRunCommandsOn", MovementManager.class).getAsString(), getOption("ingameCommandRemoveOld", Demote.class).getAsString().replace("{ign}", dPlayer.getMinecraftIGN()).replace("{oldRank}", current.getLuckPermsRank()));
        }
        if (Bot.getBot().isPluginMode() && previousRoleObject.getLuckPermsRank() != null && dPlayer.isLinked()) {
            com.i0dev.BotPlugin.runCommand(getOption("serverToRunCommandsOn", MovementManager.class).getAsString(), getOption("ingameCommandDemoteNew", Demote.class).getAsString().replace("{ign}", dPlayer.getMinecraftIGN()).replace("{newRank}", previousRoleObject.getLuckPermsRank()));
        }

    }
}

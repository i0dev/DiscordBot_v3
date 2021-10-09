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

public class Promote extends SuperDiscordCommand {

    public static void load() {
        addMessage("assigned", "You assigned {tag} to {role}");
        addMessage("promoted", "You promoted {tag} to {role}");
        addMessage("highest", "{tag} is already the highest staff role.");
        addMessage("promoteAnnounce","**{tag}** has been promoted to {role}");
        addMessage("assignedAnnounce", "**{tag}** has been assigned to {role}");
        addOption("ingameCommandPromoteNew", "lp user {ign} parent add {newRank}");
        addOption("ingameCommandRemoveOld", "lp user {ign} parent remove {oldRank}");

    }

    @CommandData(commandID = "promote", identifier = "Movement Promote", usage = "<user> [role]", minMessageLength = 2, maxMessageLength = 3, parentClass = MovementManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        Role assign = null;
        if (e.getOffsetSplit().size() == 3) {
            if ((assign = FindUtil.getRole(e.getOffsetSplit().get(2), e.getMessage())) == null) return;
        }
        Member member = e.getGuild().getMember(user);
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(user);


        if (assign != null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content(getMessage("assigned", Promote.class).replace("{role}", assign.getAsMention())).user(user).build());
            MovementManager.sendMsg(EmbedMaker.builder().thumbnail(dPlayer.getMinecraftSkin()).author(e.getAuthor()).embedColor(EmbedColor.SUCCESS).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Promotion").content(getMessage("assignedAnnounce", Promote.class).replace("{role}", assign.getAsMention())).build());
            MovementObject assignRoleObject = MovementManager.getObject(assign);
            new RoleQueueObject(user.getIdLong(), assign.getIdLong(), Type.ADD_ROLE).add();
            if (assignRoleObject != null) {
                NicknameUtil.modifyNicknameGlobally(user, MovementManager.getOption("nicknameFormat", MovementManager.class).getAsString().replace("{ignOrName}", dPlayer.isLinked() ? Bot.getBot().getDPlayerManager().getDPlayer(user).getMinecraftIGN() : user.getName()).replace("{displayName}", assignRoleObject.getDisplayName()));
                MovementManager.giveNewRoles(e.getGuild().getMember(user), assign.getIdLong());
            }
            return;
        }


        if (!MovementManager.isAlreadyStaff(member)) {
            MovementObject firstRoleObject = MovementManager.getTracks().get(0);
            Role role = Bot.getBot().getJda().getRoleById(firstRoleObject.getMainRole());
            MovementManager.giveNewRoles(member, MovementManager.getTracks().get(0).getMainRole());
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content(getMessage("promoted", Promote.class).replace("{role}", role.getAsMention())).user(user).build());
            MovementManager.sendMsg(EmbedMaker.builder().thumbnail(dPlayer.getMinecraftSkin()).author(e.getAuthor()).embedColor(EmbedColor.SUCCESS).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("New Promotion").content(getMessage("promoteAnnounce", Promote.class).replace("{role}", role.getAsMention())).build());
            NicknameUtil.modifyNicknameGlobally(user, MovementManager.getOption("nicknameFormat", MovementManager.class).getAsString().replace("{ignOrName}", Bot.getBot().getDPlayerManager().getDPlayer(user).isLinked() ? Bot.getBot().getDPlayerManager().getDPlayer(user).getMinecraftIGN() : user.getName()).replace("{displayName}", firstRoleObject.getDisplayName()));

            if (Bot.getBot().isPluginMode() && firstRoleObject.getLuckPermsRank() != null && dPlayer.isLinked()) {
                com.i0dev.BotPlugin.get().getProxy().getConsole().sendMessages("lp user {ign} parent add ".replace("{ign}", dPlayer.getMinecraftIGN()) + firstRoleObject.getLuckPermsRank());
            }

            return;
        }
        if (MovementManager.isHighestStaff(member)) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content(getMessage("highest", Promote.class)).user(user).build());
            return;
        }

        Role currentParentRole = MovementManager.getParentStaff(member);
        Role nextRole = MovementManager.getNextRole(currentParentRole);
        MovementManager.removeOldRoles(member, currentParentRole.getIdLong());
        MovementManager.giveNewRoles(member, nextRole.getIdLong());
        MovementObject nextRoleObject = MovementManager.getNextRoleObject(currentParentRole);

        NicknameUtil.modifyNicknameGlobally(user, MovementManager.getOption("nicknameFormat", Promote.class).getAsString().replace("{ignOrName}", dPlayer.isLinked() ? Bot.getBot().getDPlayerManager().getDPlayer(user).getMinecraftIGN() : user.getName()).replace("{displayName}", nextRoleObject.getDisplayName()));

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content(getMessage("promoted", Promote.class).replace("{role}", nextRole.getAsMention())).user(user).build());
        MovementManager.sendMsg(EmbedMaker.builder().thumbnail(dPlayer.getMinecraftSkin()).author(e.getAuthor()).embedColor(EmbedColor.SUCCESS).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Promotion").content(getMessage("promoteAnnounce", Promote.class).replace("{role}", nextRole.getAsMention())).build());
        MovementObject current = MovementManager.getObject(currentParentRole);
        if (Bot.getBot().isPluginMode() && current != null && current.getLuckPermsRank() != null && nextRoleObject.getLuckPermsRank() != null && dPlayer.isLinked()) {
            com.i0dev.BotPlugin.runCommand(getOption("ingameCommandRemoveOld", Promote.class).getAsString().replace("{ign}", dPlayer.getMinecraftIGN()).replace("{oldRank}", current.getLuckPermsRank()));
            com.i0dev.BotPlugin.runCommand(getOption("ingameCommandPromoteNew", Promote.class).getAsString().replace("{ign}", dPlayer.getMinecraftIGN()).replace("{newRank}", nextRoleObject.getLuckPermsRank()));
        }

    }
}

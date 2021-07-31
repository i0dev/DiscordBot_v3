package com.i0dev.modules.movement;

import com.i0dev.Bot;
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
import org.json.simple.JSONObject;

public class Promote extends SuperDiscordCommand {


    @CommandData(commandID = "promote", identifier = "Movement Promote", usage = "<user>", messageLength = 2, parentClass = MovementManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        Member member = e.getGuild().getMember(user);
        String thumbnail = DPlayer.getDPlayer(user).getMinecraftUUID().equals("") ? null : "https://crafatar.com/renders/body/" + DPlayer.getDPlayer(user).getMinecraftUUID();

        if (!MovementManager.isAlreadyStaff(member)) {
            MovementObject firstRoleObject = MovementManager.getTracks().get(0);
            Role role = Bot.getJda().getRoleById(firstRoleObject.getMainRole());
            MovementManager.giveNewRoles(member, MovementManager.getTracks().get(0).getMainRole());
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content("You promoted {tag} to {role}".replace("{role}", role.getAsMention())).user(user).build());
            MovementManager.sendMsg(EmbedMaker.builder().thumbnail(thumbnail).author(e.getAuthor()).embedColor(EmbedColor.SUCCESS).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("New Promotion").content("**{tag}** has been promoted to {role}".replace("{role}", role.getAsMention())).build());
            NicknameUtil.modifyNickname(user, MovementManager.getOption("nicknameFormat", MovementManager.class).getAsString().replace("{ignOrName}", DPlayer.getDPlayer(user).isLinked() ? DPlayer.getDPlayer(user).getMinecraftIGN() : user.getName()).replace("{displayName}", firstRoleObject.getDisplayName()));

            return;
        }
        if (MovementManager.isHighestStaff(member)) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("{tag} is already the highest staff role.").user(user).build());
            return;
        }

        Role currentParentRole = MovementManager.getParentStaff(member);
        Role nextRole = MovementManager.getNextRole(currentParentRole);
        MovementManager.removeOldRoles(member, currentParentRole.getIdLong());
        MovementManager.giveNewRoles(member, nextRole.getIdLong());
        MovementObject nextRoleObject = MovementManager.getNextRoleObject(currentParentRole);

        NicknameUtil.modifyNickname(user, MovementManager.getOption("nicknameFormat", MovementManager.class).getAsString().replace("{ignOrName}", DPlayer.getDPlayer(user).isLinked() ? DPlayer.getDPlayer(user).getMinecraftIGN() : user.getName()).replace("{displayName}", nextRoleObject.getDisplayName()));

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content("You promoted {tag} to {role}".replace("{role}", nextRole.getAsMention())).user(user).build());
        MovementManager.sendMsg(EmbedMaker.builder().thumbnail(thumbnail).author(e.getAuthor()).embedColor(EmbedColor.SUCCESS).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Promotion").content("**{tag}** has been promoted to {role}".replace("{role}", nextRole.getAsMention())).build());


    }
}

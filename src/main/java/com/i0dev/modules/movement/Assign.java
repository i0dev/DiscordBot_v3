package com.i0dev.modules.movement;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;

import java.time.ZonedDateTime;

public class Assign extends SuperDiscordCommand {

    @CommandData(commandID = "assign", messageLength = 3, usage = "<user> <role>", identifier = "Movement Assign", parentClass = MovementManager.class)
    public static void run(CommandEvent e) {
        User user;
        Role role;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        if ((role = FindUtil.getRole(e.getOffsetSplit().get(2), e.getMessage())) == null) return;

        MovementManager.giveNewRoles(e.getGuild().getMember(user), role.getIdLong());
        MovementObject assignRoleObject = MovementManager.getObject(role);
        NicknameUtil.modifyNickname(user, MovementManager.getOption("nicknameFormat", MovementManager.class).getAsString().replace("{ignOrName}", DPlayer.getDPlayer(user).isLinked() ? DPlayer.getDPlayer(user).getMinecraftIGN() : user.getName()).replace("{displayName}", assignRoleObject.getDisplayName()));
        String thumbnail = !DPlayer.getDPlayer(user).isLinked() ? null : "https://crafatar.com/renders/body/" + DPlayer.getDPlayer(user).getMinecraftUUID() + "?scale=7";
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).embedColor(EmbedColor.SUCCESS).content("You assigned {tag} to {role}".replace("{role}", role.getAsMention())).user(user).build());
        MovementManager.sendMsg(EmbedMaker.builder().author(e.getAuthor()).thumbnail(thumbnail).author(e.getAuthor()).embedColor(EmbedColor.SUCCESS).user(user).authorImg(user.getEffectiveAvatarUrl()).authorName("Assignation").content("**{tag}** has been assigned to {role}".replace("{role}", role.getAsMention())).build());
    }
}

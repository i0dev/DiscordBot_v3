package com.i0dev.modules.basic;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;

public class CmdProfile extends DiscordCommand {

    @CommandData(commandID = "cmd_profile", identifier = "Profile", maxMessageLength = 2, usage = "[user]")
    public static void run(CommandEvent e) {
        User user = e.getAuthor();
        if (e.getSplit().length > 1) {
            if ((user = FindUtil.getUser(e.getSplit()[1], e.getMessage())) == null) return;
        }

        DPlayer dPlayer = DPlayer.getDPlayer(user.getIdLong());

        StringBuilder bot = new StringBuilder();
        bot.append("Tickets Closed: ").append("`{ticketsClosed}`").append("\n");
        bot.append("Invites: ").append("`{invites}`").append("\n");
        bot.append("Warnings: ").append("`{warnings}`").append("\n");
        bot.append("Blacklisted: ").append("`{blacklisted}`").append("\n");
        User invitedBy = null;
        if (dPlayer.getInvitedByDiscordID() != 0) {
            invitedBy = Bot.getBot().getJda().retrieveUserById(dPlayer.getInvitedByDiscordID()).complete();
        }
        bot.append("Invited By: `").append(invitedBy == null ? "Unkown" : invitedBy.getAsTag()).append("`\n");
        bot.append("Linked IGN: ").append("`{ign}`").append("\n");
        bot.append("Boosts: ").append("`{boosts}`").append("\n");
        bot.append("Boost Credits: ").append("`{boostCredits}`").append("\n");

        StringBuilder general = new StringBuilder();
        general.append("Created Date: ").append("{timeCreated}").append("\n");
        general.append("Joined Date: ").append("<t:" + (e.getGuild().getMember(user).getTimeJoined().toInstant().toEpochMilli() / 1000L) + ":R>").append("\n");
        general.append("Mention: ").append("{mention}").append("\n");
        general.append("Tag: ").append("`{tag}`").append("\n");
        general.append("DiscordID: ").append("`{id}`").append("\n");
        general.append("Effective Name: ").append("`{effectiveName}`").append("\n");
        general.append("Bot: ").append("`{isBot}`").append("\n");
        general.append("Online Status: ").append("`").append(e.getGuild().getMember(user).getOnlineStatus().getKey()).append("`").append("\n");
        general.append("Boosting: ").append("`{isBoosting}`").append("\n");

        e.reply(EmbedMaker.builder()
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__General Info__", general.toString(), true),
                        new MessageEmbed.Field("__Activity Info__", bot.toString(), true),
                        new MessageEmbed.Field("__Roles__", Utility.FormatList(e.getGuild().getMember(user).getRoles()), false)
                })
                .authorName("{tag}'s User Profile")
                .authorImg(user.getEffectiveAvatarUrl())
                .user(user)
                .thumbnail("https://crafatar.com/renders/body/" + (dPlayer.getMinecraftUUID().equals("") ? "ec561538-f3fd-461d-aff5-086b22154bce" : dPlayer.getMinecraftUUID()) + "?scale=7&overlay")
                .build());
    }

}
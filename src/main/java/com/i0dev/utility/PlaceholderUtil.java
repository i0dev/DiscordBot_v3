package com.i0dev.utility;

import com.i0dev.Bot;
import com.i0dev.BotPlugin;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.discordLinking.DPlayer;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.format.DateTimeFormatter;

public class PlaceholderUtil {

    public static String convert(String message, EmbedMaker maker) {
        return convert(message, maker.getUser(), maker.getMentioned(), maker.getAuthor());
    }

    @SneakyThrows
    public static String convert(String message, User user, User mentioned, User author) {
        JDA jda = Bot.getJda();
        Guild guild = Utility.getAllowedGuilds().get(0);
        if (message == null) return null;

        // new User
        if (user != null) {
            DPlayer dPlayer = DPlayer.getDPlayer(user.getIdLong());
            Member member = guild.getMember(user);
            message = message
                    .replace("{tag}", user.getAsTag())
                    .replace("{mention}", user.getAsMention())
                    .replace("{id}", user.getId())
                    .replace("{ign}", dPlayer.getMinecraftIGN().equals("") ? "Not Linked" : dPlayer.getMinecraftIGN())
                    .replace("{points}", Utility.numberFormat.format(dPlayer.getPoints()))
                    .replace("{uuid}", dPlayer.getMinecraftUUID())
                    .replace("{invites}", dPlayer.getInvites() + "")
                    .replace("{isBot}", user.isBot() ? "Yes" : "No")
                    .replace("{effectiveName}", member == null ? Bot.getJda().getSelfUser().getName() : member.getEffectiveName())
                    .replace("{timeCreated}", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME))
                    .replace("{isBoosting}", guild.getBoosters().contains(member) ? "Yes" : "No")
                    .replace("{name}", user.getName());
        }

        //Mentioned User
        if (mentioned != null) {
            DPlayer dPlayer = DPlayer.getDPlayer(mentioned.getIdLong());
            message = message
                    .replace("{mentionedUserName}", mentioned.getName())
                    .replace("{mentionedUserTag}", mentioned.getAsTag())
                    .replace("{mentionedUserTagBold}", "**" + mentioned.getAsTag() + "**")
                    .replace("{mentionedUserMention}", mentioned.getAsMention())
                    .replace("{mentionedUserID}", mentioned.getId())
                    .replace("{mentionedUserAvatarUrl}", mentioned.getEffectiveAvatarUrl())

                    .replace("{mentionedUserLinkStatus}", dPlayer.isLinked() ? "Linked" : "Not Linked")
                    .replace("{mentionedUserInvitedByTag}", dPlayer.getInvitedByDiscordID() == 0 ? "No Data" : Bot.getJda().retrieveUserById(dPlayer.getInvitedByDiscordID()).complete().getAsTag())
                    .replace("{mentionedUserLinkedIGN}", dPlayer.getMinecraftIGN().equals("") ? "Not Linked" : dPlayer.getMinecraftIGN())
                    .replace("{mentionedUserMinecraftUUID}", dPlayer.getMinecraftUUID().equals("") ? "Not Linked" : dPlayer.getMinecraftUUID())
                    .replace("{mentionedUserIsBlacklisted}", dPlayer.isBlacklisted() ? "Yes" : "No")
                    .replace("{mentionedUserPointsCount}", Utility.decimalFormat.format(dPlayer.getPoints()))
                    .replace("{mentionedUserBoostCount}", dPlayer.getBoosts() + "")
                    .replace("{mentionedUserBoostCredits}", dPlayer.getBoostCredits() + "")
                    .replace("{mentionedUserRewardsClaimed}", dPlayer.getRewardsClaimed() + "")
                    .replace("{mentionedUserHasReclaim}", dPlayer.isClaimedReclaim() ? "Yes" : "No")

                    .replace("{mentionedUserInviteCount}", dPlayer.getInvites() + "")
                    .replace("{mentionedUserTicketsClosed}", dPlayer.getTicketsClosed() + "")
                    .replace("{mentionedUserWarnCount}", dPlayer.getWarnings() + "")
                    .replace("{mentionedUserRoleCount}", guild.getMember(mentioned).getRoles().size() + "")
                    .replace("{mentionedUserIsAdministrator}", guild.getMember(mentioned).getPermissions().contains(Permission.ADMINISTRATOR) + "")
                    .replace("{mentionedUserEffectiveName}", guild.getMember(mentioned).getEffectiveName());
        } else {
            message = message
                    .replace("{mentionedUserTag}", "Unknown");
        }

        //Author
        if (author != null) {
            DPlayer dPlayer = DPlayer.getDPlayer(author.getIdLong());
            message = message
                    .replace("{authorName}", author.getName())
                    .replace("{authorTag}", author.getAsTag())
                    .replace("{authorTagBold}", "**" + author.getAsTag() + "**")
                    .replace("{authorMention}", author.getAsMention())
                    .replace("{authorID}", author.getId())
                    .replace("{authorAvatarUrl}", author.getEffectiveAvatarUrl())

                    .replace("{authorLinkStatus}", dPlayer.isLinked() ? "Linked" : "Not Linked")
                    .replace("{authorLinkedIGN}", dPlayer.getMinecraftIGN().equals("") ? "Not Linked" : dPlayer.getMinecraftIGN())
                    .replace("{authorLinkedIGNOrDiscordName}", dPlayer.getMinecraftIGN().equals("") ? author.getName() : dPlayer.getMinecraftIGN())
                    .replace("{authorIsBlacklisted}", dPlayer.isBlacklisted() ? "Yes" : "No")
                    .replace("{authorPointsCount}", Utility.decimalFormat.format(dPlayer.getPoints()))
                    .replace("{authorBoostCount}", dPlayer.getBoosts() + "")
                    .replace("{authorBoostCredits}", dPlayer.getBoostCredits() + "")
                    .replace("{authorRewardsClaimed}", dPlayer.getRewardsClaimed() + "")
                    .replace("{authorHasReclaim}", dPlayer.isClaimedReclaim() ? "Yes" : "No")

                    .replace("{authorInviteCount}", dPlayer.getInvites() + "")
                    .replace("{authorTicketsClosed}", dPlayer.getTicketsClosed() + "")
                    .replace("{authorWarnCount}", dPlayer.getWarnings() + "")
                    .replace("{authorMemberRoleCount}", guild.getMember(author).getRoles().size() + "")
                    .replace("{authorIsAdministrator}", guild.getMember(author).getPermissions().contains(Permission.ADMINISTRATOR) + "")
                    .replace("{authorEffectiveName}", guild.getMember(author).getEffectiveName());
        } else {
            message = message
                    .replace("{authorTag}", "Unknown");
        }

        //Guild
        if (guild != null) {
            message = message
                    .replace("{guildName}", guild.getName())
                    .replace("{guildMemberCount}", guild.getMemberCount() + "")
                    .replace("{guildBoostTier}", guild.getBoostTier().getKey() + "")
                    .replace("{guildBannerUrl}", guild.getBannerUrl() == null ? "No Banner" : guild.getBannerUrl())
                    .replace("{guildOwnerTag}", guild.getOwner().getUser().getAsTag())
                    .replace("{guildOwnerMention}", guild.getOwner().getUser().getAsMention())
                    .replace("{guildOwnerID}", guild.getOwner().getUser().getId())
                    .replace("{guildOwnerAvatarUrl}", guild.getOwner().getUser().getEffectiveAvatarUrl())
                    .replace("{guildOwnerName}", guild.getOwner().getUser().getName());
        }

        //Self User
        message = message
                .replace("{botTag}", jda.getSelfUser().getAsTag())
                .replace("{botMention}", jda.getSelfUser().getAsMention())
                .replace("{botAvatarUL}", jda.getSelfUser().getEffectiveAvatarUrl())
                .replace("{botID}", jda.getSelfUser().getId())
                .replace("{botName}", jda.getSelfUser().getName());

        //General
        message = message
                .replace("{DiscordBotAuthor}", "i0#0001")
                .replace("{DiscordBotPluginMode}", Bot.pluginMode ? "Yes" : "No")
                .replace("{prefix}", GeneralConfig.get().getPrefixes().get(0))
                .replace("{version}", "3.0.0");


        //plugin mode
        if (Bot.isPluginMode()) {
            message = message
                    .replace("{onlinePlayers}", BotPlugin.server.getOnlineCount() + "");
        }

        return message;
    }
}

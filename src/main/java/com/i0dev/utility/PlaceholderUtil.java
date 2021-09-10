package com.i0dev.utility;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.discordLinking.DPlayer;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class PlaceholderUtil {

    public static String convert(String message, EmbedMaker maker) {
        return convert(message, maker.getUser(), maker.getAuthor());
    }

    @SneakyThrows
    public static String convert(String message, User user, User author) {
        JDA jda = Bot.getBot().getJda();
        Guild guild = Utility.getAllowedGuilds().get(0);
        if (message == null) return null;

        // new User
        if (user != null) {
            DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(user.getIdLong());
            Member member = guild.getMember(user);
            message = message
                    .replace("{tag}", user.getAsTag())
                    .replace("{mention}", user.getAsMention())
                    .replace("{id}", user.getId())
                    .replace("{blacklisted}", dPlayer.isBlacklisted() ? "Yes" : "No")
                    .replace("{boosts}", dPlayer.getBoosts() + "")
                    .replace("{boostCredits}", dPlayer.getBoostCredits() + "")
                    .replace("{ticketsClosed}", dPlayer.getTicketsClosed() + "")
                    .replace("{warnings}", dPlayer.getWarnings() + "")
                    .replace("{ign}", dPlayer.getMinecraftIGN().equals("") ? "Not Linked" : dPlayer.getMinecraftIGN())
                    .replace("{uuid}", dPlayer.getMinecraftUUID())
                    .replace("{invites}", dPlayer.getInvites() + "")
                    .replace("{isBot}", user.isBot() ? "Yes" : "No")
                    .replace("{effectiveName}", member == null ? Bot.getBot().getJda().getSelfUser().getName() : member.getEffectiveName())
                    .replace("{timeCreated}", "<t:" + (user.getTimeCreated().toInstant().toEpochMilli() / 1000L) + ":R>")
                    .replace("{linkTime}", "<t:" + (dPlayer.getLinkedTime() / 1000L) + ":R>")
                    .replace("{isBoosting}", guild.getBoosters().contains(member) ? "Yes" : "No")
                    .replace("{name}", user.getName());
        }

        //Author
        if (author != null) {
            DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(author.getIdLong());
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
                    .replace("{authorBoostCount}", dPlayer.getBoosts() + "")
                    .replace("{authorBoostCredits}", dPlayer.getBoostCredits() + "")

                    .replace("{authorInviteCount}", dPlayer.getInvites() + "")
                    .replace("{authorTicketsClosed}", dPlayer.getTicketsClosed() + "")
                    .replace("{authorWarnCount}", dPlayer.getWarnings() + "")
                    .replace("{authorMemberRoleCount}", guild.getMember(author).getRoles().size() + "")
                    .replace("{authorIsAdministrator}", guild.getMember(author).getPermissions().contains(Permission.ADMINISTRATOR) + "")
                    .replace("{authorEffectiveName}", guild.getMember(author).getEffectiveName());
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
        if (Bot.getBot().getJda() != null)
            message = message
                    .replace("{botTag}", jda.getSelfUser().getAsTag())
                    .replace("{botMention}", jda.getSelfUser().getAsMention())
                    .replace("{botAvatarUL}", jda.getSelfUser().getEffectiveAvatarUrl())
                    .replace("{botID}", jda.getSelfUser().getId())
                    .replace("{botName}", jda.getSelfUser().getName());

        //General
        message = message
                .replace("{botAuthor}", "i0#0001")
                .replace("{pluginMode}", Bot.getBot().isPluginMode() ? "Yes" : "No")
                .replace("{prefix}", GeneralConfig.get().getPrefixes().get(0))
                .replace("{version}", "3.0.15");

        //plugin mode
        if (Bot.getBot().isPluginMode()) {
            message = message
                    .replace("{onlinePlayers}", com.i0dev.BotPlugin.server.getOnlineCount() + "");
        }

        return message;
    }
}

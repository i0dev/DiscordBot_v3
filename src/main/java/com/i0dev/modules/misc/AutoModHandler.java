package com.i0dev.modules.misc;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.config.MiscConfig;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class AutoModHandler extends ListenerAdapter {


    static List<String> lastMessages = new ArrayList<>();

    public static int getOccurrences(String msg) {
        return (int) lastMessages.stream().filter(s -> s.equalsIgnoreCase(msg)).count();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;

        lastMessages.add(0, e.getMessage().getContentRaw());
        if (lastMessages.size() > MiscConfig.get().getAutoMod_SameMessageLockdownNumber() + 7)
            lastMessages.remove(lastMessages.size() - 1);

        if (getOccurrences(e.getMessage().getContentRaw()) > MiscConfig.get().getAutoMod_SameMessageLockdownNumber()) {
            e.getMessage().delete().queue();
            e.getChannel().sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().content("{tag}, You have triggered an anti-spam system. Your message has been deleted, and the channel locked.").embedColor(EmbedColor.FAILURE).user(e.getAuthor()).build())).queue();

            MiscConfig.get().getAutoMod_lockdownRolesToDenySendingMessages().forEach(aLong -> {
                Role role = Bot.getBot().getJda().getRoleById(aLong);
                if (role == null) return;

                Collection<Permission> deny = new HashSet<>();
                Collection<Permission> allowed = new HashSet<>();
                PermissionOverride ee = e.getChannel().getPermissionOverride(role);
                if (ee != null) {
                    deny = ee.getDenied();
                    allowed = ee.getAllowed();
                }
                deny.add(Permission.MESSAGE_WRITE);
                e.getChannel().putPermissionOverride(role).setAllow(allowed).setDeny(deny).queue();
            });

            LogUtil.logDiscord(EmbedMaker.builder().user(e.getAuthor()).field(new MessageEmbed.Field("AutoMod Locked Channel", "{tag} triggered the anti-spam system in {channel}".replace("{channel}", e.getChannel().getAsMention()), false)).build());
            return;
        }


        int pings = e.getMessage().getMentionedUsers().size();
        if (pings >= MiscConfig.get().getAutoMod_PingsPerMessageLimit()) {
            if (MiscConfig.get().isAutoMod_banOnMaxPings()) {
                e.getMessage().delete().queue();
                e.getGuild().ban(e.getAuthor(), 1).queue();
                if (MiscConfig.get().autoMod_log) {
                    LogUtil.logDiscord(EmbedMaker.builder().user(e.getAuthor()).field(new MessageEmbed.Field("AutoMod Banned User", "{tag} pinged too many users [{amt}]".replace("{amt}", pings + ""), false)).build());
                    return;
                }
            }
            if (MiscConfig.get().isAutoMod_deleteOnMaxPings()) {
                e.getMessage().delete().queue();
                if (MiscConfig.get().autoMod_log) {
                    LogUtil.logDiscord(EmbedMaker.builder().user(e.getAuthor()).field(new MessageEmbed.Field("AutoMod Deleted Message", "{tag} pinged too many users [{amt}]".replace("{amt}", pings + ""), false)).build());
                    return;
                }
            }
        }

        if (MiscConfig.get().autoMod_whitelistMode)
            if (!Utility.isChannelInList(e.getChannel(), MiscConfig.get().autoMod_channels)) return;
        String messageContent = e.getMessage().getContentRaw();
        for (String word : MiscConfig.get().getAutoMod_words()) {
            if (messageContent.contains(word)) {
                e.getMessage().delete().queue();
                if (MiscConfig.get().autoMod_log)
                    LogUtil.logDiscord(EmbedMaker.builder().user(e.getAuthor()).field(new MessageEmbed.Field("AutoMod Deleted Message", "{tag} said a blacklisted word:\n||{msg}||".replace("{msg}", messageContent), false)).build());
                break;
            }
        }
    }

}
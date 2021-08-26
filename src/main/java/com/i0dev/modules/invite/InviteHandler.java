package com.i0dev.modules.invite;

import com.i0dev.Bot;
import com.i0dev.config.MiscConfig;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerFieldType;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InviteHandler extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (!Utility.isValidGuild(e.getGuild())) return;
        e.getGuild().retrieveInvites().queue(retrievedInvites -> {
            for (final Invite retrievedInvite : retrievedInvites) {
                final String code = retrievedInvite.getCode();
                final InviteData cachedInvite = inviteCache.get(code);
                if (cachedInvite == null || retrievedInvite.getUses() == cachedInvite.getUses() || retrievedInvite.getInviter() == null)
                    continue;
                cachedInvite.incrementUses();

                DPlayer inviter = Bot.getBot().getDPlayerManager().getDPlayer(retrievedInvite.getInviter());
                inviter.increase(DPlayerFieldType.INVITES);

                DPlayer joined = Bot.getBot().getDPlayerManager().getDPlayer(e.getMember());
                joined.setInvitedByDiscordID(inviter.getDiscordID());
                joined.used().save();

                if (MiscConfig.get().invite_joinLog)
                    LogUtil.logDiscord(EmbedMaker.builder().authorImg(e.getUser().getEffectiveAvatarUrl()).embedColor(EmbedColor.SUCCESS).content("**{tag}** joined the server, invited by {authorTag}").user(e.getUser()).author(retrievedInvite.getInviter()).build());
                break;
            }
        });
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        if (!Utility.isValidGuild(e.getGuild())) return;
        DPlayer left = Bot.getBot().getDPlayerManager().getDPlayer(e.getUser());
        if (left.getInvitedByDiscordID() == 0) return;
        DPlayer inviter = Bot.getBot().getDPlayerManager().getDPlayer(e.getJDA().retrieveUserById(left.getInvitedByDiscordID()).complete());
        inviter.decrease(DPlayerFieldType.INVITES);
        if (MiscConfig.get().invite_leaveLog)
            LogUtil.logDiscord(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("**{tag}** left the server, invited by {authorTag}").user(e.getUser()).author(e.getJDA().retrieveUserById(left.getInvitedByDiscordID()).complete()).build());
        left.setInvitedByDiscordID(0);
        left.used().save();
    }


    public static final Map<String, InviteData> inviteCache = new ConcurrentHashMap<>();

    @Override
    public void onGuildInviteCreate(final GuildInviteCreateEvent event) {
        final String code = event.getCode();
        final InviteData inviteData = new InviteData(event.getInvite());
        inviteCache.put(code, inviteData);
    }

    @Override
    public void onGuildInviteDelete(final GuildInviteDeleteEvent event) {
        final String code = event.getCode();
        inviteCache.remove(code);
    }

    @Override
    public void onGuildReady(final GuildReadyEvent event) {
        final Guild guild = event.getGuild();
        attemptInviteCaching(guild);
    }

    @Override
    public void onGuildLeave(final GuildLeaveEvent event) {
        final long guildId = event.getGuild().getIdLong();
        inviteCache.entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);
    }

    public static void attemptInviteCaching(final Guild guild) {
        if (guild == null) return;
        final Member selfMember = guild.getSelfMember();
        if (!selfMember.hasPermission(Permission.MANAGE_SERVER))
            return;

        guild.retrieveInvites().queue(retrievedInvites -> retrievedInvites.forEach(retrievedInvite -> inviteCache.put(retrievedInvite.getCode(), new InviteData(retrievedInvite))));
    }

}

class InviteData {
    private final long guildId;
    private int uses;

    public InviteData(final Invite invite) {
        this.guildId = invite.getGuild().getIdLong();
        this.uses = invite.getUses();
    }

    public long getGuildId() {
        return guildId;
    }

    public int getUses() {
        return uses;
    }

    public void incrementUses() {
        this.uses++;
    }
}

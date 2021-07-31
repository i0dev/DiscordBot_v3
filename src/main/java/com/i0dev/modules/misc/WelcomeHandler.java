package com.i0dev.modules.misc;

import com.i0dev.Bot;
import com.i0dev.config.MiscConfig;
import com.i0dev.object.RoleQueueObject;
import com.i0dev.object.Type;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class WelcomeHandler extends ListenerAdapter {


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (!MiscConfig.get().welcome_enabled) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        MiscConfig.get().welcome_roles.forEach(roleID -> new RoleQueueObject(e.getUser().getIdLong(), roleID, Type.ADD_ROLE).add());
        TextChannel channel = Bot.getJda().getTextChannelById(MiscConfig.get().welcome_channel);
        if (channel == null) return;
        if (MiscConfig.get().welcome_pingJoin)
            channel.sendMessage(e.getMember().getAsMention()).complete();
        String image = MiscConfig.get().welcome_image.equals("") ? null : MiscConfig.get().welcome_image;
        String thumbnail = MiscConfig.get().welcome_UserThumbnail ? e.getUser().getEffectiveAvatarUrl() : Bot.getJda().getSelfUser().getEffectiveAvatarUrl();
        channel.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().thumbnail(thumbnail).image(image).title(MiscConfig.get().welcome_title).content(MiscConfig.get().welcome_content).user(e.getUser()).build())).queue();
    }
}

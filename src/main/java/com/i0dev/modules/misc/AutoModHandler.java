package com.i0dev.modules.misc;

import com.i0dev.config.MiscConfig;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoModHandler extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        if (MiscConfig.get().autoMod_whitelistMode)
            if (!Utility.isChannelInList(e.getChannel(), MiscConfig.get().autoMod_channels)) return;
        String messageContent = e.getMessage().getContentRaw();
        for (String word : MiscConfig.get().getAutoMod_words()) {
            if (messageContent.contains(word)) {
                e.getMessage().delete().queue();
                if (MiscConfig.get().autoMod_log)
                    LogUtil.logDiscord(EmbedMaker.builder().field(new MessageEmbed.Field("AutoMod Deleted Message", "{tag} said a blacklisted word:\n||{msg}||".replace("{msg}", messageContent), false)).build());
                break;
            }
        }
    }
}
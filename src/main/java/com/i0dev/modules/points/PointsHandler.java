package com.i0dev.modules.points;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.Utility;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class PointsHandler extends ListenerAdapter {

    private static final double imageSent = PointsManager.getOption("imageSent", PointsManager.class).getAsDouble();
    private static final double videoSent = PointsManager.getOption("videoSent", PointsManager.class).getAsDouble();
    private static final double messageCharacterModifier = PointsManager.getOption("messageCharacterModifier", PointsManager.class).getAsDouble();
    private static final boolean prevBoostsEffectBoostPoints = PointsManager.getOption("prevBoostsEffectBoostPoints", PointsManager.class).getAsBoolean();
    private static final double boost = PointsManager.getOption("boost", PointsManager.class).getAsDouble();
    private static final double reaction = PointsManager.getOption("reaction", PointsManager.class).getAsDouble();


    public static void performBoost(int boosts, User user) {
        DPlayer dpLayer = DPlayer.getDPlayer(user);
        double pointsToGive = 0;
        if (prevBoostsEffectBoostPoints) {
            int boostMath = 0;
            for (int i = 0; i < boosts; i++) {
                long numerator = 2;
                long denominator = dpLayer.getBoosts();
                if (dpLayer.getBoosts() == 0 || dpLayer.getBoosts() == 1) {
                    numerator = 1;
                    denominator = 1;
                }
                double math = boost * numerator / denominator;
                boostMath += math;
            }
            dpLayer.save();

            pointsToGive += boostMath;
        } else {
            for (int i = 0; i < boosts; i++) {
                pointsToGive += boost;
            }
        }

        dpLayer.setPoints(dpLayer.getPoints() + pointsToGive);
        dpLayer.save();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        double pointsToGive = messageCharacterModifier * e.getMessage().getContentRaw().length();
        for (Message.Attachment attachment : e.getMessage().getAttachments()) {
            if (attachment.isImage()) {
                pointsToGive += imageSent;
            } else if (attachment.isVideo()) {
                pointsToGive += videoSent;
            }
        }
        DPlayer dpLayer = DPlayer.getDPlayer(e.getAuthor());
        dpLayer.setPoints(dpLayer.getPoints() + pointsToGive);
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        DPlayer dpLayer = DPlayer.getDPlayer(e.getUser().getIdLong());
        dpLayer.setPoints(dpLayer.getPoints() + reaction);
    }

    @Getter
    public final static Map<Member, Long> voiceChannelCache = new HashMap<>();

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        if (e.getEntity().getUser().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        voiceChannelCache.put(e.getMember(), System.currentTimeMillis());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
        if (e.getEntity().getUser().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        voiceChannelCache.put(e.getMember(), System.currentTimeMillis());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        if (e.getEntity().getUser().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        voiceChannelCache.remove(e.getMember());
    }
}

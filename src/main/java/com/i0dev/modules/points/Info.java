package com.i0dev.modules.points;

import com.i0dev.Bot;
import com.i0dev.config.BasicCommandsConfig;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Info extends SuperDiscordCommand {


    @CommandData(commandID = "info", messageLength = 1, identifier = "Points Information", parentClass = PointsManager.class)
    public static void run(CommandEvent e) {
        StringBuilder obtain = new StringBuilder();
        obtain.append("Sending a message: `{points}` points for each character.\n".replace("{points}", PointsManager.getOption("messageCharacterModifier", PointsManager.class).getAsDouble() + ""));
        obtain.append("Sending an image: `{points}` points.\n".replace("{points}", PointsManager.getOption("imageSent", PointsManager.class).getAsDouble() + ""));
        obtain.append("Sending a video: `{points}` points.\n".replace("{points}", PointsManager.getOption("videoSent", PointsManager.class).getAsDouble() + ""));
        obtain.append("Reacting to a message: `{points}` points.\n".replace("{points}", PointsManager.getOption("reaction", PointsManager.class).getAsDouble() + ""));
        obtain.append("Being in a voice channel: `{points}` points for every `{time}` seconds.\n".replace("{time}", PointsManager.getOption("voiceChannelSeconds", PointsManager.class).getAsDouble() + "").replace("{points}", PointsManager.getOption("voiceChannelXSecondsPoints", PointsManager.class).getAsDouble() + ""));
        obtain.append("Inviting a user to the discord: `{points}` points.\n".replace("{points}", PointsManager.getOption("inviteUser", PointsManager.class).getAsDouble() + ""));
        obtain.append("Invited user leaving the discord: `-{points}` points.\n".replace("{points}", PointsManager.getOption("inviteUser", PointsManager.class).getAsDouble() + ""));
        StringBuilder boosting = new StringBuilder();
        if (PointsManager.getOption("prevBoostsEffectBoostPoints", PointsManager.class).getAsBoolean()) {
            boosting.append("For every time you boost, you will get less points added for every future boost.");
            boosting.append("The starting amount is `{points}` points, and every boost after the first will be less than that.".replace("{points}", PointsManager.getOption("boost", PointsManager.class).getAsDouble() + ""));
        } else
            boosting.append("The boost amount is `{points}` points.".replace("{points}", PointsManager.getOption("boost", PointsManager.class).getAsDouble() + ""));

        StringBuilder rewards = new StringBuilder();
        rewards.append("If you have your account linked you will be able to use the command `{cmd}` to claim `{points}` points.".replace("{points}", BasicCommandsConfig.get().getCmd_rewards().getOptions().get("points").getAsString() + "").replace("{cmd}", GeneralConfig.get().getPrefixes().get(0) + BasicCommandsConfig.get().getCmd_rewards().getAliases().get(0)));

        // shop
        //  desc.append("To spend your points, you can spend them in the shop in game, with the command `/points shop`, or in discord with the command `.points shop`");

        e.reply(EmbedMaker.builder()
                .authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl())
                .authorName("Points Information and Help")
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__How to obtain points__", obtain.toString(), false),
                        new MessageEmbed.Field("__Boosting__", boosting.toString(), false),
                        new MessageEmbed.Field("__Rewards__", rewards.toString(), false),
                })
                .build());
    }
}
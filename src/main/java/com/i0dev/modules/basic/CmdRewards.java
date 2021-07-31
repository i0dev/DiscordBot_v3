package com.i0dev.modules.basic;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.discordLinking.DPlayerFieldType;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.MessageUtil;
import com.i0dev.utility.TimeUtil;
import com.i0dev.utility.Utility;

public class CmdRewards extends DiscordCommand {

    public static void load() {
        addOption("cooldownMillis", 86400000L);
        addOption("points", 125L);
        addMessage("wait", "You have to wait {time} before you can claim your daily reward again.");
        addMessage("claimed", "You have claimed your daily reward of: `{points}` points!");
    }

    @CommandData(commandID = "cmd_rewards", identifier = "Rewards", messageLength = 1)
    public static void run(CommandEvent e) {

        long cooldown = getOption("cooldownMillis").getAsLong();
        long points = getOption("points").getAsLong();

        if (e.getDPlayer().getLastRewardsClaim() != 0 && System.currentTimeMillis() < (e.getDPlayer().getLastRewardsClaim() + cooldown)) {
            long timePeriod = e.getDPlayer().getLastRewardsClaim() + getOption("cooldownMillis").getAsLong() - System.currentTimeMillis();
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content(getMessage("wait").replace("{time}", TimeUtil.formatTime(timePeriod))).build());
            return;
        }
        e.getDPlayer().setLastRewardsClaim(System.currentTimeMillis());
        e.getDPlayer().increase(DPlayerFieldType.POINTS, points);
        e.getDPlayer().increase(DPlayerFieldType.CLAIMED_REWARDS);
        e.reply(EmbedMaker.builder().content(getMessage("claimed").replace("{points}", points + "")).embedColor(EmbedColor.SUCCESS).build());

    }
}
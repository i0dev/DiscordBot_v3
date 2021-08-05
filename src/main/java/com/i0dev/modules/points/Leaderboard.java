package com.i0dev.modules.points;

import com.i0dev.Bot;
import com.i0dev.modules.invite.InviteManager;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.SQLUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class Leaderboard extends SuperDiscordCommand {

    public static void load() {
        addOption("limit", 30);
    }

    @CommandData(commandID = "leaderboard", identifier = "Points Leaderboard", messageLength = 1, parentClass = PointsManager.class)
    public static void run(CommandEvent e) {
        StringBuilder msg = new StringBuilder();

        List<Object> list = SQLUtil.getSortedList(DiscordPoints.class.getSimpleName(), "points", DiscordPoints.class, getOption("limit").getAsInt(), "discordID");
        int count = 0;
        for (Object o : list) {
            DiscordPoints dPoints = (DiscordPoints) o;
            if (dPoints.getPoints() == 0) continue;
            User user = Bot.getJda().retrieveUserById(dPoints.getDiscordID()).complete();
            msg.append("**#").append(count + 1).append("**. *").append(user.getAsTag()).append("*: `").append(Utility.numberFormat.format(dPoints.getPoints())).append(" points`\n");
            count++;
        }

        if (count == 0) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("There are currently not any users with points.").build());
            return;
        }

        e.reply(EmbedMaker.builder().field(new MessageEmbed.Field("Points Leaderboard:", msg.toString(), true)).build());
    }


}

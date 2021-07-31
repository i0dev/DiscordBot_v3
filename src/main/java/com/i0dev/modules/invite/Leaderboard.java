package com.i0dev.modules.invite;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.SQLUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class Leaderboard extends SuperDiscordCommand {

    public static void load() {
        addOption("limit", 30);
    }

    @CommandData(commandID = "leaderboard", identifier = "Invite Leaderboard", messageLength = 1, parentClass = InviteManager.class)
    public static void run(CommandEvent e) {
        StringBuilder msg = new StringBuilder();
        int count = 0;
        for (Object o : SQLUtil.getSortedList(DPlayer.class.getSimpleName(), "invites", DPlayer.class, getOption("limit").getAsInt(), "discordID")) {
            DPlayer dPlayer = (DPlayer) o;
            if (dPlayer.getInvites() == 0) continue;
            User user = Bot.getJda().retrieveUserById(dPlayer.getDiscordID()).complete();
            msg.append("**#").append(count + 1).append("**. *").append(user.getAsTag()).append("*: `").append(dPlayer.getInvites()).append(" invites`\n");
            count++;
        }
        e.reply(EmbedMaker.builder().field(new MessageEmbed.Field("Invites Leaderboard:", msg.toString(), true)).build());
    }
}

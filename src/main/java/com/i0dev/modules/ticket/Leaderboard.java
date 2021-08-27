package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.managers.SQLManager;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class Leaderboard extends SuperDiscordCommand {

    public static void load() {
        addOption("limit", 30);
    }

    @CommandData(commandID = "leaderboard", identifier = "TicketTop Leaderboard", messageLength = 1, parentClass = TicketManager.class)
    public static void run(CommandEvent e) {
        StringBuilder msg = new StringBuilder();

        List<Object> list = Bot.getBot().getManager(SQLManager.class).getSortedList(DPlayer.class.getSimpleName(), "ticketsClosed", DPlayer.class, getOption("limit").getAsInt(), "discordID");
        int count = 0;
        for (Object o : list) {
            DPlayer dPlayer = (DPlayer) o;
            if (dPlayer.getTicketsClosed() == 0) continue;
            User user = Bot.getBot().getJda().retrieveUserById(dPlayer.getDiscordID()).complete();
            msg.append("**#").append(count + 1).append("**. *").append(user.getAsTag()).append("*: `").append(Utility.numberFormat.format(dPlayer.getTicketsClosed())).append(" tickets closed`\n");
            count++;
        }

        if (count == 0) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("There are currently not any users with tickets closed.").build());
            return;
        }

        e.reply(EmbedMaker.builder().field(new MessageEmbed.Field("Tickets Closed Leaderboard:", msg.toString(), true)).build());
    }


}

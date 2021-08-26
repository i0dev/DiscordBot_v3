package com.i0dev.modules.blacklist;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.managers.SQLManager;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class List extends SuperDiscordCommand {

    @CommandData(commandID = "list", identifier = "Blacklist List", messageLength = 1, parentClass = BlacklistManager.class)
    public static void run(CommandEvent e) {
        StringBuilder msg = new StringBuilder();

        java.util.List<Object> list = Bot.getBot().getManager(SQLManager.class).getListWhere(DPlayer.class.getSimpleName(), "blacklisted", "1", DPlayer.class, "discordID");
        if (list.isEmpty()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("There are currently not any blacklisted users.").build());
            return;
        }

        for (Object o : list) {
            DPlayer dPlayer = (DPlayer) o;
            User user = Bot.getBot().getJda().retrieveUserById(dPlayer.getDiscordID()).complete();
            msg.append(user.getAsTag()).append(" `(").append(user.getIdLong()).append(")`\n");
        }
        e.reply(EmbedMaker.builder().field(new MessageEmbed.Field("Blacklisted Users:", msg.toString(), true)).build());
    }
}

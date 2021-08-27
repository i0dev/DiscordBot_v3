package com.i0dev.modules.mute;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.managers.SQLManager;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class Retrieve extends SuperDiscordCommand {

    @CommandData(commandID = "list", parentClass = MuteManager.class, messageLength = 1, identifier = "Mute List")
    public static void run(CommandEvent e) {
        if (MuteManager.mutedRole == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Muted role is not yet setup. You can create one with {prefix}mute create").build());
            return;
        }

        List<Object> list = Bot.getBot().getManager(SQLManager.class).getListWhere(DPlayer.class.getSimpleName(), "muted", "1", DPlayer.class, "discordID");
        if (list.isEmpty()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("There are currently not any muted users.").build());
            return;
        }

        StringBuilder msg = new StringBuilder();
        list.forEach(ob -> {
            DPlayer dPlayer = ((DPlayer) ob);
            User user = Bot.getBot().getJda().retrieveUserById(dPlayer.getDiscordID()).complete();
            msg.append(user.getAsTag()).append(" `(").append(dPlayer.getDiscordID()).append(")`\n");
        });

        e.reply(EmbedMaker.builder().field(new MessageEmbed.Field("Muted Users:", msg.toString(), true)).build());
    }
}

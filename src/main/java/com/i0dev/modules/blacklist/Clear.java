package com.i0dev.modules.blacklist;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.managers.SQLManager;
import com.i0dev.utility.EmbedMaker;

public class Clear extends SuperDiscordCommand {

    @CommandData(commandID = "clear", identifier = "Blacklist Clear", messageLength = 1, parentClass = BlacklistManager.class)
    public static void run(CommandEvent e) {
        Bot.getBot().getManager(SQLManager.class).getListWhere(DPlayer.class.getSimpleName(), "blacklisted", "1", DPlayer.class, "discordID").forEach(o -> {
            DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(((DPlayer) o).getDiscordID());
            dPlayer.setBlacklisted(false);
            dPlayer.save();
        });
        System.gc();
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully cleared all blacklisted users.").build());
    }
}

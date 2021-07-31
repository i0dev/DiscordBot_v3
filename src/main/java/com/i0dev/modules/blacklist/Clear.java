package com.i0dev.modules.blacklist;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.SQLUtil;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class Clear extends SuperDiscordCommand {

    @CommandData(commandID = "clear", identifier = "Blacklist Clear", messageLength = 1, parentClass = BlacklistManager.class)
    public static void run(CommandEvent e) {
        DPlayer.loadAll();
        DPlayer.getCachedUsers().forEach(dPlayer -> {
            dPlayer.setBlacklisted(false);
            dPlayer.save();
        });
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully cleared all blacklisted users.").build());
    }
}

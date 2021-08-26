package com.i0dev.modules.invite;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.managers.SQLManager;
import com.i0dev.utility.EmbedMaker;

public class Clear extends SuperDiscordCommand {

    @CommandData(commandID = "clear", identifier = "Invite Reset", messageLength = 1, parentClass = InviteManager.class)
    public static void run(CommandEvent e) {

        Bot.getBot().getManager(SQLManager.class).getListWhereNot(DPlayer.class.getSimpleName(), "invites", "0", DPlayer.class, "discordID").forEach(o -> {
            DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(((DPlayer) o).getDiscordID());
            dPlayer.setInvites(0);
            dPlayer.setInvitedByDiscordID(0);
            dPlayer.save();
        });

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully cleared all invite data.").build());
    }
}

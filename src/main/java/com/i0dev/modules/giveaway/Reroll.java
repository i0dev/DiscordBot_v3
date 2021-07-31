package com.i0dev.modules.giveaway;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;

public class Reroll extends SuperDiscordCommand {

    @CommandData(commandID = "reroll", parentClass = GiveawayManager.class, identifier = "Giveaway Reroll", usage = "<message>", messageLength = 2)
    public static void run(CommandEvent e) {
        Giveaway giveaway = Giveaway.getGiveaway(e.getOffsetSplit().get(1));
        if (giveaway == null) {
            e.reply(EmbedMaker.builder().content("Could not find that giveaway.").embedColor(EmbedColor.FAILURE).build());
            return;
        }


        if (!giveaway.isEnded()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("That giveaway is currently active.").build());
            return;
        }
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully re-rolled that giveaway.").build());

        GiveawayHandler.endGiveawayFull(giveaway, true, true, true, e.getAuthor());

    }
}

package com.i0dev.modules.fun;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;

public class CmdDice extends DiscordCommand {

    @CommandData(commandID = "cmd_dice", messageLength = 1, identifier = "Dice")
    public static void run(CommandEvent e) {
        e.reply(EmbedMaker.builder().content("You rolled a die, it landed on the side: `{side}`".replace("{side}", Utility.randomNumber(6) + "")).build());
    }
}


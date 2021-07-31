package com.i0dev.modules.fun;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;

public class CmdCoinflip extends DiscordCommand {

    @CommandData(commandID = "cmd_coinFlip", identifier = "CoinFlip", messageLength = 1)
    public static void run(CommandEvent e) {
        e.reply(EmbedMaker.builder().content("You flipped a coin and you got: `{flip}``".replace("{flip}", Utility.randomNumber(2) == 1 ? "Heads" : "Tails")).build());
    }
}

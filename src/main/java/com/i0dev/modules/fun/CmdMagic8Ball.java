package com.i0dev.modules.fun;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.concurrent.ThreadLocalRandom;

public class CmdMagic8Ball extends DiscordCommand {

    @CommandData(identifier = "Magic8Ball", commandID = "cmd_magic8ball", minMessageLength = 2, usage = "<question>")
    public static void run(CommandEvent e) {
        String question = Utility.remainingArgFormatter(e.getSplit(), 1);
        String response = "ERROR";
        switch (Math.abs(ThreadLocalRandom.current().nextInt(20)) + 1) {
            case 1:
                response = "Ask again later.";
                break;
            case 2:
                response = "Better not tell you now.";
                break;
            case 3:
                response = "Cannot predict now.";
                break;
            case 4:
                response = " Concentrate and ask again";
                break;
            case 5:
                response = "Don't count on it.";
                break;
            case 6:
                response = "It is certain.";
                break;
            case 7:
                response = "It is decidedly so.";
                break;
            case 8:
                response = "Most likely.";
                break;
            case 9:
                response = "My reply is no.";
                break;
            case 10:
                response = "My sources say no.";
                break;
            case 11:
                response = "Outlook not so good.";
                break;
            case 12:
                response = "Outlook good.";
                break;
            case 13:
                response = "Reply hazy, try again.";
                break;
            case 14:
                response = "Signs point to yes.";
                break;
            case 15:
                response = "Very doubtful.";
                break;
            case 16:
                response = "Without a doubt.";
                break;
            case 17:
                response = "Yes.";
                break;
            case 18:
                response = "Yes - definitely.";
                break;
            case 19:
                response = "You may rely on it.";
                break;
            case 20:
                response = "As I see it, yes.";
                break;
        }
        e.reply(EmbedMaker.builder().field(new MessageEmbed.Field("Question: *{q}*".replace("{q}", question), "\nMy reply is: `{a}`".replace("{a}", response), true)).build());
    }
}

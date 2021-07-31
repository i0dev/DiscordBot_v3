package com.i0dev.modules.moderation;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;

public class CmdVerifyPanel extends DiscordCommand {

    public static void load() {
        addOption("title", "Secure Verification");
        addOption("content", "To ensure a safe and mutually beneficial experience, all users are required to verify themselves as an actual human. It is your responsibility as a client to read all the rules. Once you agree to them you will be bound by them for as long as you are on this server. When you are done reading them, select the reaction at the bottom to acknowledge and agree to these terms, which will grant you access to the rest of the server.");
        addOption("pin", true);
        addOption("buttonLabel", "Click to verify");
        addOption("buttonEmoji", "U+2705");
    }

    @CommandData(commandID = "cmd_verifyPanel", identifier = "Verify Panel", messageLength = 1)
    public static void run(CommandEvent e) {

        Message msg = e.getChannel().sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().content(getOption("content").getAsString()).authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl()).authorName(getOption("title").getAsString()).build()))
                .setActionRow(Button.success("BUTTON_VERIFY_PANEL_2", getOption("buttonLabel").getAsString()).withEmoji(Emoji.fromMarkdown(getOption("buttonEmoji").getAsString())))
                .complete();
        if (getOption("pin").getAsBoolean()) msg.pin().queue();
    }
}

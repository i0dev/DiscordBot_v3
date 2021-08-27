package com.i0dev.modules.suggestion;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.managers.SQLManager;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;

public class Clear extends SuperDiscordCommand {

    @CommandData(commandID = "clear", parentClass = SuggestionManager.class, messageLength = 1, identifier = "Suggestion Clear")
    public static void run(CommandEvent e) {
        Bot.getBot().getManager(SQLManager.class).clearTable(Suggestion.class.getSimpleName());

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have cleared all suggestion data.").build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} has cleared all suggestion data.").author(e.getAuthor()).build());
    }
}

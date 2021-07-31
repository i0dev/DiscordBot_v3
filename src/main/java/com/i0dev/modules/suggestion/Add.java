package com.i0dev.modules.suggestion;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.EmojiUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Message;

public class Add extends SuperDiscordCommand {


    @CommandData(commandID = "add", identifier = "Suggestion Add", usage = "<content>", minMessageLength = 2, parentClass = SuggestionManager.class)
    public static void run(CommandEvent e) {
        String suggestion = Utility.remainingArgFormatter(e.getOffsetSplit(), 1);

        Message msg = SuggestionManager.sendPending(EmbedMaker.builder()
                .authorName("Suggestion from: {tag}")
                .content("`" + suggestion + "`")
                .authorImg(e.getAuthor().getEffectiveAvatarUrl())
                .author(e.getAuthor())
                .user(e.getAuthor()).build());

        msg.addReaction(EmojiUtil.getEmojiWithoutArrow(SuggestionManager.getOption("upvoteEmoji", SuggestionManager.class).getAsString())).queue();
        msg.addReaction(EmojiUtil.getEmojiWithoutArrow(SuggestionManager.getOption("downvoteEmoji", SuggestionManager.class).getAsString())).queue();

        Suggestion sugg = new Suggestion();
        sugg.setSuggestion(suggestion);
        sugg.setUserID(e.getAuthor().getIdLong());
        sugg.setMessageID(msg.getIdLong());
        sugg.setChannelID(SuggestionManager.pending.getIdLong());
        sugg.save();

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully submitted a suggestion.").build());

    }
}

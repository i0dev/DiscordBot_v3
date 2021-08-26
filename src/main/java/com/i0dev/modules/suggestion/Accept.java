package com.i0dev.modules.suggestion;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class Accept extends SuperDiscordCommand {

    @CommandData(commandID = "accept", parentClass = SuggestionManager.class, usage = "<messageID> [reason]", minMessageLength = 3, identifier = "Suggestion Accept")
    public static void run(CommandEvent e) {
        String note = Utility.remainingArgFormatter(e.getOffsetSplit(), 2);
        Long messageID;
        if ((messageID = FindUtil.getLong(e.getOffsetSplit().get(1), e.getMessage())) == null) return;

        Suggestion suggestion = (Suggestion) SQLUtil.getObject("messageID", messageID + "", Suggestion.class);
        if (suggestion == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Could not find that suggestion.").build());
            return;
        }
        if (suggestion.isAccepted()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("That suggestion has already been accepted").build());
            return;
        }
        if (suggestion.isRejected()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("That suggestion has already been rejected").build());
            return;
        }
        TextChannel channel = Bot.getBot().getJda().getTextChannelById(suggestion.getChannelID());
        if (channel == null || channel.retrieveMessageById(messageID).complete() == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Could not find that suggestion.").build());
            return;
        }
        channel.retrieveMessageById(messageID).complete().delete().queue();
        suggestion.setAccepted(true);
        suggestion.save();

        User user = Bot.getBot().getJda().retrieveUserById(suggestion.getUserID()).complete();
        SuggestionManager.sendAccepted(EmbedMaker.builder()
                .author(e.getAuthor())
                .user(user)
                .embedColor(EmbedColor.SUCCESS)
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__Suggestion__", suggestion.getSuggestion(), false),
                        new MessageEmbed.Field("__Note from {authorTag}__", note, false)
                })
                .authorName("Accepted Suggestion from: {tag}")
                .authorImg(user.getEffectiveAvatarUrl())
                .build());


        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully accepted that suggestion").build());

    }


    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (SuggestionManager.pending == null) return;

        if (e.getChannel().getIdLong() != SuggestionManager.pending.getIdLong()) return;
        String Emoji = EmojiUtil.getSimpleEmoji(SuggestionManager.getOption("acceptEmoji", SuggestionManager.class).getAsString());

        if (e.getReactionEmote().isEmoji()) {
            if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji))
                return;
        } else {
            if (!e.getReactionEmote().getName().equalsIgnoreCase(SuggestionManager.getOption("acceptEmoji", SuggestionManager.class).getAsString()))
                return;
        }

        Message message = e.retrieveMessage().complete();
        Suggestion suggestion = (Suggestion) SQLUtil.getObject("messageID", e.getMessageId(), Suggestion.class);
        if (suggestion == null) return;
        message.delete().queue();
        suggestion.setAccepted(true);
        suggestion.save();

        String note = "Nothing Provided";

        User user = Bot.getBot().getJda().retrieveUserById(suggestion.getUserID()).complete();
        SuggestionManager.sendAccepted(EmbedMaker.builder()
                .author(e.getUser())
                .user(user)
                .embedColor(EmbedColor.SUCCESS)
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__Suggestion__", suggestion.getSuggestion(), false),
                        new MessageEmbed.Field("__Note from {authorTag}__", note, false)
                })
                .authorName("Accepted Suggestion from: {tag}")
                .authorImg(user.getEffectiveAvatarUrl())
                .build());

    }

}
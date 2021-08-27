package com.i0dev.modules.suggestion;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.managers.SQLManager;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class Deny extends SuperDiscordCommand {

    @CommandData(commandID = "deny", parentClass = SuggestionManager.class, usage = "<messageID> [reason]", minMessageLength = 3, identifier = "Suggestion Deny")
    public static void run(CommandEvent e) {
        String note = Utility.remainingArgFormatter(e.getOffsetSplit(), 2);
        Long messageID;
        if ((messageID = FindUtil.getLong(e.getOffsetSplit().get(1), e.getMessage())) == null) return;

        Suggestion suggestion = (Suggestion) Bot.getBot().getManager(SQLManager.class).getObject("messageID", messageID + "", Suggestion.class);
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
        suggestion.setRejected(true);
        suggestion.save();

        User user = Bot.getBot().getJda().retrieveUserById(suggestion.getUserID()).complete();
        SuggestionManager.sendDenied(EmbedMaker.builder()
                .author(e.getAuthor())
                .user(user)
                .embedColor(EmbedColor.FAILURE)
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__Suggestion__", suggestion.getSuggestion(), false),
                        new MessageEmbed.Field("__Note from {authorTag}__", note, false)
                })
                .authorName("Denied Suggestion from: {tag}")
                .authorImg(user.getEffectiveAvatarUrl())
                .build());


        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully denied that suggestion").build());

    }


    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (SuggestionManager.pending == null) return;
        if (e.getChannel().getIdLong() != SuggestionManager.pending.getIdLong()) return;
        String Emoji = EmojiUtil.getSimpleEmoji(SuggestionManager.getOption("denyEmoji", SuggestionManager.class).getAsString());

        if (e.getReactionEmote().isEmoji()) {
            if (!EmojiUtil.getUnicodeFromCodepoints(e.getReactionEmote().getAsCodepoints()).equalsIgnoreCase(Emoji))
                return;
        } else {
            if (!e.getReactionEmote().getName().equalsIgnoreCase(SuggestionManager.getOption("denyEmoji", SuggestionManager.class).getAsString()))
                return;
        }

        Message message = e.retrieveMessage().complete();
        Suggestion suggestion = (Suggestion) Bot.getBot().getManager(SQLManager.class).getObject("messageID", e.getMessageId(), Suggestion.class);
        if (suggestion == null) return;
        message.delete().queue();
        suggestion.setRejected(true);
        suggestion.save();

        String note = "Nothing Provided";

        User user = Bot.getBot().getJda().retrieveUserById(suggestion.getUserID()).complete();
        SuggestionManager.sendDenied(EmbedMaker.builder()
                .author(e.getUser())
                .user(user)
                .embedColor(EmbedColor.FAILURE)
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__Suggestion__", suggestion.getSuggestion(), false),
                        new MessageEmbed.Field("__Note from {authorTag}__", note, false)
                })
                .authorName("Denied Suggestion from: {tag}")
                .authorImg(user.getEffectiveAvatarUrl())
                .build());

    }

}
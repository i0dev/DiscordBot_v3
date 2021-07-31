package com.i0dev.utility;

import com.i0dev.Bot;
import net.dv8tion.jda.api.entities.MessageReaction;

public class EmojiUtil {


    public static boolean isEmojiValid(MessageReaction.ReactionEmote reactionEmote, String emote) {
        if (reactionEmote.isEmoji()) {
            return EmojiUtil.getUnicodeFromCodepoints(reactionEmote.getAsCodepoints()).equalsIgnoreCase(emote);
        }
        return reactionEmote.getEmote().getAsMention().equalsIgnoreCase(emote);
    }


    public static String getSimpleEmoji(String Emoji) {
        if (Emoji.length() < 20) {
            return MessageReaction.ReactionEmote.fromUnicode(Emoji, Bot.getJda()).getEmoji();
        } else {
            return Emoji.substring(2, Emoji.length() - 20);
        }
    }

    public static String getUnicodeFromCodepoints(String s) {
        return "U" + s.split("U")[1];
    }

    public static String getEmojiWithoutArrow(String Emoji) {
        if (Emoji.length() < 15) {
            return MessageReaction.ReactionEmote.fromUnicode(Emoji, Bot.getJda()).getEmoji();
        } else {
            return Emoji.substring(0, Emoji.length() - 1);
        }
    }

}

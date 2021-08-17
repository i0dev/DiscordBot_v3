package com.i0dev.object;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommandEvent {

    public User author;
    public long authorIdLong;
    public String authorId;
    public Message message;
    public MessageChannel channel;
    public Guild guild;
    public Member member;
    public DPlayer dPlayer;
    public JDA jda;
    public String messageId;
    public long messageIdLong;
    public String[] split;
    public List<String> offsetSplit;

    public void reply(EmbedMaker builder) {
        replyStatic(builder, getMessage());
    }

    public Message replyComplete(EmbedMaker builder) {
        return replyStaticComplete(builder, getMessage());
    }

    public static void replyStatic(EmbedMaker maker, Message message) {
        message.replyEmbeds(EmbedMaker.create(maker)).mentionRepliedUser(false).queue();
    }

    public static Message replyStaticComplete(EmbedMaker maker, Message message) {
        return message.replyEmbeds(EmbedMaker.create(maker)).mentionRepliedUser(false).complete();
    }

}

package com.i0dev.utility;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.*;

public class FindUtil {

    public static Integer getInteger(String arg, Message message) {
        if (Utility.getInt(arg) != null)
            return Utility.getInt(arg);
        CommandEvent.replyStatic(EmbedMaker.builder().content(GeneralConfig.get().message_invalidNumber).embedColor(EmbedColor.FAILURE).author(message.getAuthor()).build(), message);
        return null;
    }

    public static Long getLong(String arg, Message message) {
        if (Utility.getLong(arg) != null)
            return Utility.getLong(arg);
        CommandEvent.replyStatic(EmbedMaker.builder().content(GeneralConfig.get().message_invalidNumber).embedColor(EmbedColor.FAILURE).author(message.getAuthor()).build(), message);
        return null;
    }

    public static User getUser(String arg, Message message) {
        try {
            return message.getMentionedUsers().get(0);
        } catch (Exception ignored) {
        }
        try {
            Long.parseLong(arg);
            User user = Bot.getJda().getUserById(Long.parseLong(arg));
            if (user == null) {
                Long.parseLong("fail");
            }
            return user;
        } catch (Exception ignored) {
        }
        try {
            User user = Bot.getJda().getUserByTag(arg);
            if (user == null) {
                Long.parseLong("fail");
            }
            return user;
        } catch (Exception ignored) {
        }
        CommandEvent.replyStatic(EmbedMaker.builder().content(GeneralConfig.get().message_invalidUser).embedColor(EmbedColor.FAILURE).author(message.getAuthor()).build(), message);
        return null;
    }

    public static Member getMember(String arg, Message message) {
        try {
            return message.getMentionedMembers().get(0);
        } catch (Exception ignored) {
        }
        try {
            Long.parseLong(arg);
            Member member = message.getGuild().getMemberById(Long.parseLong(arg));
            if (member == null) {
                Long.parseLong("fail");
            }
            return member;
        } catch (Exception ignored) {
        }
        try {
            Member member = message.getGuild().getMemberByTag(arg);
            if (member == null) {
                Long.parseLong("fail");
            }
            return member;
        } catch (Exception ignored) {
        }
        CommandEvent.replyStatic(EmbedMaker.builder().content(GeneralConfig.get().message_invalidUser).embedColor(EmbedColor.FAILURE).author(message.getAuthor()).build(), message);
        return null;
    }

    public static User retrieveUser(String arg, Message message) {
        try {
            return message.getMentionedUsers().get(0);
        } catch (Exception ignored) {
        }
        try {
            Long.parseLong(arg);
            return Bot.getJda().retrieveUserById(Long.parseLong(arg)).complete();
        } catch (Exception ignored) {
        }
        try {
            return Bot.getJda().getUserByTag(arg);

        } catch (Exception ignored) {
        }
        CommandEvent.replyStatic(EmbedMaker.builder().content(GeneralConfig.get().message_invalidUser).embedColor(EmbedColor.FAILURE).author(message.getAuthor()).build(), message);
        return null;
    }

    public static Role getRole(String arg, Message message) {
        try {
            return message.getMentionedRoles().get(0);
        } catch (Exception ignored) {
        }
        try {
            Long.parseLong(arg);
            return Bot.getJda().getRoleById(Long.parseLong(arg));
        } catch (Exception ignored) {
        }
        try {
            return message.getGuild().getRolesByName(arg, false).get(0);
        } catch (Exception ignored) {
        }
        try {
            return message.getGuild().getRolesByName(arg, true).get(0);
        } catch (Exception ignored) {
        }
        CommandEvent.replyStatic(EmbedMaker.builder().content(GeneralConfig.get().message_invalidRole).embedColor(EmbedColor.FAILURE).author(message.getAuthor()).build(), message);

        return null;
    }

    public static TextChannel getTextChannel(String arg, Message message) {
        try {
            return message.getMentionedChannels().get(0);
        } catch (Exception ignored) {
        }
        try {
            Long.parseLong(arg);
            return Bot.getJda().getTextChannelById(Long.parseLong(arg));
        } catch (Exception ignored) {
        }
        try {
            return message.getGuild().getTextChannelsByName(arg, false).get(0);
        } catch (Exception ignored) {
        }
        try {
            return message.getGuild().getTextChannelsByName(arg, true).get(0);
        } catch (Exception ignored) {
        }
        CommandEvent.replyStatic(EmbedMaker.builder().content(GeneralConfig.get().message_invalidChannel).embedColor(EmbedColor.FAILURE).author(message.getAuthor()).build(), message);

        return null;
    }

    public static Message getMessage(String arg, TextChannel channel) {

        try {
            Long.parseLong(arg);
        } catch (Exception exception) {
            return null;
        }

        return channel.retrieveMessageById(arg).complete();
    }

    public static Message getMessage(String arg, long channelID) {

        try {
            Long.parseLong(arg);
            return Bot.getJda().getTextChannelById(channelID).retrieveMessageById(arg).complete();
        } catch (Exception exception) {
            return null;
        }

    }
}

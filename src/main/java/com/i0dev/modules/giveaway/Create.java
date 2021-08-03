package com.i0dev.modules.giveaway;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.HashSet;
import java.util.Set;

public class Create extends SuperDiscordCommand {

    public static void load() {
        addOption("emoji", "U+1F389");
    }

    @Getter
    public static Set<GiveawayResponse> userList = new HashSet<>();


    @CommandData(commandID = "create", identifier = "Giveaway Create", messageLength = 1, parentClass = GiveawayManager.class)
    public static void run(CommandEvent e) {

        if (contains(e.getAuthor())) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("You already have an active giveaway creator!").build());
            return;
        }

        e.reply(EmbedMaker.builder().content("Please check your Direct Messages to create the giveaway.").embedColor(EmbedColor.SUCCESS).build());

        if (MessageUtil.sendPrivateMessageComplete(e.getMessage(), e.getAuthor(), EmbedMaker.builder()
                .content("You have started a giveaway creator.\nType `{prefix}cancel` at any time to cancel this creator.")
                .embedColor(EmbedColor.SUCCESS).build()) == null)
            return;

        MessageUtil.sendPrivateMessageComplete(e.getMessage(), e.getAuthor(), EmbedMaker.builder()
                .content("Please enter the channel you want the giveaway to be posted in.")
                .build());

        getUserList().add(new GiveawayResponse(e.getAuthor(), 1));
    }


    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (!contains(e.getAuthor())) return;
        String[] split = e.getMessage().getContentRaw().split(" ");
        GiveawayResponse response = get(e.getAuthor());

        if (e.getMessage().getContentRaw().equalsIgnoreCase(GeneralConfig.get().getPrefixes().get(0) + "cancel")) {
            reply(e.getMessage(), EmbedMaker.builder().content("You have successfully canceled your Giveaway Creator.").embedColor(EmbedColor.SUCCESS).build());
            userList.remove(response);
            return;
        }

        int question = response.getQuestionNumber();
        switch (question) {
            case 1: {
                TextChannel channel;
                channel = FindUtil.getTextChannel(split[0], e.getMessage());
                if (channel == null)
                    return;
                response.setChannel(channel);
                msg(e.getMessage(), EmbedMaker.builder().content("What is the prize of this giveaway?").build());
            }
            break;
            case 2: {
                response.setPrize(e.getMessage().getContentRaw());
                msg(e.getMessage(), EmbedMaker.builder().content("How many winners will there be in this giveaway?").build());
            }
            break;
            case 3: {
                Integer winners;
                winners = FindUtil.getInteger(split[0], e.getMessage());
                if (winners == null)
                    return;
                response.setWinners(winners);
                msg(e.getMessage(), EmbedMaker.builder().content("How long do you want the giveaway to last. Enter in this format: `1w4m`, `1d4m2s` etc.").build());
            }
            break;
            case 4: {
                long length;
                if ((length = Utility.getTimeMilis(e.getMessage().getContentRaw())) == -1) {
                    reply(e.getMessage(), EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Invalid time format. Enter in this format: `1w4m`, `1d4m2s` etc.").build());
                    return;
                }
                response.setLength(length);
                msg(e.getMessage(), EmbedMaker.builder().content("Type `{prefix}submit` to finish your giveaway creator.").build());
            }
            break;
            case 5: {
                if (e.getMessage().getContentRaw().equalsIgnoreCase(GeneralConfig.get().getPrefixes().get(0) + "submit")) {

                    // new giveaway!

                    StringBuilder content = new StringBuilder();
                    content.append("Prize: `").append(response.prize).append("`\n");
                    content.append("Host: `").append(e.getAuthor().getAsTag()).append("`\n");
                    content.append("Winners: `").append(response.getWinners()).append("`\n");
                    content.append("Time Remaining: ").append(TimeUtil.formatTime(response.length)).append("\n");
                    content.append("\nReact with {emoji} to enter.".replace("{emoji}", Emoji.fromMarkdown(getOption("emoji").getAsString()).getAsMention()));

                    EmbedMaker embed = EmbedMaker.builder()
                            .authorName("New Giveaway!")
                            .content(content.toString())
                            .authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl())
                            .build();

                    Message created = response.channel.sendMessageEmbeds(EmbedMaker.create(embed)).complete();
                    created.addReaction(getOption("emoji").getAsString()).queue();

                    Giveaway giveaway = new Giveaway();
                    giveaway.setPrize(response.getPrize());
                    giveaway.setMessageID(created.getIdLong());
                    giveaway.setHostID(e.getAuthor().getIdLong());
                    giveaway.setChannelID(response.getChannel().getIdLong());
                    giveaway.setEndTime(response.getLength() + System.currentTimeMillis());
                    giveaway.setWinnerAmount(response.getWinners());
                    giveaway.setEnded(false);
                    giveaway.save();

                    //

                    reply(e.getMessage(), EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully created a giveaway").build());
                    userList.remove(response);
                    return;
                }
            }
            break;
        }

        response.setQuestionNumber(++question);
    }

    public static void reply(Message msg, EmbedMaker maker) {
        msg.replyEmbeds(EmbedMaker.create(maker)).mentionRepliedUser(false).queue();
    }

    public static void msg(Message msg, EmbedMaker maker) {
        msg.getChannel().sendMessageEmbeds(EmbedMaker.create(maker)).queue();
    }

    public static boolean contains(ISnowflake idAble) {
        return userList.stream().anyMatch(giveawayResponse -> giveawayResponse.getUserID() == idAble.getIdLong());
    }

    public static GiveawayResponse get(ISnowflake idAble) {
        return userList.stream().filter(giveawayResponse -> giveawayResponse.getUserID() == idAble.getIdLong()).findAny().orElseGet(null);
    }

}

@Getter
@ToString
@Setter
class GiveawayResponse {

    int questionNumber;
    long userID;

    TextChannel channel;
    String prize;
    long winners, length;

    public GiveawayResponse(ISnowflake idAble, int questionNumber) {
        this.questionNumber = questionNumber;
        this.userID = idAble.getIdLong();
    }

}
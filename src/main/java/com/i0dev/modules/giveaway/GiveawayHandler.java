package com.i0dev.modules.giveaway;

import com.i0dev.Bot;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.MessageUtil;
import com.i0dev.utility.Utility;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GiveawayHandler {


    @SneakyThrows
    public static void endGiveawayFull(Giveaway giveaway, boolean byPassTime, boolean byPassEnding, boolean reroll, User reroller) {
        long endTime = giveaway.getEndTime();
        if (System.currentTimeMillis() > endTime || byPassTime) {
            if (!byPassEnding) if (giveaway.isEnded()) return;
            String prize = giveaway.getPrize();
            long winnerCount = giveaway.getWinnerAmount();
            TextChannel channel = Bot.getJda().getTextChannelById(giveaway.getChannelID());
            if (channel == null) return;
            User Host = Bot.getJda().retrieveUserById(giveaway.getHostID()).complete();
            Message Message;
            try {
                Message = channel.retrieveMessageById(giveaway.messageID).complete();
            } catch (Exception ignored) {
                return;
            }
            ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.of("America/New_York"));
            List<User> UsersReacted = new ArrayList<>();
            Message.getReactions().get(0).retrieveUsers().forEach(UsersReacted::add);

            List<User> selectedWinners = new ArrayList<>();
            UsersReacted.removeIf(User::isBot);

            for (int i = 0; i < winnerCount; i++) {
                selectedWinners.add(UsersReacted.get(ThreadLocalRandom.current().nextInt(UsersReacted.size())));
            }
            String winnersFormatted = Utility.FormatDoubleListUser(selectedWinners);


            StringBuilder info = new StringBuilder();
            info.append("Prize: `").append(giveaway.prize).append("`\n");
            info.append("Host: `").append(Host.getAsTag()).append("`\n");
            info.append("Entries: `").append(UsersReacted.size()).append("`\n");
            info.append("Winner(s): ").append(winnersFormatted).append("\n");

            EmbedMaker edit = EmbedMaker.builder()
                    .authorName("Giveaway Ended!")
                    .timestamp(time)
                    .footer("End Time")
                    .authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl())
                    .field(new MessageEmbed.Field("__Information:__", info.toString(), false))
                    .build();

            channel.editMessageEmbedsById(giveaway.messageID, EmbedMaker.create(edit)).queue();

            EmbedMaker.EmbedMakerBuilder newEmbed = EmbedMaker.builder()
                    .user(Host)
                    .content(info.toString())
                    .authorURL("https://discordapp.com/channels/" + channel.getGuild().getId() + "/" + giveaway.channelID + "/" + giveaway.messageID)
                    .authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl());
            if (reroll) newEmbed.authorName("Giveaway Rerolled by {who}".replace("{who}", reroller.getAsTag()));
            else newEmbed.authorName("Giveaway Ended!");

            channel.sendMessageEmbeds(EmbedMaker.create(newEmbed.build())).queue();

            selectedWinners.forEach(user -> {
                MessageUtil.sendPrivateMessage(null, user, EmbedMaker.builder()
                        .authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl())
                        .authorName("You won a giveaway!")
                        .authorURL("https://discordapp.com/channels/" + channel.getGuild().getId() + "/" + giveaway.channelID + "/" + giveaway.messageID)
                        .content("You won: `{prize}`".replace("{prize}", prize))
                        .embedColor(EmbedColor.SUCCESS).build());
            });

            giveaway.setEnded(true);
            giveaway.save();
        }
    }
}

package com.i0dev.modules.gamemode.factions;

import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Confirm extends SuperDiscordCommand {

    public static void load() {
        addOption("channel", 0L);
    }

    @CommandData(commandID = "confirm", parentClass = FactionsManager.class, identifier = "Factions Confirm", usage = "<leader> <faction> <size>", messageLength = 4)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        String faction = e.getOffsetSplit().get(2);
        String size = e.getOffsetSplit().get(3);

        if (size.equalsIgnoreCase("-1"))
            size = "Unlimited";

        e.reply(EmbedMaker.builder().user(user)
                .embedColor(EmbedColor.SUCCESS).content("You have successfully confirmed the faction `{fac}` with the leader being: `{tag}`, and the roster size being: `{size}`"
                        .replace("{fac}", faction)
                        .replace("{size}", size)
                ).build());

        TextChannel channel = Bot.getJda().getTextChannelById(getOption("channel").getAsLong());
        if (channel == null) return;

        StringBuilder msg = new StringBuilder();
        msg.append("Faction: `").append(faction).append("`\n");
        msg.append("Leader: `{tag}`\n");
        msg.append("Size: `").append(size).append("`\n");


        channel.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder()
                .authorImg(user.getEffectiveAvatarUrl())
                .authorName("New Confirmed Faction!")
                .user(user)
                .content(msg.toString())
                .build())).queue();

    }
}

package com.i0dev.modules.gamemode.prison;

import com.i0dev.Bot;
import com.i0dev.modules.gamemode.factions.FactionsManager;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Confirm extends SuperDiscordCommand {

    public static void load() {
        addOption("channel", 0L);
    }

    @CommandData(commandID = "confirm", parentClass = PrisonManager.class, identifier = "Prison Confirm", usage = "<leader> <team> <size>", messageLength = 4)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        String team = e.getOffsetSplit().get(2);
        String size = e.getOffsetSplit().get(3);

        if (size.equalsIgnoreCase("-1"))
            size = "Unlimited";

        e.reply(EmbedMaker.builder().user(user)
                .embedColor(EmbedColor.SUCCESS).content("You have successfully confirmed the team `{team}` with the leader being: `{tag}`, and the roster size being: `{size}`"
                        .replace("{team}", team)
                        .replace("{size}", size)
                ).build());

        TextChannel channel = Bot.getBot().getJda().getTextChannelById(getOption("channel").getAsLong());
        if (channel == null) return;

        StringBuilder msg = new StringBuilder();
        msg.append("Team: `").append(team).append("`\n");
        msg.append("Leader: `{tag}`\n");
        msg.append("Size: `").append(size).append("`\n");


        channel.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder()
                .authorImg(user.getEffectiveAvatarUrl())
                .authorName("New Confirmed Team!")
                .user(user)
                .content(msg.toString())
                .build())).queue();

    }
}

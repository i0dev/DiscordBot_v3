package com.i0dev.modules.points;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerFieldType;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import net.dv8tion.jda.api.entities.User;

public class Remove extends SuperDiscordCommand {

    @CommandData(commandID = "remove", messageLength = 3, usage = "<user> <amount>", identifier = "Points Remove", parentClass = PointsManager.class)
    public static void run(CommandEvent e) {
        User user;
        Integer amt;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        if ((amt = FindUtil.getInteger(e.getOffsetSplit().get(2), e.getMessage())) == null) return;
        DPlayer dPlayer = DPlayer.getDPlayer(user.getIdLong());
        dPlayer.decrease(DPlayerFieldType.POINTS, amt);

        e.reply(EmbedMaker.builder().content("You have removed {amt} points from {tag}".replace("{amt}", amt + "")).user(user).embedColor(EmbedColor.SUCCESS).build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} removed {amt} points from {tag}".replace("{amt}", amt + "")).author(e.getAuthor()).user(user).build());
    }

}

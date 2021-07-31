package com.i0dev.modules.points;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerFieldType;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.User;

public class Pay extends SuperDiscordCommand {

    @CommandData(commandID = "pay", parentClass = PointsManager.class, identifier = "Points Pay", usage = "<user> <amount>", messageLength = 3)
    public static void run(CommandEvent e) {
        User user;
        Integer amt;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        if ((amt = FindUtil.getInteger(e.getOffsetSplit().get(2), e.getMessage())) == null) return;
        DPlayer dPlayer = DPlayer.getDPlayer(user);

        if (e.getDPlayer().getPoints() <= amt) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("You have an insufficient amount of points.\nYou only have: `{points}` points").user(e.getAuthor()).build());
            return;
        }

        dPlayer.increase(DPlayerFieldType.POINTS, amt);
        e.getDPlayer().decrease(DPlayerFieldType.POINTS, amt);

        e.reply(EmbedMaker.builder().user(e.getAuthor()).embedColor(EmbedColor.SUCCESS).content("You have paid {tag} `{amt}` points".replace("{amt}", amt + "")).build());
    }
}
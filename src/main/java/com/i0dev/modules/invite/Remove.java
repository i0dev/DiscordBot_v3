package com.i0dev.modules.invite;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerFieldType;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.User;

public class Remove extends SuperDiscordCommand {

    @CommandData(commandID = "remove", identifier = "Invite Remove", messageLength = 3, usage = "<user> <amount>", parentClass = InviteManager.class)
    public static void run(CommandEvent e) {
        User user;
        Integer amt;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        if ((amt = FindUtil.getInteger(e.getOffsetSplit().get(2), e.getMessage())) == null) return;
        DPlayer dPlayer = DPlayer.getDPlayer(user);
        dPlayer.decrease(DPlayerFieldType.INVITES, amt);

        e.reply(EmbedMaker.builder().user(user).content("You have removed {amt} invites from {tag}'s invite count.".replace("{amt}", amt + "")).embedColor(EmbedColor.SUCCESS).build());
    }
}

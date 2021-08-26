package com.i0dev.modules.linking;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.APIUtil;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import net.dv8tion.jda.api.entities.User;

public class Force extends SuperDiscordCommand {

    @CommandData(commandID = "force", usage = "<user> <ign>", identifier = "Link Force", messageLength = 3, parentClass = LinkManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(user);
        String ign = e.getOffsetSplit().get(2);
        if (APIUtil.getUUIDFromIGN(ign) == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("That ign does not exist!").build());
            return;
        }
        dPlayer.link("forced", ign, APIUtil.getUUIDFromIGN(ign).toString());
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You forced :link: `{tag}` to the ign `{ign}`").user(user).build());

        LogUtil.logDiscord(EmbedMaker.builder().user(user).content("{authorTag} forced linked `{tag}` to the ign `{ign}`").author(e.getAuthor()).build());
        RoleRefreshHandler.RefreshUserRank(dPlayer);

    }
}
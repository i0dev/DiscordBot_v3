package com.i0dev.modules.linking;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import net.dv8tion.jda.api.entities.User;

public class Remove extends SuperDiscordCommand {

    @CommandData(commandID = "remove", identifier = "Link remove", usage = "<user>", messageLength = 2, parentClass = LinkManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        DPlayer dPlayer = DPlayer.getDPlayer(user);
        if (!dPlayer.isLinked()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(user).content("{tag} is not linked to any account.").build());
            return;
        }

        String cachedIGN = dPlayer.getMinecraftIGN();
        dPlayer.setLinked(false);
        dPlayer.setLinkCode("");
        dPlayer.setMinecraftIGN("");
        dPlayer.setMinecraftUUID("");
        dPlayer.setLinkedTime(0);
        dPlayer.used().save();

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).content("You removed the :link: between `{tag}` and `{ign}`".replace("{ign}", cachedIGN)).build());

        LogUtil.logDiscord(EmbedMaker.builder().user(user).content("{authorTag} forced removed `{tag}` from the ign `{ign}`".replace("{ign}", cachedIGN)).author(e.getAuthor()).build());
    }
}
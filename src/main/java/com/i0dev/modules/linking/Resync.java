package com.i0dev.modules.linking;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.entities.User;

public class Resync extends SuperDiscordCommand {

    @CommandData(commandID = "resync", identifier = "Link Re-Sync", usage = "<user>", messageLength = 2, parentClass = LinkManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(user);
        if (!dPlayer.isLinked()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(user).content("{tag} is not linked to any account.").build());
            return;
        }

        RoleRefreshHandler.RefreshUserRank(dPlayer);

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).content("You have successfully refreshed {tag}'s link info").build());

        String ign = APIUtil.getIGNFromUUID(dPlayer.getMinecraftUUID());
        if (ign == null) return;
        dPlayer.setMinecraftIGN(ign);
        dPlayer.setLastUpdatedMillis(System.currentTimeMillis());
        dPlayer.save();
    }
}
package com.i0dev.modules.linking;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.User;

public class Info extends SuperDiscordCommand {


    @CommandData(commandID = "info", identifier = "Link Info", messageLength = 2, usage = "<user>", parentClass = LinkManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        DPlayer dPlayer = DPlayer.getDPlayer(user);

        if (!dPlayer.isLinked()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("{tag} is not linked to any account.").user(user).build());
            return;
        }
        StringBuilder msg = new StringBuilder();
        msg.append("Minecraft IGN: `").append("{ign}").append("`\n");
        msg.append("Minecraft UUID: `").append("{uuid}").append("`\n");
        msg.append("Linked Time: ").append("{linkTime}").append("\n");

        e.reply(EmbedMaker.builder().user(user)
                .content(msg.toString())
                .thumbnail("https://crafatar.com/renders/body/" + dPlayer.getMinecraftUUID())
                .authorName("Link information for {tag}").authorImg(user.getEffectiveAvatarUrl()).build());

    }
}
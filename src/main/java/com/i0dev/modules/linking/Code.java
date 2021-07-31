package com.i0dev.modules.linking;

import com.i0dev.object.*;
import com.i0dev.object.discordLinking.CodeCache;
import com.i0dev.object.discordLinking.From_IngameCodeLinker;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.MessageUtil;

public class Code extends SuperDiscordCommand {

    @CommandData(commandID = "code", messageLength = 2, identifier = "Link Code", usage = "<code>", requirePluginMode = true, parentClass = LinkManager.class)
    public static void run(CommandEvent e) {
        if (e.getDPlayer().isLinked()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("You are already linked to the ign: {ign}").user(e.getAuthor()).build());
            return;
        }
        String code = e.getOffsetSplit().get(1);

        From_IngameCodeLinker codeLinker = CodeCache.getInstance().getObjectIngame(code);
        if (codeLinker == null) {
            e.reply(EmbedMaker.builder().content("The code: `{code}` is not valid. Please try again.".replace("{code}", code)).embedColor(EmbedColor.FAILURE).build());
            return;
        }

        e.getDPlayer().link(code, codeLinker.getPlayer().getName(), codeLinker.getPlayer().getUniqueId().toString());
        MessageUtil.sendMessageInGame(codeLinker.getPlayer(), "&7You have linked yourself to the discord tag: &c{tag}".replace("{tag}", e.getAuthor().getAsTag()));

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have linked yourself to the ign: `{ign}`").user(e.getAuthor()).build());

        LogUtil.logDiscord(EmbedMaker.builder().content("{tag} is now linked to the ign: `{ign}`").user(e.getAuthor()).build());

        //   RoleRefreshHandler.RefreshUserRank(dPlayer);

    }
}
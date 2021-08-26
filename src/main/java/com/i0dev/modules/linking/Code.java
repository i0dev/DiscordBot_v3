package com.i0dev.modules.linking;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.modules.CommandManager;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.CodeCache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.From_IngameCodeLinker;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.MessageUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class Code extends SuperDiscordCommand {

    public void load() {
        addOption("deleteMessages", false);
    }

    @CommandData(commandID = "code", messageLength = 2, identifier = "Link Code", usage = "<code>", requirePluginMode = true, parentClass = LinkManager.class, canBePrivateMessage = true)
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
        boolean deleteMessages = getOption("deleteMessages").getAsBoolean();

        e.getDPlayer().link(code, codeLinker.getPlayer().getName(), codeLinker.getPlayer().getUniqueId().toString());
        MessageUtil.sendMessageInGame(codeLinker.getPlayer(), "&7You have linked yourself to the discord tag: &c{tag}".replace("{tag}", e.getAuthor().getAsTag()));

        Message msg = e.replyComplete(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have linked yourself to the ign: `{ign}`").user(e.getAuthor()).build());

        if (deleteMessages) {
            msg.delete().queueAfter(5, TimeUnit.SECONDS);
            e.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
        }

        LogUtil.logDiscord(EmbedMaker.builder().content("{tag} is now linked to the ign: `{ign}`").user(e.getAuthor()).build());
        CodeCache.getInstance().getFrom_Ingame_cache().remove(codeLinker);
        RoleRefreshHandler.RefreshUserRank(Bot.getBot().getDPlayerManager().getDPlayer(e.getAuthor()));
    }

}
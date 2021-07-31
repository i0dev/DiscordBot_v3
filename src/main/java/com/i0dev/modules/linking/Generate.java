package com.i0dev.modules.linking;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.object.discordLinking.CodeCache;
import com.i0dev.object.discordLinking.From_DiscordCodeLinker;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.MessageUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Generate extends SuperDiscordCommand {

    @CommandData(commandID = "generate", identifier = "Link Generate", messageLength = 1, requirePluginMode = true, parentClass = LinkManager.class)
    public static void run(CommandEvent e) {
        if (e.getDPlayer().isLinked()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("You are already linked to the ign: {ign}").user(e.getAuthor()).build());
            return;
        }

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("Please look at your direct messages for the code.").build());

        String code = Utility.GenerateRandomString(5);

        From_DiscordCodeLinker from_discordCodeLinker = new From_DiscordCodeLinker(e.getAuthor(), code);
        CodeCache.getInstance().getFrom_Discord_cache().add(from_discordCodeLinker);

        MessageUtil.sendPrivateMessage(e.getMessage(), e.getAuthor(), EmbedMaker.builder().field(
                new MessageEmbed.Field("Your code is: `{code}`".replace("{code}", code), "Use the command: `/link code {code}` in game to finish linking!".replace("{code}", code), false)
        ).build());
    }
}
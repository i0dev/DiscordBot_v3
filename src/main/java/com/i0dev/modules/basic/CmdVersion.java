package com.i0dev.modules.basic;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdVersion extends DiscordCommand {

    private static EmbedMaker message;

    public static void load() {
        StringBuilder msg = new StringBuilder();
        msg.append("Prefixes: `").append(Utility.FormatListStringComma(GeneralConfig.get().getPrefixes())).append("`\n");
        msg.append("Bot Author: ").append("`{botAuthor}`").append("\n");
        msg.append("Bot Version: `").append("{version}").append("`\n");
        msg.append("Plugin Mode: `").append("{pluginMode}").append("`\n");

        message = EmbedMaker.builder()
                .authorName("DiscordBot Information")
                .authorURL("https://i0dev.com/")
                .authorImg(Bot.getBot().getJda().getSelfUser().getEffectiveAvatarUrl())
                .footer("Bot created by i0dev.com")
                .footerImg("https://cdn.discordapp.com/attachments/763790150550683708/780593953824964628/i01.png")
                .content(msg.toString())
                .build();
    }

    @CommandData(commandID = "cmd_version", identifier = "Version", messageLength = 1)
    public static void run(CommandEvent e) {
        e.reply(message);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().startsWith("<@!" + Bot.getBot().getJda().getSelfUser().getId() + ">"))
            e.getMessage().replyEmbeds(EmbedMaker.create(message)).queue();
    }
}

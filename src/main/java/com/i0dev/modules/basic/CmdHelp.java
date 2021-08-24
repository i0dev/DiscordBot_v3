package com.i0dev.modules.basic;

import com.i0dev.Bot;
import com.i0dev.config.CustomCommandsConfig;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.*;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.stream.Collectors;

public class CmdHelp extends DiscordCommand {

    @CommandData(commandID = "cmd_help", identifier = "Help", maxMessageLength = 2, canBePrivateMessage = true)
    public static void run(CommandEvent e) {
        Integer page = 1;
        if (e.getSplit().length > 1) {
            if ((page = FindUtil.getInteger(e.split[1], e.getMessage())) == null) return;
        }

        String prefix = GeneralConfig.get().getPrefixes().get(0);


        StringBuilder basicCommands = new StringBuilder();
        basicCommands.append("__**Commands**__").append("\n");
        for (BasicCommand registeredCommand : Bot.getRegisteredCommands().stream().filter(command -> !(command instanceof AdvancedCommand)).collect(Collectors.toList())) {
            basicCommands.append("`").append(prefix).append(registeredCommand.getAliases().get(0)).append("`").append(", ");
        }
        basicCommands.delete(basicCommands.length() - 2, basicCommands.length() - 1);

        basicCommands.append("\n\n__**Advanced Commands**__").append("\n");
        for (BasicCommand registeredCommand : Bot.getRegisteredCommands().stream().filter(command -> (command instanceof AdvancedCommand)).collect(Collectors.toList())) {
            StringBuilder internal = new StringBuilder();
            AdvancedDiscordCommand.getAdvancedCommand(registeredCommand.getClazz()).getSuperCommands().forEach(superCommand -> internal.append(superCommand.getAliases().get(0)).append(" | "));
            internal.delete(internal.length() - 3, internal.length());
            basicCommands.append("`").append(prefix).append(registeredCommand.getAliases().get(0)).append(" (").append(internal).append(")`").append("\n");
        }

        basicCommands.append("\n__**Custom Commands**__").append("\n");
        for (CustomCommandsConfig.MessageSetting cmd : CustomCommandsConfig.get().getCustomCommands()) {
            basicCommands.append("`" + prefix + cmd.getCallers().get(0) + "`, ");
        }
        basicCommands.delete(basicCommands.length() - 2, basicCommands.length() - 1);


        e.reply(EmbedMaker.builder().author(e.getAuthor()).authorName("DiscordBot Help Page").authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl()).content(basicCommands.toString()).build());

    }
}

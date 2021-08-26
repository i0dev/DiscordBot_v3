package com.i0dev.modules.basic;

import com.i0dev.Bot;
import com.i0dev.config.CustomCommandsConfig;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.*;
import com.i0dev.utility.EmbedMaker;

import java.util.List;
import java.util.stream.Collectors;

public class CmdHelp extends DiscordCommand {

    @CommandData(commandID = "cmd_help", identifier = "Help", messageLength = 1, canBePrivateMessage = true)
    public static void run(CommandEvent e) {
        String prefix = GeneralConfig.get().getPrefixes().get(0);
        StringBuilder basicCommands = new StringBuilder();
        basicCommands.append("__**Commands**__").append("\n");
        for (BasicCommand registeredCommand : Bot.getBot().getRegisteredCommands().stream().filter(command -> !(command instanceof AdvancedCommand)).collect(Collectors.toList())) {
            if (!registeredCommand.isEnabled()) continue;
            basicCommands.append("`").append(prefix).append(registeredCommand.getAliases().get(0)).append("`").append(", ");
        }
        basicCommands.delete(basicCommands.length() - 2, basicCommands.length() - 1);

        basicCommands.append("\n\n__**Advanced Commands**__").append("\n");
        for (BasicCommand registeredCommand : Bot.getBot().getRegisteredCommands().stream().filter(command -> (command instanceof AdvancedCommand)).collect(Collectors.toList())) {
            StringBuilder internal = new StringBuilder();
            if (!registeredCommand.isEnabled()) continue;
            AdvancedDiscordCommand.getAdvancedCommand(registeredCommand.getClazz()).getSuperCommands().forEach(superCommand -> {
                if (!superCommand.isEnabled()) return;
                internal.append(superCommand.getAliases().get(0)).append(" | ");
            });
            internal.delete(internal.length() - 3, internal.length());
            basicCommands.append("`").append(prefix).append(registeredCommand.getAliases().get(0)).append(" (").append(internal).append(")`").append("\n");
        }

        List<CustomCommandsConfig.MessageSetting> list = CustomCommandsConfig.get().getCustomCommands().stream().filter(messageSetting -> !messageSetting.getCallers().get(0).equals("Example Custom Message")).collect(Collectors.toList());
        if (!list.isEmpty()) {
            basicCommands.append("\n__**Custom Commands**__").append("\n");
            for (CustomCommandsConfig.MessageSetting cmd : list) {
                basicCommands.append("`").append(prefix).append(cmd.getCallers().get(0)).append("`, ");
            }
            basicCommands.delete(basicCommands.length() - 2, basicCommands.length() - 1);
        }
        

        e.reply(EmbedMaker.builder().author(e.getAuthor()).authorName("DiscordBot Help Page").authorImg(Bot.getBot().getJda().getSelfUser().getEffectiveAvatarUrl()).content(basicCommands.toString()).build());

    }
}

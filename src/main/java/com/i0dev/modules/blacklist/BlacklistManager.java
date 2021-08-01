package com.i0dev.modules.blacklist;

import com.i0dev.config.CommandsConfig;
import com.i0dev.object.*;
import lombok.SneakyThrows;

public class BlacklistManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("add", new SuperCommand(CommandsConfig.s("add"), Permission.strict(), Add.class));
        addSuperCommand("remove", new SuperCommand(CommandsConfig.s("remove"), Permission.strict(), Remove.class));
        addSuperCommand("clear", new SuperCommand(CommandsConfig.s("clear"), Permission.admin(), Clear.class));
        addSuperCommand("list", new SuperCommand(CommandsConfig.s("list"), Permission.strict(), List.class));
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_blacklist", identifier = "Blacklist")
    public static void run(CommandEvent e) {
    }
}

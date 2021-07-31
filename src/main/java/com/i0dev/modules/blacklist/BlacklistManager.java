package com.i0dev.modules.blacklist;

import com.i0dev.config.BasicCommandsConfig;
import com.i0dev.object.*;
import lombok.SneakyThrows;

public class BlacklistManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("add", new SuperCommand(BasicCommandsConfig.s("add"), Permission.strict(), Add.class));
        addSuperCommand("remove", new SuperCommand(BasicCommandsConfig.s("remove"), Permission.strict(), Remove.class));
        addSuperCommand("clear", new SuperCommand(BasicCommandsConfig.s("clear"), Permission.admin(), Clear.class));
        addSuperCommand("list", new SuperCommand(BasicCommandsConfig.s("list"), Permission.strict(), List.class));
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_blacklist", identifier = "Blacklist")
    public static void run(CommandEvent e) {
    }
}

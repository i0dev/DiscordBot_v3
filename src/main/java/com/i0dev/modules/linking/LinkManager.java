package com.i0dev.modules.linking;

import com.i0dev.config.BasicCommandsConfig;
import com.i0dev.modules.invite.*;
import com.i0dev.object.*;
import lombok.SneakyThrows;

public class LinkManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("code", new SuperCommand(s("code"), Permission.none(), Code.class));
        addSuperCommand("force", new SuperCommand(s("force"), Permission.none(), Force.class));
        addSuperCommand("generate", new SuperCommand(s("generate"), Permission.none(), Generate.class));
        addSuperCommand("info", new SuperCommand(s("info"), Permission.lite(), Info.class));
        addSuperCommand("remove", new SuperCommand(s("remove"), Permission.strict(), Remove.class));

    }

    @SneakyThrows
    @CommandData(commandID = "cmd_link", identifier = "Link")
    public static void run(CommandEvent e) {
    }
}

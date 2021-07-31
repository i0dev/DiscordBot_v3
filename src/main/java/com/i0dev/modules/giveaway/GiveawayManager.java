package com.i0dev.modules.giveaway;

import com.i0dev.config.BasicCommandsConfig;
import com.i0dev.modules.invite.*;
import com.i0dev.object.*;
import com.i0dev.utility.SQLUtil;
import lombok.SneakyThrows;

public class GiveawayManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("create", new SuperCommand(BasicCommandsConfig.s("create"), Permission.strict(), Create.class));
        addSuperCommand("end", new SuperCommand(BasicCommandsConfig.s("end"), Permission.strict(), End.class));
        addSuperCommand("info", new SuperCommand(BasicCommandsConfig.s("info"), Permission.none(), Info.class));
        addSuperCommand("reroll", new SuperCommand(BasicCommandsConfig.s("reroll"), Permission.strict(), Reroll.class));

        SQLUtil.makeTable(Giveaway.class);
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_giveaway", identifier = "Giveaway Manager")
    public static void run(CommandEvent e) {
    }
}

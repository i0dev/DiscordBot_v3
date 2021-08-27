package com.i0dev.modules.giveaway;

import com.i0dev.Bot;
import com.i0dev.config.CommandsConfig;
import com.i0dev.object.*;
import com.i0dev.managers.SQLManager;
import lombok.SneakyThrows;

public class GiveawayManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("create", new SuperCommand(CommandsConfig.s("create"), Permission.strict(), Create.class));
        addSuperCommand("end", new SuperCommand(CommandsConfig.s("end"), Permission.strict(), End.class));
        addSuperCommand("info", new SuperCommand(CommandsConfig.s("info"), Permission.none(), Info.class));
        addSuperCommand("reroll", new SuperCommand(CommandsConfig.s("reroll"), Permission.strict(), Reroll.class));

        Bot.getBot().getManager(SQLManager.class).makeTable(Giveaway.class);
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_giveaway", identifier = "Giveaway Manager")
    public static void run(CommandEvent e) {
    }
}

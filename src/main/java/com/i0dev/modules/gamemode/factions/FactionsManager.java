package com.i0dev.modules.gamemode.factions;
import com.i0dev.config.BasicCommandsConfig;
import com.i0dev.modules.invite.*;
import com.i0dev.object.*;
import com.i0dev.utility.SQLUtil;
import lombok.SneakyThrows;

public class FactionsManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("leader", new SuperCommand(BasicCommandsConfig.s("leader"), Permission.strict(), Leader.class));
        addSuperCommand("confirm", new SuperCommand(BasicCommandsConfig.s("confirm"), Permission.strict(), Confirm.class));
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_factions", identifier = "Factions Manager")
    public static void run(CommandEvent e) {
    }
}
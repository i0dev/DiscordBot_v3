package com.i0dev.modules.gamemode.prison;
import com.i0dev.config.CommandsConfig;
import com.i0dev.object.*;
import lombok.SneakyThrows;

public class PrisonManager  extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("leader", new SuperCommand(CommandsConfig.s("leader"), Permission.strict(), Leader.class));
        addSuperCommand("confirm", new SuperCommand(CommandsConfig.s("confirm"), Permission.strict(), Confirm.class));
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_prison", identifier = "Prison Manager")
    public static void run(CommandEvent e) {
    }
}
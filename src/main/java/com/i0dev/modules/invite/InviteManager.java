package com.i0dev.modules.invite;

import com.i0dev.config.CommandsConfig;
import com.i0dev.object.*;
import lombok.SneakyThrows;

public class InviteManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("invites", new SuperCommand(CommandsConfig.s("invites"), Permission.none(), Invites.class));
        addSuperCommand("leaderboard", new SuperCommand(CommandsConfig.s("leaderboard"), Permission.none(), Leaderboard.class));
        addSuperCommand("add", new SuperCommand(CommandsConfig.s("add"), Permission.strict(), Add.class));
        addSuperCommand("remove", new SuperCommand(CommandsConfig.s("remove"), Permission.strict(), Remove.class));
        addSuperCommand("clear", new SuperCommand(CommandsConfig.s("clear"), Permission.admin(), Clear.class));
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_invite", identifier = "Invite")
    public static void run(CommandEvent e) {
    }
}

package com.i0dev.modules.invite;

import com.i0dev.config.BasicCommandsConfig;
import com.i0dev.object.*;
import lombok.SneakyThrows;

public class InviteManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("invites", new SuperCommand(BasicCommandsConfig.s("invites"), Permission.none(), Invites.class));
        addSuperCommand("leaderboard", new SuperCommand(BasicCommandsConfig.s("leaderboard"), Permission.none(), Leaderboard.class));
        addSuperCommand("add", new SuperCommand(BasicCommandsConfig.s("add"), Permission.strict(), Add.class));
        addSuperCommand("remove", new SuperCommand(BasicCommandsConfig.s("remove"), Permission.strict(), Remove.class));
        addSuperCommand("clear", new SuperCommand(BasicCommandsConfig.s("clear"), Permission.admin(), Clear.class));
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_invite", identifier = "Invite")
    public static void run(CommandEvent e) {
    }
}

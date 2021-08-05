package com.i0dev.modules.points;

import com.i0dev.object.*;
import com.i0dev.utility.SQLUtil;
import lombok.SneakyThrows;

public class PointsManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("add", new SuperCommand(s("add"), Permission.strict(), Add.class));
        addSuperCommand("remove", new SuperCommand(s("remove"), Permission.strict(), Remove.class));
        addSuperCommand("set", new SuperCommand(s("set"), Permission.strict(), Set.class));
        addSuperCommand("balance", new SuperCommand(s("balance"), Permission.none(), Balance.class));
        addSuperCommand("pay", new SuperCommand(s("pay"), Permission.none(), Pay.class));
        addSuperCommand("info", new SuperCommand(s("info"), Permission.none(), Info.class));
        addSuperCommand("leaderboard", new SuperCommand(s("leaderboard"), Permission.none(), Leaderboard.class));

        SQLUtil.makeTable(DiscordPoints.class);

        addOption("imageSent", 3.0D);
        addOption("videoSent", 5.0D);
        addOption("messageCharacterModifier", 0.00083D);
        addOption("boost", 500.0D);
        addOption("prevBoostsEffectBoostPoints", true);
        addOption("reaction", 1.5D);
        addOption("voiceChannelSeconds", 300.0D);
        addOption("voiceChannelXSecondsPoints", 3.0D);
        addOption("inviteUser", 20.0D);
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_points", identifier = "Points Manager")
    public static void run(CommandEvent e) {
    }
}

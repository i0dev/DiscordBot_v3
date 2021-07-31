package com.i0dev.modules.mute;

import com.i0dev.Bot;
import com.i0dev.config.BasicCommandsConfig;
import com.i0dev.object.*;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Role;

public class MuteManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("add", new SuperCommand(BasicCommandsConfig.s("add"), Permission.lite(), Add.class));
        addSuperCommand("remove", new SuperCommand(BasicCommandsConfig.s("remove"), Permission.lite(), Remove.class));
        addSuperCommand("clear", new SuperCommand(BasicCommandsConfig.s("clear"), Permission.admin(), Clear.class));
        addSuperCommand("list", new SuperCommand(BasicCommandsConfig.s("list"), Permission.lite(), Retrieve.class));
        addSuperCommand("create", new SuperCommand(BasicCommandsConfig.s("create"), Permission.strict(), Create.class));

        addOption("role", 0L);
        mutedRole = Bot.getJda().getRoleById(getOption("role").getAsLong());
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_mute", identifier = "Mute Manager")
    public static void run(CommandEvent e) {
    }


    public static Role mutedRole;

    public static void saveRole(long roleID) {
        MuteManager.forceSaveOption("role", roleID);
    }

}

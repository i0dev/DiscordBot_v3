package com.i0dev.modules.mute;

import com.i0dev.Bot;
import com.i0dev.config.CommandsConfig;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.jetbrains.annotations.NotNull;

public class MuteManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("add", new SuperCommand(CommandsConfig.s("add"), Permission.lite(), Add.class));
        addSuperCommand("remove", new SuperCommand(CommandsConfig.s("remove"), Permission.lite(), Remove.class));
        addSuperCommand("clear", new SuperCommand(CommandsConfig.s("clear"), Permission.admin(), Clear.class));
        addSuperCommand("list", new SuperCommand(CommandsConfig.s("list"), Permission.lite(), Retrieve.class));
        addSuperCommand("create", new SuperCommand(CommandsConfig.s("create"), Permission.strict(), Create.class));

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


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        DPlayer dPlayer = DPlayer.getDPlayer(event.getUser());
        if (!dPlayer.isMuted()) return;
        new RoleQueueObject(event.getUser().getIdLong(), MuteManager.mutedRole.getIdLong(), Type.ADD_ROLE).add();
    }
}

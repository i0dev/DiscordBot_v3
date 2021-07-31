package com.i0dev.modules.gamemode.prison;

import com.i0dev.modules.gamemode.factions.FactionsManager;
import com.i0dev.object.*;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.User;

public class Leader extends SuperDiscordCommand {

    public static void load() {
        addOption("role", 0L);
    }

    @CommandData(commandID = "leader", parentClass = PrisonManager.class, identifier = "Prison Leader", usage = "<user>", messageLength = 2)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;

        e.reply(EmbedMaker.builder().user(user).embedColor(EmbedColor.SUCCESS).content("You have successfully given {tag} the Prison Leader role.").build());

        new RoleQueueObject(user.getIdLong(), getOption("role").getAsLong(), Type.ADD_ROLE).add();

    }
}

package com.i0dev.modules.gamemode.factions;

import com.i0dev.modules.giveaway.Giveaway;
import com.i0dev.modules.giveaway.GiveawayHandler;
import com.i0dev.modules.giveaway.GiveawayManager;
import com.i0dev.modules.movement.MovementObject;
import com.i0dev.object.*;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class Leader extends SuperDiscordCommand {

    public static void load() {
        addOption("role", 0L);
    }

    @CommandData(commandID = "leader", parentClass = FactionsManager.class, identifier = "Factions Leader", usage = "<user>", messageLength = 2)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;

        e.reply(EmbedMaker.builder().user(user).embedColor(EmbedColor.SUCCESS).content("You have successfully given {tag} the Faction Leader role.").build());

        new RoleQueueObject(user.getIdLong(), getOption("role").getAsLong(), Type.ADD_ROLE).add();

    }
}

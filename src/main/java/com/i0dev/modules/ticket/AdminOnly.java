package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.modules.movement.MovementManager;
import com.i0dev.modules.movement.MovementObject;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.ConfigUtil;
import com.i0dev.utility.EmbedMaker;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class AdminOnly extends SuperDiscordCommand {

    public static List<Long> adminOnlySeeRoles = new ArrayList<>();

    public static void load() {
        addOption("adminOnlySeeRoles", ConfigUtil.ObjectToJsonArr(adminOnlySeeRoles));
        adminOnlySeeRoles = new ArrayList<>();
        ConfigUtil.getObjectFromInternalPath(getAnnotation(TicketManager.class).commandID() + ".parts.adminOnly.options.adminOnlySeeRoles", ConfigUtil.getJsonObject(Bot.getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> adminOnlySeeRoles.add(jsonElement.getAsLong()));
    }

    @CommandData(commandID = "adminOnly", identifier = "Ticket AdminOnly", messageLength = 1, parentClass = TicketManager.class)
    public static void run(CommandEvent e) {
        if (!TicketManager.isTicket(e.getChannel())) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("This command can only be used in a ticket.").build());
            return;
        }

        Ticket ticket = Ticket.getTicket(e.getChannel());
        if (ticket.isAdminOnlyMode()) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("This ticket is already in Admin-Only mode.").build());
            return;
        }
        ticket.setAdminOnlyMode(true);
        ticket.save();

        for (Long roleID : TicketManager.getRolesToSeeTickets()) {
            Role role = e.getGuild().getRoleById(roleID);
            if (role == null) continue;
            e.getChannel().putPermissionOverride(role)
                    .setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS,
                            Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                            Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_MENTION_EVERYONE,
                            Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS, Permission.MANAGE_WEBHOOKS,
                            Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();
        }
        for (Long roleID : adminOnlySeeRoles) {
            Role role = e.getGuild().getRoleById(roleID);
            if (role == null) continue;
            e.getChannel().putPermissionOverride(role)
                    .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                    .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                            Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();
        }

        e.reply(EmbedMaker.builder().content("You have made this ticket Admin-Only mode.").embedColor(EmbedColor.SUCCESS).build());
    }
}

package com.i0dev.modules.ticket;

import com.google.gson.JsonObject;
import com.i0dev.Bot;
import com.i0dev.modules.CommandManager;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.ConfigUtil;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketAdminOnlyHandler extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getButton() == null) return;
        if (!"BUTTON_TICKET_ADMIN_ONLY".equalsIgnoreCase(e.getButton().getId())) return;
        if (e.getUser().isBot()) return;
        if (!ConfigUtil.getObjectFromInternalPath("cmd_ticket.parts.adminOnly.enabled", ConfigUtil.getJsonObject(Bot.getBasicConfigPath())).getAsBoolean())
            return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        if (DPlayer.getDPlayer(e.getUser()).isBlacklisted()) return;
        JsonObject ob = ConfigUtil.getObjectFromInternalPath("cmd_ticket.parts.adminOnly.permission", ConfigUtil.getJsonObject(Bot.getBasicConfigPath())).getAsJsonObject();
        if (!CommandManager.hasPermission(e.getMember(), ob.get("strict").getAsBoolean(), ob.get("lite").getAsBoolean(), ob.get("admin").getAsBoolean()))
            return;
        if (!TicketManager.isTicket(e.getChannel())) return;

        Ticket ticket = Ticket.getTicket(e.getChannel());
        if (ticket.isAdminOnlyMode()) {
            e.getChannel().sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("This ticket is already in Admin-Only mode.").build())).queue();
            return;
        }
        ticket.setAdminOnlyMode(true);
        ticket.save();

        for (Long roleID : TicketManager.getRolesToSeeTickets()) {
            Role role = e.getGuild().getRoleById(roleID);
            if (role == null) continue;
            ((TextChannel) e.getChannel()).putPermissionOverride(role)
                    .setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS,
                            Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                            Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_MENTION_EVERYONE,
                            Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS, Permission.MANAGE_WEBHOOKS,
                            Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();
        }
        for (Long roleID : AdminOnly.adminOnlySeeRoles) {
            Role role = e.getGuild().getRoleById(roleID);
            if (role == null) continue;
            ((TextChannel) e.getChannel()).putPermissionOverride(role)
                    .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                    .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                            Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();
        }

        e.getChannel().sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().content("You have made this ticket Admin-Only mode.").embedColor(EmbedColor.SUCCESS).build())).queue();
        e.deferEdit().queue();
    }

}

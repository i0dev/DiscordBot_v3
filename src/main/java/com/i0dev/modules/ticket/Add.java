package com.i0dev.modules.ticket;

import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.SuperDiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

public class Add extends SuperDiscordCommand {

    @CommandData(commandID = "add", identifier = "Ticket Add", messageLength = 2, usage = "<user>", parentClass = TicketManager.class)
    public static void run(CommandEvent e) {
        if (!TicketManager.isTicket(e.getChannel())) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("This command can only be used in a ticket.").build());
            return;
        }
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;

        e.getChannel().putPermissionOverride(Objects.requireNonNull(e.getGuild().getMember(user)))
                .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                        Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                .queue();

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have added {tag} to this ticket!").user(user).build());

    }
}
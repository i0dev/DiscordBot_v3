package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.Engine;
import com.i0dev.config.MiscConfig;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.LogObject;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TicketCreateHandler extends ListenerAdapter {

    public int getUsersTicketCount(User user) {
        int count = 0;
        for (Object o : SQLUtil.getAllObjects(Ticket.class.getSimpleName(), "channelID", Ticket.class)) {
            Ticket ticket = ((Ticket) o);
            if (ticket.getTicketOwnerID() == user.getIdLong())
                count++;
        }
        return count;
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        long ticketCreateChannelID = TicketManager.getOption("ticketCreateChannel", TicketManager.class).getAsLong();
        if (e.getChannel().getIdLong() != (ticketCreateChannelID)) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        DPlayer dPlayer = DPlayer.getDPlayer(e.getUser());
        if (dPlayer.isBlacklisted()) return;
        for (TicketOption option : TicketManager.getOptions()) {
            String Emoji = option.getEmoji();
            if (!EmojiUtil.isEmojiValid(e.getReactionEmote(), Emoji)) continue;
            e.getChannel().removeReactionById(e.getMessageId(), EmojiUtil.getEmojiWithoutArrow(Emoji), e.getUser()).queue();

            int maxTicketsPerUser = TicketManager.getOption("maxTicketsPerUser", TicketManager.class).getAsInt();
            if (getUsersTicketCount(e.getUser()) >= maxTicketsPerUser) {
                MessageUtil.sendPrivateMessage(null, e.getUser(), EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(e.getUser()).content("You have reached your maximum amount of open tickets.").build());
                return;
            }

            List<String> Questions = option.getQuestions();
            boolean AdminOnlyDefault = option.isAdminOnlyDefault();
            boolean PingStaffRoles = option.isPingStaff();
            String ChannelName = option.getChannelName();
            long categoryID = option.getCategory();
            Category NewTicketCreatedCategory = Bot.getBot().getJda().getCategoryById(categoryID);
            TextChannel NewTicketCreated;
            MiscConfig.get().ticketNumber += 1;
            ConfigUtil.save(MiscConfig.get(), Bot.getBot().getMiscConfigPath());
            if (NewTicketCreatedCategory != null)
                NewTicketCreated = NewTicketCreatedCategory.createTextChannel(ChannelName.replace("{num}", MiscConfig.get().getTicketNumber() + "")).complete();
            else
                NewTicketCreated = e.getGuild().createTextChannel(ChannelName.replace("{num}", MiscConfig.get().getTicketNumber() + "")).complete();

            NewTicketCreated.putPermissionOverride(e.getMember())
                    .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                    .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                            Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();

            NewTicketCreated.putPermissionOverride(e.getGuild().getPublicRole())
                    .setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE,
                            Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                            Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();

            if (!AdminOnlyDefault) {
                for (Long roleID : TicketManager.getRolesToSeeTickets()) {
                    Role role = e.getGuild().getRoleById(roleID);
                    if (role == null) continue;
                    NewTicketCreated.putPermissionOverride(role)
                            .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                                    Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                                    Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                            .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                                    Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                            .queue();
                }
            }
            for (Long roleID : AdminOnly.adminOnlySeeRoles) {
                Role role = e.getGuild().getRoleById(roleID);
                if (role == null) continue;
                NewTicketCreated.putPermissionOverride(role)
                        .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                                Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                        .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                                Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                        .queue();

            }


            if (PingStaffRoles) {
                List<Role> rolesToPing = new ArrayList<>();
                for (Long roleID : TicketManager.rolesToSeeTickets) {
                    Role role = e.getGuild().getRoleById(roleID);
                    if (role == null) continue;
                    rolesToPing.add(role);
                }
                NewTicketCreated.sendMessage(Utility.FormatList(rolesToPing) + ", " + e.getMember().getAsMention()).queue();
            } else NewTicketCreated.sendMessage(e.getMember().getAsMention()).queue();

            StringBuilder userInfo = new StringBuilder();
            userInfo.append("Linked IGN: ").append("`{ign}`").append("\n");
            userInfo.append("Ticket Number `").append(MiscConfig.get().ticketNumber).append("`\n");
            userInfo.append("Category`").append(NewTicketCreated.getParent() == null ? "None" : NewTicketCreated.getParent().getName()).append("`\n");

            NewTicketCreated.sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder()
                            .author(e.getUser())
                            .user(e.getUser())
                            .authorImg(e.getUser().getEffectiveAvatarUrl())
                            .authorName("New ticket from: {tag}")
                            .fields(new MessageEmbed.Field[]{
                                    new MessageEmbed.Field("__Questions:__", "```" + Utility.FormatListString(Questions) + "```", true),
                                    new MessageEmbed.Field("__Information:__", userInfo.toString(), true)
                            })

                            .build()))
                    .setActionRow(Button.danger("BUTTON_TICKET_CLOSE", "Close").withEmoji(net.dv8tion.jda.api.entities.Emoji.fromMarkdown(TicketManager.getOption("closeTicketEmoji", TicketManager.class).getAsString())),
                            Button.success("BUTTON_TICKET_ADMIN_ONLY", "Admin Only").withEmoji(net.dv8tion.jda.api.entities.Emoji.fromMarkdown(TicketManager.getOption("adminOnlyEmoji", TicketManager.class).getAsString())))
                    .queue();

            Ticket ticket = new Ticket();
            ticket.setAdminOnlyMode(AdminOnlyDefault);
            ticket.setTicketOwnerID(e.getUser().getIdLong());
            ticket.setChannelID(NewTicketCreated.getIdLong());
            ticket.setTicketName(NewTicketCreated.getName());
            ticket.setTicketNumber(MiscConfig.get().ticketNumber);
            ticket.save();

            File ticketLogsFile = new File(Bot.getBot().getTicketLogsPath() + NewTicketCreated.getId() + ".log");

            String Zone = ZonedDateTime.now().getZone().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            Long ticketOwnerID = ticket.getTicketOwnerID();
            String ticketOwnerAvatarURL = e.getUser().getEffectiveAvatarUrl();
            String ticketOwnerTag = e.getUser().getAsTag();
            boolean adminOnlyMode = ticket.isAdminOnlyMode();

            StringBuilder toFile = new StringBuilder();
            toFile.append("Ticket information:").append("\n");
            toFile.append("     Channel ID: ").append(NewTicketCreated.getId()).append("\n");
            toFile.append("     Channel Name: ").append(NewTicketCreated.getName()).append("\n");
            toFile.append("     Ticket Owner ID: ").append(ticketOwnerID).append("\n");
            toFile.append("     Ticket Owner Tag: ").append(ticketOwnerTag).append("\n");
            toFile.append("     Ticket Owner Avatar: ").append(ticketOwnerAvatarURL).append("\n");
            toFile.append("     Admin Only Mode: ").append(adminOnlyMode).append("\n");
            toFile.append("Ticket logs (TimeZone: ").append(Zone).append("):");

            Engine.getToLog().add(new LogObject(toFile.toString(), ticketLogsFile));

            StringBuilder dm = new StringBuilder();
            dm.append("You have created a new ticket in the **{guildName}** discord!\n");
            dm.append("Click {channel} to go to it!".replace("{channel}", NewTicketCreated.getAsMention()));

            MessageUtil.sendPrivateMessage(null, e.getUser(), EmbedMaker.builder().embedColor(EmbedColor.SUCCESS)
                    .author(e.getUser())
                    .user(e.getUser())
                    .authorImg(e.getUser().getEffectiveAvatarUrl())
                    .authorName("You created a ticket: {name}".replace("{name}", NewTicketCreated.getName()))
                    .content(dm.toString())
                    .build());

        }
    }
}

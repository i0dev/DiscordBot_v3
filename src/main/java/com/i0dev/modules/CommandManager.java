package com.i0dev.modules;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandManager extends ListenerAdapter {

    public static List<String> getSuperMessageArray(String[] message, int offset) {
        List<String> ret = new ArrayList<>(Arrays.asList(message));
        if (offset > 0)
            ret.subList(0, offset).clear();
        return ret;
    }


    private boolean isCommand(List<String> aliases, Message message) {
        for (String alias : aliases) {
            for (String prefix : GeneralConfig.get().getPrefixes()) {
                if ((prefix + alias.toLowerCase()).equalsIgnoreCase(message.getContentRaw().split(" ")[0])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSuperCommand(List<String> aliases, Message message) {
        for (String alias : aliases) {
            if ((alias.toLowerCase()).equalsIgnoreCase(message.getContentRaw().split(" ")[1])) {
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        try {
            if (e.getAuthor().isBot()) return;
            if (e.getChannelType().isGuild()) {
                if (!Utility.isValidGuild(e.getGuild())) return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");

            boolean initCheck = false;
            for (String prefix : GeneralConfig.get().getPrefixes()) {
                if (e.getMessage().getContentRaw().startsWith(prefix)) {
                    initCheck = true;
                    break;
                }
            }
            if (!initCheck) return;

            DPlayer dPlayer = DPlayer.getDPlayer(e.getAuthor().getIdLong());
            if (dPlayer.isBlacklisted()) {
                CommandEvent.replyStaticComplete(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("You are blacklisted and cannot use commands.").build(), e.getMessage()).delete().queueAfter(5, TimeUnit.SECONDS);
                return;
            }

            for (BasicCommand command : Bot.getRegisteredCommands()) {
                if (isCommand(command.getAliases(), e.getMessage())) {
                    CommandData data = DiscordCommand.getAnnotation(command.getClazz());
                    if (!check(e.getMessage(), data, command, command.isEnabled(), data.identifier(), dPlayer, e.getMember(), command.getAliases().get(0), 0))
                        return;
                    if (command instanceof AdvancedCommand) {
                        StringBuilder usages = new StringBuilder();
                        for (SuperCommand superCommand : AdvancedDiscordCommand.getAdvancedCommand(command.getClazz()).getSuperCommands()) {
                            usages.append(GeneralConfig.get().getPrefixes().get(0)).append(AdvancedDiscordCommand.getAdvancedCommand(command.getClazz()).getAliases().get(0)).append(" ").append(superCommand.getAliases().get(0)).append(" ").append(SuperDiscordCommand.getAnnotation(superCommand.getClazz()).usage()).append("\n");
                        }
                        EmbedMaker usage = EmbedMaker.builder().field(new MessageEmbed.Field(DiscordCommand.getAnnotation(AdvancedDiscordCommand.getAdvancedCommand(command.getClazz()).getClazz()).identifier() + " Commands", usages.toString(), true)).build();

                        if (message.length == 1) {
                            CommandEvent.replyStatic(usage, e.getMessage());
                            return;
                        }
                        for (SuperCommand superCommand : AdvancedDiscordCommand.getAdvancedCommand(command.getClazz()).getSuperCommands()) {
                            if (CommandManager.isSuperCommand(superCommand.getAliases(), e.getMessage())) {
                                CommandData superData = SuperDiscordCommand.getAnnotation(superCommand.getClazz());
                                if (!check(e.getMessage(), superData, superCommand, command.isEnabled(), superData.identifier(), dPlayer, e.getMember(), command.getAliases().get(0) + " " + superCommand.getAliases().get(0), 1))
                                    return;
                                superCommand.getClazz().getMethod("run", CommandEvent.class).invoke(superCommand.getClazz().newInstance(), newEvent(e, 1));
                                return;
                            }
                        }
                        CommandEvent.replyStatic(usage, e.getMessage());

                        return;
                    }

                    command.getClazz().getMethod("run", CommandEvent.class).invoke(command.getClazz().newInstance(), newEvent(e, 0));
                    return;
                }
            }

            for (BasicCommand advancedCommand : Bot.getRegisteredCommands().stream().filter(command -> command instanceof AdvancedCommand).collect(Collectors.toList())) {
                for (SuperCommand superCommand : ((AdvancedCommand) advancedCommand).getSuperCommands()) {
                    if (isCommand(superCommand.getAlternateCommands(), e.getMessage())) {
                        CommandData data = DiscordCommand.getAnnotation(superCommand.getClazz());
                        if (!check(e.getMessage(), data, superCommand, superCommand.isEnabled(), data.identifier(), dPlayer, e.getMember(), message[0].substring(1), 0))
                            return;
                        superCommand.getClazz().getMethod("run", CommandEvent.class).invoke(superCommand.getClazz().newInstance(), newEvent(e, 0));
                        return;
                    }
                }
            }
        } catch (Exception error) {
            e.getChannel().sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder()
                    .embedColor(EmbedColor.FAILURE)
                    .authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl())
                    .authorName("An Error Occurred: " + error.getCause().getClass().getSimpleName())
                    .content(error.getCause().getMessage())
                    .build())).queue();
            error.printStackTrace();
        }
    }

    public static boolean check(Message message, CommandData data, BasicCommand command, boolean enabled, String identifier, DPlayer dPlayer, Member member, String usagePrefix, int offset) {
        String[] msg = message.getContentRaw().split(" ");
        EmbedMaker maker = EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Command usage: " +
                        GeneralConfig.get().getPrefixes().get(0) + (data.usage().equals("") ? usagePrefix : usagePrefix + " " + data.usage()))
                .build();
        if (!enabled) {
            CommandEvent.replyStatic(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("The command `{cmd}` is not enabled.".replace("{cmd}", identifier)).build(), message);
            return false;
        }
        if (data.messageLength() != -1) {
            if (msg.length != data.messageLength() + offset) {
                CommandEvent.replyStatic(maker, message);
                return false;
            }
        }
        if (data.maxMessageLength() != -1) {
            if (msg.length > data.maxMessageLength() + offset) {
                CommandEvent.replyStatic(maker, message);
                return false;
            }
        }
        if (data.minMessageLength() != -1) {
            if (msg.length < data.minMessageLength() + offset) {
                CommandEvent.replyStatic(maker, message);
                return false;
            }
        }
        if (data.requirePluginMode()) {
            if (!Bot.isPluginMode()) {
                CommandEvent.replyStatic(EmbedMaker.builder().content("This command can only be used in PluginMode").embedColor(EmbedColor.FAILURE).build(), message);
                return false;
            }
        }
        if (data.requireLinked()) {
            if (!dPlayer.isLinked()) {
                CommandEvent.replyStatic(EmbedMaker.builder().content("You need to be linked in order to use this command.").embedColor(EmbedColor.FAILURE).build(), message);
                return false;
            }
        }
        if (!hasPermission(member, command.getPermission().isStrict(), command.getPermission().isLite(), command.getPermission().isAdmin())) {
            CommandEvent.replyStatic(EmbedMaker.builder().content("You do not have permission to use this command.").embedColor(EmbedColor.FAILURE).build(), message);
            return false;
        }
        return true;
    }

    public static boolean hasPermission(Member member, boolean strict, boolean lite, boolean admin) {
        if (admin) {
            return member.hasPermission(Permission.ADMINISTRATOR);
        } else if (lite) {
            return hasLitePermission(member.getUser(), member.getGuild());
        } else if (strict) {
            return hasStrictPermission(member.getUser(), member.getGuild());
        }
        return true;
    }

    public static boolean hasLitePermission(User user, Guild guild) {
        if (hasStrictPermission(user, guild)) return true;
        for (long RoleID : GeneralConfig.get().getLiteAllowed()) {
            if (guild.getMember(user) == null) continue;
            if (guild.getRoleById(RoleID) == null) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().isEmpty()) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().contains(guild.getRoleById(RoleID))) {
                return true;
            }
        }
        for (long userID : GeneralConfig.get().getLiteAllowed()) {
            if (Bot.getJda().getUserById(userID) == null) continue;
            if (user.equals(Bot.getJda().getUserById(userID))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasStrictPermission(User user, Guild guild) {
        if (guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)) return true;
        if (Objects.requireNonNull(guild.getMember(user)).getPermissions().contains(Permission.ADMINISTRATOR))
            return true;
        for (long RoleID : GeneralConfig.get().getStrictAllowed()) {
            if (guild.getMember(user) == null) continue;
            if (guild.getRoleById(RoleID) == null) continue;

            if (Objects.requireNonNull(guild.getMember(user)).getRoles().isEmpty()) continue;
            if (Objects.requireNonNull(guild.getMember(user)).getRoles().contains(guild.getRoleById(RoleID))) {
                return true;
            }
        }
        for (long userID : GeneralConfig.get().getStrictAllowed()) {
            if (Bot.getJda().getUserById(userID) == null) continue;

            if (user.equals(Bot.getJda().getUserById(userID))) {
                return true;
            }
        }
        return false;
    }

    public static CommandEvent newEvent(GuildMessageReceivedEvent e, int offset) {

        return new CommandEvent(
                e.getAuthor(),
                e.getAuthor().getIdLong(),
                e.getAuthor().getId(),
                e.getMessage(),
                e.getChannel(),
                e.getGuild(),
                e.getMember(),
                DPlayer.getDPlayer(e.getAuthor().getIdLong()),
                e.getJDA(),
                e.getMessageId(),
                e.getMessageIdLong(),
                e.getMessage().getContentRaw().split(" "),
                getSuperMessageArray(e.getMessage().getContentRaw().split(" "), offset)
        );
    }

}

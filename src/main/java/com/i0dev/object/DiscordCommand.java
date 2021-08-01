package com.i0dev.object;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.i0dev.Bot;
import com.i0dev.config.CommandsConfig;
import com.i0dev.utility.ConfigUtil;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.Field;
import java.util.List;


public class DiscordCommand extends ListenerAdapter {

    public static String getCommandID(Class<? extends DiscordCommand> clazz) {
        return getAnnotation(clazz).commandID();
    }

    @SneakyThrows
    public static BasicCommand getBasicCommand(Class<? extends DiscordCommand> clazz) {
        BasicCommand ret;
        Field field = CommandsConfig.class.getDeclaredField(getCommandID(clazz));
        field.setAccessible(true);
        ret = (BasicCommand) field.get(CommandsConfig.get());
        return ret;
    }


    public static String getFirstAlias(Class<? extends DiscordCommand> clazz) {
        return getBasicCommand(clazz).getAliases().get(0);
    }

    public static List<String> getAliases(Class<? extends DiscordCommand> clazz) {
        return getBasicCommand(clazz).getAliases();
    }

    @SneakyThrows
    public static CommandData getAnnotation(Class<? extends DiscordCommand> clazz) {
        return clazz.getMethod("run", CommandEvent.class).getAnnotation(CommandData.class);
    }


    @SneakyThrows
    public static String getMessage(String key) {
        Class<? extends DiscordCommand> clazz = (Class<? extends DiscordCommand>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
        return getBasicCommand(clazz).getMessages().get(key).getAsString();
    }

    @SneakyThrows
    public static JsonElement getOption(String key) {
        Class<? extends DiscordCommand> clazz = (Class<? extends DiscordCommand>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
        BasicCommand command;
        if (clazz.getSuperclass().getName().equals(SuperDiscordCommand.class.getName()))
            command = SuperDiscordCommand.getSuperCommand(clazz, getAnnotation(clazz).parentClass());
        else command = getBasicCommand(clazz);
        return command.getOptions().get(key);
    }

    @SneakyThrows
    public static JsonElement getOption(String key, Class<? extends DiscordCommand> clazz) {
        BasicCommand command;
        if (clazz.getSuperclass().getName().equals(SuperDiscordCommand.class.getName()))
            command = SuperDiscordCommand.getSuperCommand(clazz, getAnnotation(clazz).parentClass());
        else command = getBasicCommand(clazz);
        return command.getOptions().get(key);
    }


    @SneakyThrows
    public static void addMessage(String key, String value) {
        Class<? extends DiscordCommand> clazz = (Class<? extends DiscordCommand>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
        BasicCommand cmd;
        if (clazz.getSuperclass().getName().equals(SuperDiscordCommand.class.getName()))
            cmd = SuperDiscordCommand.getSuperCommand(clazz, getAnnotation(clazz).parentClass());
        else cmd = getBasicCommand(clazz);
        if (cmd.messages == null) cmd.messages = new JsonObject();
        if (cmd.getMessages().has(key)) return;
        cmd.addMessage(key, value);
        if (clazz.getSuperclass().getName().equals(SuperDiscordCommand.class.getName()))
            AdvancedCommand.turnPartsListToParts();
        ConfigUtil.save(CommandsConfig.get(), Bot.getBasicConfigPath());
    }

    @SneakyThrows
    public static void addOption(String key, Object value) {
        Class<? extends DiscordCommand> clazz = (Class<? extends DiscordCommand>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
        BasicCommand cmd;
        if (clazz.getSuperclass().getName().equals(SuperDiscordCommand.class.getName())) {
            cmd = SuperDiscordCommand.getSuperCommand(clazz, getAnnotation(clazz).parentClass());
        } else cmd = getBasicCommand(clazz);

        if (cmd.options == null) cmd.options = new JsonObject();
        if (cmd.getOptions().has(key)) return;
        cmd.addOption(key, value);
        if (clazz.getSuperclass().getName().equals(SuperDiscordCommand.class.getName()))
            AdvancedCommand.turnPartsListToParts();
        ConfigUtil.save(CommandsConfig.get(), Bot.getBasicConfigPath());
    }

    @SneakyThrows
    public static void forceSaveOption(String key, Object value) {
        Class<? extends DiscordCommand> clazz = (Class<? extends DiscordCommand>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
        BasicCommand cmd;
        if (clazz.getSuperclass().getName().equals(SuperDiscordCommand.class.getName()))
            cmd = SuperDiscordCommand.getSuperCommand(clazz, getAnnotation(clazz).parentClass());
        else cmd = getBasicCommand(clazz);
        if (cmd.options == null) cmd.options = new JsonObject();
        cmd.addOption(key, value);
        if (clazz.getSuperclass().getName().equals(SuperDiscordCommand.class.getName()))
            AdvancedCommand.turnPartsListToParts();
        ConfigUtil.save(CommandsConfig.get(), Bot.getBasicConfigPath());
    }
}

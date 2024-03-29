package com.i0dev.object;

import com.i0dev.Bot;
import com.i0dev.config.CommandsConfig;
import com.i0dev.managers.ConfigManager;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvancedDiscordCommand extends DiscordCommand {

    public static <T> List<T> s(T s) {
        return Collections.singletonList(s);
    }

    @SafeVarargs
    protected static <T> List<T> ls(T... ts) {
        List<T> ret = new ArrayList<>();
        Collections.addAll(ret, ts);
        return ret;
    }

    @SneakyThrows
    public static AdvancedCommand getAdvancedCommand() {
        Class<? extends DiscordCommand> parent = (Class<? extends DiscordCommand>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
        AdvancedCommand ret;
        Field field = CommandsConfig.class.getDeclaredField(getCommandID(parent));
        field.setAccessible(true);
        ret = (AdvancedCommand) field.get(CommandsConfig.get());
        return ret;
    }

    @SneakyThrows
    public static AdvancedCommand getAdvancedCommand(Class<? extends DiscordCommand> clazz) {
        AdvancedCommand ret;
        Field field = CommandsConfig.class.getDeclaredField(getCommandID(clazz));
        field.setAccessible(true);
        ret = (AdvancedCommand) field.get(CommandsConfig.get());
        return ret;
    }


    @SneakyThrows
    public static void addSuperCommand(String key, SuperCommand value) {
        AdvancedCommand cmd = getAdvancedCommand((Class<? extends DiscordCommand>) Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()));
        if (cmd.getParts().has(key)) {
            SuperCommand superCommand = (SuperCommand) Bot.getBot().getConfigManager().JsonToObject(cmd.getParts().get(key), SuperCommand.class);
            superCommand.setClazz(value.getClazz());
            cmd.getSuperCommands().add(superCommand);
            return;
        }
        cmd.addSuperCommand(key, value);
        cmd.getSuperCommands().add(value);
        Bot.getBot().getManager(ConfigManager.class).save(CommandsConfig.get(), Bot.getBot().getBasicConfigPath());
    }

}

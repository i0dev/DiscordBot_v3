package com.i0dev.object;

import com.google.gson.JsonObject;
import com.i0dev.Bot;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
@Setter
@NoArgsConstructor
public class AdvancedCommand extends BasicCommand {
    JsonObject parts = new JsonObject();
    public transient List<SuperCommand> superCommands = new ArrayList<>();

    public AdvancedCommand(List<String> aliases, Permission permission) {
        this.permission = permission;
        this.aliases = aliases;
    }

    public void addSuperCommand(String key, SuperCommand defaultValue) {
        if (getParts().has(key)) return;
        getParts().add(key, Bot.getBot().getConfigManager().ObjectToJsonObj(defaultValue));
    }

    public static void turnPartsListToParts() {
        Bot.getBot().getRegisteredCommands().stream().filter(command -> command instanceof AdvancedCommand).forEach(command -> {
            JsonObject newOb = new JsonObject();
            ((AdvancedCommand) command).getSuperCommands().forEach(superCommand -> newOb.add(DiscordCommand.getAnnotation(superCommand.getClazz()).commandID(), Bot.getBot().getConfigManager().ObjectToJsonObj(superCommand)));
            ((AdvancedCommand) command).setParts(newOb);
        });
    }

}
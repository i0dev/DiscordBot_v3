package com.i0dev.object;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
@NoArgsConstructor
public class BasicCommand {

    public transient Class<? extends DiscordCommand> clazz;
    List<String> aliases = new ArrayList<>();
    boolean enabled = true;
    Permission permission = new Permission();
    JsonObject options = null;
    JsonObject messages = null;

    public BasicCommand(List<String> aliases, Permission permission) {
        this.permission = permission;
        this.aliases = aliases;
    }

    public String addMessage(String key, String defaultValue) {
        if (messages == null) messages = new JsonObject();
        getMessages().addProperty(key, defaultValue);
        return defaultValue;
    }

    public void addOption(String key, Object value) {
        if (options == null) options = new JsonObject();
        if (value instanceof String)
            getOptions().addProperty(key, ((String) value));
        else if (value instanceof Boolean)
            getOptions().addProperty(key, ((Boolean) value));
        else if (value instanceof Long || value instanceof Integer || value instanceof Double)
            getOptions().addProperty(key, ((Number) value));
        else
            getOptions().add(key, ((JsonElement) value));
    }

    public BasicCommand setClazz(Class<? extends DiscordCommand> clazz) {
        this.clazz = clazz;
        return this;
    }
}
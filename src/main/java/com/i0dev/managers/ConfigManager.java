package com.i0dev.managers;

import com.google.gson.*;
import com.i0dev.DiscordBot;
import com.i0dev.config.CommandsConfig;
import com.i0dev.config.CustomCommandsConfig;
import com.i0dev.config.GeneralConfig;
import com.i0dev.config.MiscConfig;
import com.i0dev.utility.LogUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigManager extends Manager {

    Map<Class<?>, String> configMap = new HashMap<>();

    public ConfigManager(DiscordBot bot) {
        super(bot);
    }

    @Override
    public void initialize() {
        deinitialize();

        configMap.put(CommandsConfig.class, bot.getBasicConfigPath());
        configMap.put(GeneralConfig.class, bot.getConfigPath());
        configMap.put(MiscConfig.class, bot.getMiscConfigPath());
        configMap.put(CustomCommandsConfig.class, bot.getCustomCommandsConfigPath());
        configMap.forEach(this::load);
    }

    @Override
    public void deinitialize() {
        configMap.clear();
    }

    public String ObjectToJson(Object object) {
        return new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(object), JsonObject.class).toString()));
    }

    public JsonObject ObjectToJsonObj(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonObject.class);
    }

    public JsonArray ObjectToJsonArr(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonArray.class);
    }

    public Object JsonToObject(JsonElement json, Class<?> clazz) {
        return new Gson().fromJson(new Gson().toJson(json), clazz);
    }

    @SneakyThrows
    public void save(Object object, String path) {
        Files.write(Paths.get(path), ObjectToJson(object).getBytes());
    }

    public JsonObject getJsonObject(String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            return new Gson().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    public JsonElement getObjectFromInternalPath(String path, JsonObject json) {
        String[] paths = path.split("\\.");
        if (paths.length == 1)
            return json.get(paths[0]);
        JsonObject finalProduct = new JsonObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) finalProduct = json.get(paths[i]).getAsJsonObject();
            else finalProduct = finalProduct.get(paths[i]).getAsJsonObject();
        }
        return finalProduct.get(paths[paths.length - 1]);
    }

    @SneakyThrows
    public void load(Class<?> clazz, String path) {
        JsonObject savedObject = getJsonObject(path);
        String configString = IOUtils.toString(Files.newBufferedReader(Paths.get(path)));
        Object config = new Gson().fromJson(savedObject, clazz);
        Field field = clazz.getDeclaredField("instance");
        field.setAccessible(true);
        if ("".equals(configString)) {
            save(field.get(new Object()), path);
            load(clazz, path);
            return;
        }

        if (config == null) throw new IOException("The config file: [" + path + "] is not in valid json format.");
        field.set(new Object(), config);
        save(config, path);
        LogUtil.log("Loaded config: " + clazz.getSimpleName() + " from storage.");
    }

    public void reloadConfig() {
        configMap.forEach(this::load);
    }
}

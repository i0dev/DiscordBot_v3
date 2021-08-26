package com.i0dev.utility;

import com.google.gson.*;
import com.i0dev.Bot;
import lombok.SneakyThrows;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigUtil {

    public static String ObjectToJson(Object object) {
        return new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(object), JsonObject.class).toString()));
    }

    public static JsonObject ObjectToJsonObj(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonObject.class);
    }

    public static JsonArray ObjectToJsonArr(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonArray.class);
    }

    public static Object JsonToObject(JsonElement json, Class<?> clazz) {
        return new Gson().fromJson(new Gson().toJson(json), clazz);
    }

    @SneakyThrows
    public static void save(Object object, String path) {
        Files.write(Paths.get(path), ObjectToJson(object).getBytes());
    }

    public static JsonObject getJsonObject(String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            return new Gson().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static JsonElement getObjectFromInternalPath(String path, JsonObject json) {
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
    public static void load(Class<?> clazz, String path) {
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

    public static void reloadConfig() {
        Bot.getBot().getConfigMap().forEach(ConfigUtil::load);
    }
}

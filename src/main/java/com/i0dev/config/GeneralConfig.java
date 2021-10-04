package com.i0dev.config;

import com.google.gson.JsonObject;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class GeneralConfig {

    public static GeneralConfig instance = new GeneralConfig();

    public static GeneralConfig get() {
        return instance;
    }

    //
    // Configuration starts
    //

    String discordToken = "Enter your token here!";
    String tebexSecret = "Enter if you want to use tebex";

    String activity = "Rendering nodes...";
    String activityType = "watching";
    List<String> prefixes = Collections.singletonList(".");
    List<Long> allowedGuilds = Collections.singletonList(0L);

    boolean useDatabase = false;
    String dbName = "DiscordBot";
    String dbAddress = "localhost";
    long dbPort = 3306;
    String dbUsername = "username";
    String dbPassword = "password";

    String successColor = "#27ae5f";
    String failureColor = "#cd3939";
    String normalColor = "#2f3136";

    List<Long> liteAllowed = new ArrayList<>();
    List<Long> strictAllowed = new ArrayList<>();

    //channel
    long logChannel = 766330396832432150L;
    long punishmentLogChannel = 766330396832432150L;

    //msg
    String message_invalidUser = "Cannot find that user.";
    String message_invalidNumber = "Cannot find that number.";
    String message_invalidRole = "Cannot find that role.";
    String message_invalidChannel = "Cannot find that channel.";

}

package com.i0dev.utility;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import net.dv8tion.jda.api.entities.TextChannel;

public class LogUtil {

    static boolean debug = true;

    public static void debug(String message) {
        if (debug)
            System.out.println("DEBUG: " + message);
    }

    public static void warn(String message) {
        System.out.println("WARN: " + message);
    }

    public static void severe(String message) {
        System.out.println("SEVERE: " + message);
    }

    public static void log(String message) {
        System.out.println("BOT: " + message);
    }

    public static void logDiscord(EmbedMaker embedMaker) {
        TextChannel channel = Bot.getJda().getTextChannelById(GeneralConfig.get().getLogChannel());
        if (channel == null) return;
        if (embedMaker.getAuthorName() == null)
            embedMaker.setAuthorName("Discord Log");
        if (embedMaker.getAuthorImg() == null)
            if (embedMaker.getAuthor() == null)
                embedMaker.setAuthorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl());
            else embedMaker.setAuthorImg(embedMaker.getAuthor().getEffectiveAvatarUrl());

        channel.sendMessageEmbeds(EmbedMaker.create(embedMaker)).queue();
    }
}

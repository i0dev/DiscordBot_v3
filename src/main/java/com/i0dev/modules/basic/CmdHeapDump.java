package com.i0dev.modules.basic;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.TimeUtil;
import com.i0dev.utility.Utility;

import java.lang.management.ManagementFactory;

public class CmdHeapDump extends DiscordCommand {


    @CommandData(commandID = "cmd_heapDump", identifier = "Heap Dump", messageLength = 1)
    public static void run(CommandEvent e) {
        StringBuilder desc = new StringBuilder();
        desc.append("Free Memory: ").append("`").append(Utility.numberFormat.format(Utility.runtime.freeMemory() / 1024L / 1024L)).append(" MB`").append("\n");
        desc.append("Used Memory: ").append("`").append(Utility.numberFormat.format(((Utility.runtime.maxMemory() - Utility.runtime.freeMemory()) / 1024L / 1024L))).append(" MB`").append("\n");
        desc.append("Max Memory: ").append("`").append(Utility.numberFormat.format(Utility.runtime.maxMemory() / 1024L / 1024L)).append(" MB`").append("\n");
        desc.append("Available Processors: ").append("`").append(Utility.numberFormat.format(Utility.runtime.availableProcessors())).append("`").append("\n");
        desc.append("System Load Average: ").append("`").append(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage()).append("%`").append("\n");
        desc.append("Uptime: ").append(TimeUtil.formatTime(System.currentTimeMillis() - Bot.startupTime)).append("\n");
        if (Bot.pluginMode) {
            desc.append("\n**__Minecraft Server Information:__**\n");
            desc.append("**Version:** ").append("`").append(com.i0dev.BotPlugin.server.getVersion()).append("`").append("\n");
            desc.append("**Online Players:** ").append("`").append(com.i0dev.BotPlugin.server.getOnlineCount()).append(" / ").append(com.i0dev.BotPlugin.server.getConfig().getPlayerLimit()).append("`").append("\n");
        }

        e.reply(EmbedMaker.builder()
                        .authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl())
                        .authorName("Runtime Information")
                .content(desc.toString()).author(e.getAuthor()).build());
    }
}
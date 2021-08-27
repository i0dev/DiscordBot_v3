package com.i0dev.modules.basic;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.utility.APIUtil;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.PlaceholderUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.MessageEmbed;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.text.NumberFormat;

public class CmdHeapDump extends DiscordCommand {


    @CommandData(commandID = "cmd_heapDump", identifier = "Heap Dump", messageLength = 1)
    public static void run(CommandEvent e) {
        SystemInfo info = new SystemInfo();
        HardwareAbstractionLayer hardware = info.getHardware();
        OperatingSystem os = info.getOperatingSystem();
        GlobalMemory memory = hardware.getMemory();
        CentralProcessor processor = hardware.getProcessor();
        NumberFormat f = Utility.numberFormat;

        StringBuilder memoryField = new StringBuilder();
        memoryField.append("Free Memory: ").append("`").append(f.format(memory.getAvailable() / 1024 / 1024)).append(" MB`").append("\n");
        memoryField.append("Used Memory: ").append("`").append(f.format(memory.getVirtualMemory().getVirtualInUse() / 1024 / 1024)).append(" MB`").append("\n");
        memoryField.append("Max Memory: ").append("`").append(f.format(memory.getTotal() / 1024 / 1024)).append(" MB`").append("\n");

        StringBuilder proccessorField = new StringBuilder();
        proccessorField.append("Logical Processors: `").append(processor.getLogicalProcessorCount()).append("`\n");
        proccessorField.append("CPU Identifier: `").append(processor.getProcessorIdentifier().getName()).append("`\n");
        StringBuilder miscField = new StringBuilder();
        miscField.append("Startup Time: <t:").append(Bot.bot.getStartupTime() / 1000).append(":R>\n");
        long start = System.currentTimeMillis();
        APIUtil.getAuthentication("999");
        long end = System.currentTimeMillis();
        miscField.append("i0dev API Latency: `").append(end - start).append("ms`\n");
        miscField.append("Operating System: `").append(os.getManufacturer()).append(" ").append(os.getFamily()).append(" ").append(os.getVersionInfo().getVersion()).append(" ").append(os.getVersionInfo().getCodeName()).append(" x").append(os.getBitness()).append("`\n");
        miscField.append("Discord JDA Version: `").append(JDAInfo.VERSION).append("`\n");
        miscField.append("DiscordBot Version: `").append(PlaceholderUtil.convert("{version}", null, null)).append("`\n");

        StringBuilder minecraftField = new StringBuilder();
        if (Bot.getBot().isPluginMode()) {
            minecraftField.append("Version: ").append("`").append(com.i0dev.BotPlugin.server.getName() + " v" + com.i0dev.BotPlugin.server.getVersion()).append("`").append("\n");
            minecraftField.append("Online Players: ").append("`").append(com.i0dev.BotPlugin.server.getOnlineCount()).append("/").append(com.i0dev.BotPlugin.server.getConfig().getPlayerLimit()).append("`").append("\n");
        } else {
            minecraftField.append("`Not in plugin mode`");
        }

        e.reply(EmbedMaker.builder()
                .authorImg(Bot.getBot().getJda().getSelfUser().getEffectiveAvatarUrl())
                .authorName("Runtime Information")
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("Memory", memoryField.toString(), true),
                        new MessageEmbed.Field("Processor", proccessorField.toString(), false),
                        new MessageEmbed.Field("Other", miscField.toString(), true),
                        new MessageEmbed.Field("Minecraft", minecraftField.toString(), false)
                })
                .author(e.getAuthor()).build());
    }
}
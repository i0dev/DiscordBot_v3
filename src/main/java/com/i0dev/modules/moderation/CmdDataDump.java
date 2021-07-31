package com.i0dev.modules.moderation;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.Utility;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.UUID;

public class CmdDataDump extends DiscordCommand {

    @SneakyThrows
    @CommandData(commandID = "cmd_dataDump", identifier = "DataDump", minMessageLength = 1)
    public static void run(CommandEvent e) {
        File file = new File(Bot.getMainFolder() + "/DataDump_" + UUID.randomUUID() + ".txt");
        StringBuilder toFile = new StringBuilder();
        toFile.append("DataDump made by: ").append(e.getAuthor().getAsTag()).append(" (").append(e.getAuthor().getId()).append(")\n");
        toFile.append("TimeStamp: ").append(Utility.formatDate(ZonedDateTime.now())).append("\n\n");
        toFile.append("Total Users: ").append(e.getJda().getUsers().size()).append("\n\n");
        toFile.append(" - Discord ID -    | - Discord Tag -\n\n");
        e.getJda().getUsers().forEach(member -> toFile.append(member.getId()).append(" | ").append(member.getAsTag()).append("\n"));
        Files.write(Paths.get(file.getAbsolutePath()), toFile.toString().getBytes());
        e.getAuthor().openPrivateChannel().complete().sendFile(file).queue();
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("Check your dms for the exported file.").build());
    }

}

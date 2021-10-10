package com.i0dev.modules.moderation;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.MessageUtil;
import com.i0dev.utility.Utility;

public class CmdRunCmd extends DiscordCommand {
    @CommandData(commandID = "cmd_runCmd", usage = "<server> <cmd>", identifier = "Run Command", minMessageLength = 3, requirePluginMode = true)
    public static void run(CommandEvent e) {
        String server = e.getOffsetSplit().get(1);
        String command = Utility.remainingArgFormatter(e.getOffsetSplit(), 2);
        if (command.startsWith("/")) command.substring(1);
        MessageUtil.runCommandOnServer("server_command", server, command);

        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have ran the command `{cmd}` on the server *{server}*"
                .replace("{cmd}", "/" + command)
                .replace("{server}", server)
        ).build());

    }
}

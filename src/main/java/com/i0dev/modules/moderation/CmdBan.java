package com.i0dev.modules.moderation;

import com.i0dev.Bot;
import com.i0dev.object.CommandData;
import com.i0dev.object.CommandEvent;
import com.i0dev.object.DiscordCommand;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.net.DatagramPacket;

public class CmdBan extends DiscordCommand {

    public void load() {
        addOption("banInGame", true);
        addOption("command", "ban {ign} {reason}");
    }

    @CommandData(commandID = "cmd_ban", minMessageLength = 2, usage = "<user> [reason]", identifier = "Ban")
    public static void run(CommandEvent e) {
        Member member;
        if ((member = FindUtil.getMember(e.getSplit()[1], e.getMessage())) == null) return;
        DPlayer dPlayer = DPlayer.getDPlayer(member);
        String reason = Utility.remainingArgFormatter(e.getSplit(), 2);
        if (Utility.getBan(e.getGuild(), member.getUser()) != null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("{tag} is already banned.").user(member.getUser()).build());
            return;
        }

        if (Bot.getBot().isPluginMode() && dPlayer.isLinked() && getOption("banInGame").getAsBoolean()) {
            com.i0dev.BotPlugin.runCommand(getOption("command").getAsString().replace("{reason}", reason).replace("{ign}", dPlayer.getMinecraftIGN()));
        }

        e.getGuild().ban(member, 0, reason).queue();
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have banned {tag}").user(member.getUser()).build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} has banned {tag}\nReason: *{reason}*".replace("{reason}", reason)).user(member.getUser()).author(e.getAuthor()).build());
    }
}

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
        addOption("command", "ban {ign} {reason} {length}");
    }

    @CommandData(commandID = "cmd_ban", minMessageLength = 2, usage = "<user> [reason] [length]", identifier = "Ban")
    public static void run(CommandEvent e) {
        Member member;
        if ((member = FindUtil.getMember(e.getSplit()[1], e.getMessage())) == null) return;
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(member);
        String reason = Utility.remainingArgFormatter(e.getSplit(), 2);
        if (Utility.getBan(e.getGuild(), member.getUser()) != null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("{tag} is already banned.").user(member.getUser()).build());
            return;
        }

        long banLength = -1;
        String time = "";
        if (reason.contains("t:")) {
            for (String s : reason.split(" ")) {
                if (s.contains("t:")) {
                    time = s.substring(2);
                    break;
                }
            }

            int timeLength = 2 + time.length();
            reason = reason.substring(0, reason.length() - timeLength);

            long banFor = Utility.getTimeMilis(time);
            banLength = banFor;
            if ((banFor = Utility.getTimeMilis(time)) == -1) {
                e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Invalid time format. Enter in this format: `t:1w4m`, `t:1d4m2s` etc.").build());
                return;
            }

            dPlayer.setUnbanAtTime(banFor + System.currentTimeMillis());
        } else {
            dPlayer.setUnbanAtTime(-1);
        }
        dPlayer.setBanned(true);
        dPlayer.save();

        if (Bot.getBot().isPluginMode() && dPlayer.isLinked() && getOption("banInGame").getAsBoolean()) {
            com.i0dev.BotPlugin.runCommand(getOption("command").getAsString()
                    .replace("{length}", banLength == -1 ? "" : time)
                    .replace("{reason}", reason)
                    .replace("{ign}", dPlayer.getMinecraftIGN()));
        }
        reason = reason.equals("") ? "Nothing Provided" : reason;
        e.getGuild().ban(member, 0, reason).queue();
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(member.getUser()).content("You have banned {tag}\nReason: `{reason}`\nEnding: {length}"
                .replace("{length}", banLength == -1 ? "`Permanent`" : "<t:" + ((banLength + System.currentTimeMillis()) / 1000) + ":R>")
                .replace("{reason}", reason)).build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} has banned **{tag}**\nReason: `{reason}`\nEnding: {length}"
                        .replace("{reason}", reason)
                        .replace("{length}", banLength == -1 ? "`Permanent`" : "<t:" + ((banLength + System.currentTimeMillis()) / 1000) + ":R>"))
                .author(e.getAuthor()).user(member.getUser()).build());
    }
}

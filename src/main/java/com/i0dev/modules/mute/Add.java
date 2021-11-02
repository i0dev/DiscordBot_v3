package com.i0dev.modules.mute;

import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.FindUtil;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class Add extends SuperDiscordCommand {

    public void load() {
        addOption("muteInGame", true);
        addOption("command", "mute {ign} {reason} {length}");
    }

    @CommandData(commandID = "add", identifier = "Mute Add", minMessageLength = 2, usage = "<user> [reason] [length]", parentClass = MuteManager.class)
    public static void run(CommandEvent e) {
        User user;
        if ((user = FindUtil.getUser(e.getOffsetSplit().get(1), e.getMessage())) == null) return;
        String reason = Utility.remainingArgFormatter(e.getOffsetSplit(), 2);
        Member member = e.getGuild().getMember(user);
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(user);

        if (MuteManager.mutedRole == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Muted role is not yet setup. You can create one with {prefix}mute create").build());
            return;
        }

        if (member.getRoles().contains(MuteManager.mutedRole)) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).user(user).content("{tag} is already muted.").build());
            return;
        }

        long muteLength = -1;
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

            long muteFor = Utility.getTimeMilis(time);
            muteLength = muteFor;
            if ((muteFor = Utility.getTimeMilis(time)) == -1) {
                e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Invalid time format. Enter in this format: `t:1w4m`, `t:1d4m2s` etc.").build());
                return;
            }

            dPlayer.setUnmuteAtTime(muteFor + System.currentTimeMillis());
        } else {
            dPlayer.setUnmuteAtTime(-1);
        }
        dPlayer.save();


        if (Bot.getBot().isPluginMode() && dPlayer.isLinked() && getOption("muteInGame").getAsBoolean()) {
            com.i0dev.BotPlugin.runCommand(getOption("command").getAsString()
                    .replace("{reason}", reason)
                    .replace("{length}", muteLength == -1 ? "" : time)
                    .replace("{ign}", dPlayer.getMinecraftIGN()));
        }

        new RoleQueueObject(user.getIdLong(), MuteManager.mutedRole.getIdLong(), Type.ADD_ROLE).add();
        dPlayer.setMuted(true);
        dPlayer.save();
        reason = reason.equals("") ? "Nothing Provided" : reason;
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).user(user).content("You have muted {tag}\nReason: `{reason}`\nEnding: {length}"
                .replace("{length}", muteLength == -1 ? "`Permanent`" : "<t:" + ((muteLength + System.currentTimeMillis()) / 1000) + ":R>")
                .replace("{reason}", reason)).build());
        LogUtil.logDiscord(EmbedMaker.builder().content("{authorTag} has muted **{tag}**\nReason: `{reason}`\nEnding: {length}"
                        .replace("{reason}", reason)
                        .replace("{length}", muteLength == -1 ? "`Permanent`" : "<t:" + ((muteLength + System.currentTimeMillis()) / 1000) + ":R>"))
                .author(e.getAuthor()).user(user).build());
    }
}

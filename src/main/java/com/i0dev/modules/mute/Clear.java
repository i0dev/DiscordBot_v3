package com.i0dev.modules.mute;

import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import net.dv8tion.jda.api.entities.Member;

public class Clear extends SuperDiscordCommand {

    public void load() {
        addOption("unmuteInGame", true);
        addOption("command", "unmute {ign}");
    }

    @CommandData(commandID = "clear", messageLength = 1, identifier = "Mute Clear", parentClass = MuteManager.class)
    public static void run(CommandEvent e) {
        if (MuteManager.mutedRole == null) {
            e.reply(EmbedMaker.builder().embedColor(EmbedColor.FAILURE).content("Muted role is not yet setup. You can create one with {prefix}mute create").build());
            return;
        }

        for (Member member : e.getGuild().getMembers()) {
            if (member.getRoles().contains(MuteManager.mutedRole)) {
                DPlayer dPlayer = DPlayer.getDPlayer(member);
                dPlayer.setMuted(false);
                dPlayer.save();
                new RoleQueueObject(member.getIdLong(), MuteManager.mutedRole.getIdLong(), Type.REMOVE_ROLE).add();
                if (Bot.isPluginMode() && dPlayer.isLinked() && getOption("unmuteInGame").getAsBoolean()) {
                    com.i0dev.BotPlugin.runCommand(getOption("command").getAsString().replace("{ign}", dPlayer.getMinecraftIGN()));
                }
            }
        }
        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully cleared all muted members").build());
        LogUtil.logDiscord(EmbedMaker.builder().user(e.getAuthor()).content("{tag} has cleared all muted members").build());
    }
}

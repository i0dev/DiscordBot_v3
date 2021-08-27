package com.i0dev.modules.mute;

import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.managers.SQLManager;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.Utility;
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
                DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(member);
                dPlayer.setMuted(false);
                dPlayer.save();
                new RoleQueueObject(member.getIdLong(), MuteManager.mutedRole.getIdLong(), Type.REMOVE_ROLE).add();
                if (Bot.getBot().isPluginMode() && dPlayer.isLinked() && getOption("unmuteInGame").getAsBoolean()) {
                    com.i0dev.BotPlugin.runCommand(getOption("command").getAsString().replace("{ign}", dPlayer.getMinecraftIGN()));
                }
            }
        }


        Bot.getBot().getManager(SQLManager.class).getListWhere(DPlayer.class.getSimpleName(), "muted", "1", DPlayer.class, "discordID").forEach(o -> {
            DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(((DPlayer) o).getDiscordID());
            dPlayer.setMuted(false);
            if (!Utility.hasRoleAlready(MuteManager.mutedRole.getIdLong(), dPlayer.getDiscordID()) && MuteManager.mutedRole.getGuild().getMemberById(dPlayer.getDiscordID()) != null)
                new RoleQueueObject(dPlayer.getDiscordID(), MuteManager.mutedRole.getIdLong(), Type.REMOVE_ROLE).add();
            if (Bot.getBot().isPluginMode() && dPlayer.isLinked() && getOption("unmuteInGame").getAsBoolean()) {
                com.i0dev.BotPlugin.runCommand(getOption("command").getAsString().replace("{ign}", dPlayer.getMinecraftIGN()));
            }
            dPlayer.save();
        });


        e.reply(EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).content("You have successfully cleared all muted members").build());
        LogUtil.logDiscord(EmbedMaker.builder().user(e.getAuthor()).content("{tag} has cleared all muted members").build());
    }
}

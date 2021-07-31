package com.i0dev.modules.misc;

import com.i0dev.Bot;
import com.i0dev.BotPlugin;
import com.i0dev.blacklist.api.PlayerBlacklistEvent;
import com.i0dev.config.MiscConfig;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.APIUtil;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import litebans.api.Entry;
import litebans.api.Events;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InGamePunishmentLogs implements Listener {

    public static void initialize() {

        List<String> names = new ArrayList<>();
        BotPlugin.server.getPluginManager().getPlugins().forEach(plugin -> names.add(plugin.getClass().getName()));
        if (names.contains("litebans.BungeePlugin")) {
            registerEvents();
        }

        if (names.contains("com.i0dev.blacklist.Blacklist"))
            BotPlugin.server.getPluginManager().registerListener(BotPlugin.get(), new InGamePunishmentLogs());
    }

    public static void registerEvents() {
        if (!MiscConfig.instance.ingame_punishment_logs) return;
        LogUtil.log("Registered plugin LiteBans as a listener for punishments.");

        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry e) {
                APIUtil.refreshAPICache(e.getUuid());

                DPlayer dPlayer = DPlayer.getDPlayerFromUUID(e.getUuid());
                String authorImg = dPlayer == null ? Bot.getJda().getSelfUser().getEffectiveAvatarUrl() : Bot.getJda().retrieveUserById(dPlayer.getDiscordID()).complete().getEffectiveAvatarUrl();

                StringBuilder msg = new StringBuilder();
                msg.append("Punished IGN: `").append(APIUtil.getIGNFromUUID(e.getUuid())).append("`\n");
                msg.append("Punished UUID: `").append(e.getUuid()).append("`\n");
                msg.append("Staff IGN: `").append(e.getExecutorName()).append("`\n");
                msg.append("Reason: `").append(e.getReason()).append("`\n");
                msg.append("Duration: `").append(e.getDurationString()).append("`\n");

                LogUtil.logDiscord(EmbedMaker.builder()
                        .thumbnail("https://crafatar.com/renders/body/" + e.getUuid())
                        .field(new MessageEmbed.Field("Punish Type: " + e.getType(), msg.toString(), false))
                        .authorImg(authorImg).authorName("In Game Punishment Log").build());
            }
        });
    }

    @EventHandler
    public void onBlacklist(PlayerBlacklistEvent e) {
        if (!MiscConfig.instance.ingame_punishment_logs) return;
        LogUtil.log("Registered plugin Blacklist as a listener for punishments.");

        UUID uuid = APIUtil.convertUUID(e.getPlayerUUID());
        APIUtil.refreshAPICache(uuid.toString());

        DPlayer dPlayer = DPlayer.getDPlayerFromUUID(e.getPlayerUUID());
        String authorImg = dPlayer == null ? Bot.getJda().getSelfUser().getEffectiveAvatarUrl() : Bot.getJda().retrieveUserById(dPlayer.getDiscordID()).complete().getEffectiveAvatarUrl();

        StringBuilder msg = new StringBuilder();
        msg.append("Punished IGN: `").append(e.getPlayerIGN()).append("`\n");
        msg.append("Punished UUID: `").append(uuid).append("`\n");
        msg.append("Staff IGN: `").append(e.getStaffIGN()).append("`\n");
        msg.append("Reason: `").append(e.getReason()).append("`\n");
        msg.append("Duration: `").append("Forever").append("`\n");

        LogUtil.logDiscord(EmbedMaker.builder()
                .thumbnail("https://crafatar.com/renders/body/" + e.getPlayerUUID())
                .field(new MessageEmbed.Field("Punish Type: Blacklist", msg.toString(), false))
                .authorImg(authorImg).authorName("In Game Punishment Log").build());
    }

}

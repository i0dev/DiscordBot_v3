package com.i0dev.modules.linking;

import com.i0dev.Bot;
import com.i0dev.object.discordLinking.DPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;

public class EventHandler implements Listener {
    @net.md_5.bungee.event.EventHandler
    public void onJoin(PostLoginEvent e) {
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayerFromUUID(e.getPlayer().getUniqueId().toString());
        if (dPlayer == null) return;
        if (dPlayer.isLinked()) return;
        RoleRefreshHandler.RefreshUserRank(dPlayer);
    }
}

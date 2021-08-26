package com.i0dev.modules.twoFactorAuthentication;

import com.i0dev.Bot;
import com.i0dev.BotPlugin;
import com.i0dev.config.MiscConfig;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.EncryptionUtil;
import com.i0dev.utility.MessageUtil;
import com.i0dev.utility.Utility;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TwoFactorAuthentication implements Listener {
    @Setter
    @Getter
    private static Map<UUID, String> ipCache = new HashMap<>();

    public static boolean isCodeValid(String code) {
        for (TwoFactor twoFactor : Cache.getInstance().getTwoFactorCache()) {
            if (twoFactor.getCode().equalsIgnoreCase(code)) return true;
        }
        return false;
    }

    public static TwoFactor getObject(ProxiedPlayer player) {
        for (TwoFactor twoFactor : Cache.getInstance().getTwoFactorCache()) {
            if (twoFactor.getPlayer().equals(player)) return twoFactor;
        }
        return null;
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        TwoFactor twoFactor = getObject(e.getPlayer());
        if (twoFactor == null) return;
        Cache.getInstance().getTwoFactorCache().remove(twoFactor);
        Cache.getInstance().getCache().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        if (!MiscConfig.get().ingame_2fa_enabled) return;
        if (!e.getPlayer().hasPermission(MiscConfig.get().ingame_2fa_permission)) return;
        if (getIpCache().containsKey(e.getPlayer().getUniqueId())
                && EncryptionUtil.encrypt(Arrays.toString(e.getPlayer().getAddress().getAddress().getAddress()), e.getPlayer().getUniqueId().toString()).equalsIgnoreCase(getIpCache().get(e.getPlayer().getUniqueId()))) {
            return;
        }
        DPlayer dPlayer = DPlayer.getDPlayerFromUUID(e.getPlayer().getUniqueId().toString());
        if (dPlayer == null) return;
        TwoFactor preTwoF = getObject(e.getPlayer());
        MessageUtil.sendMessageInGame(e.getPlayer(), "&7Two Factor Authentication required. Please use the code the Bot.getBot() sent you in Direct Messages. &c/2fa <code>");
        Cache.getInstance().getCache().add(e.getPlayer().getUniqueId());
        User user;
        TwoFactor useThisTF;
        if (preTwoF != null) {
            user = Bot.getBot().getJda().retrieveUserById(dPlayer.getDiscordID()).complete();
            useThisTF = preTwoF;
        } else {
            TwoFactor twoFactor = new TwoFactor();
            twoFactor.setCode(Utility.GenerateRandomString(5));
            twoFactor.setPlayer(e.getPlayer());
            user = Bot.getBot().getJda().retrieveUserById(dPlayer.getDiscordID()).complete();
            twoFactor.setUser(user);
            twoFactor.addToCache();
            useThisTF = twoFactor;
        }
        MessageUtil.sendPrivateMessage(null, user, EmbedMaker.builder().embedColor(EmbedColor.SUCCESS).authorName("Secure TwoFactorAuth").authorImg(user.getEffectiveAvatarUrl()).content("Your Two Factor Authentication code is: `{code}`\n`/2fa {code}`".replace("{code}", useThisTF.getCode())).build());
        BotPlugin.server.getScheduler().runAsync(BotPlugin.get(), () -> {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ee) {
                ee.printStackTrace();
            }

            MessageUtil.sendMessageInGame(e.getPlayer(), "&7You need to enter your 2fa code before you can do anything on the network.");
            MessageUtil.sendMessageInGame(e.getPlayer(), "&7You need to enter your 2fa code before you can do anything on the network.");
            MessageUtil.sendMessageInGame(e.getPlayer(), "&7You need to enter your 2fa code before you can do anything on the network.");
            MessageUtil.sendMessageInGame(e.getPlayer(), "&7You need to enter your 2fa code before you can do anything on the network.");
        });

    }


    boolean isOnList(ProxiedPlayer player) {
        return Cache.getInstance().getCache().contains(player.getUniqueId());
    }

    @EventHandler
    public void inventoryEvent(ServerSwitchEvent e) {
        ProxiedPlayer p = e.getPlayer();
        if (!isOnList(p)) return;
        if (e.getFrom() == null) return;
        p.disconnect(Utility.c("&cYou need to enter 2fa in the hub before you can connect to another server."));
    }


    @EventHandler
    public void chat(ChatEvent e) {
        ProxiedPlayer p = ((ProxiedPlayer) e.getSender());
        if (isOnList(p)) {
            if (e.getMessage().startsWith("/2fa")) return;
            e.setCancelled(true);
            MessageUtil.sendMessageInGame(p, "&7Two Factor Authentication required. Please use the code the Bot.getBot() sent you in Direct Messages. &c/2fa <code>");
        }
    }
}

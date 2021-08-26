package com.i0dev.object.managers;

import com.i0dev.Bot;
import com.i0dev.DiscordBot;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.LogUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class DPlayerManager extends Manager {

    final Set<DPlayer> cachedUsers = new HashSet<>();

    public DPlayerManager(DiscordBot bot) {
        super(bot);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void deinitialize() {
        cachedUsers.forEach(DPlayer::save);
        cachedUsers.clear();
    }

    public DPlayer getDPlayer(ISnowflake user) {
        return getDPlayer(user.getIdLong());
    }

    public DPlayer getDPlayer(long discordID) {
        return cachedUsers.stream().filter(dPlayer -> dPlayer.getDiscordID() == discordID).findAny().orElseGet(() -> {
            DPlayer user;
            User discordUser = Bot.bot.getJda().getUserById(discordID);
            user = (DPlayer) Bot.getBot().getManager(SQLManager.class).getObject("discordID", discordID + "", DPlayer.class);
            if (user == null) {
                LogUtil.debug("Creating new DPlayer Object for user: [" + discordID + "]-[" + (discordUser == null ? "Not In Discord" : discordUser.getAsTag()) + "]");
                user = new DPlayer(discordID);
                user.addToCache();
                user.save();
                return user;
            }
            LogUtil.debug("Loading DPlayer Object from user: [" + discordID + "]-[" + (discordUser == null ? "Not In Discord" : discordUser.getAsTag()) + "]");
            user.addToCache();
            return user;
        });
    }

    public DPlayer getDPlayerFromUUID(String uuid) {
        return cachedUsers.stream().filter(dPlayer -> Objects.equals(dPlayer.getMinecraftUUID(), uuid)).findAny().orElseGet(() -> (DPlayer) Bot.getBot().getManager(SQLManager.class).getObject("minecraftUUID", uuid, DPlayer.class));
    }

    public DPlayer getDPlayerFromIGN(String ign) {
        return cachedUsers.stream().filter(dPlayer -> Objects.equals(dPlayer.getMinecraftIGN(), ign)).findAny().orElseGet(() -> (DPlayer) Bot.getBot().getManager(SQLManager.class).getObject("minecraftIGN", ign, DPlayer.class));
    }

    public Runnable taskClearCache = () -> {
        long time = 1000L * 60L * 30L; // 30 minutes
        List<DPlayer> toRemove = new ArrayList<>();
        cachedUsers.stream().filter(dPlayer -> dPlayer.getLastUsedTime() + time < System.currentTimeMillis()).forEach(toRemove::add);
        if (toRemove.isEmpty()) return;
        LogUtil.debug("Removed " + toRemove.size() + " cached DPlayer objects, with " + (cachedUsers.size() - toRemove.size() + " still cached."));
        toRemove.forEach(DPlayer::save);
        toRemove.forEach(DPlayer::removeFromCache);
    };

}

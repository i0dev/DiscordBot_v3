package com.i0dev.managers;

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

    public DPlayerManager(DiscordBot bot) {
        super(bot);
    }

    public DPlayer getDPlayer(ISnowflake user) {
        return getDPlayer(user.getIdLong());
    }

    public DPlayer getDPlayer(long discordID) {
        return (DPlayer) Bot.getBot().getManager(SQLManager.class).getObject("discordID", discordID + "", DPlayer.class);
    }

    public DPlayer getDPlayerFromUUID(String uuid) {
        return (DPlayer) Bot.getBot().getManager(SQLManager.class).getObject("minecraftUUID", uuid, DPlayer.class);
    }

    public DPlayer getDPlayerFromIGN(String ign) {
        return (DPlayer) Bot.getBot().getManager(SQLManager.class).getObject("minecraftIGN", ign, DPlayer.class);
    }
}

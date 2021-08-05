package com.i0dev.modules.points;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.SQLUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscordPoints {
    long discordID;
    double points;

    public void save() {
        SQLUtil.updateTable(this, "discordID", this.getDiscordID() + "");
    }

    public void removeFromCache() {
        getCachedPoints().remove(this);
    }

    public DiscordPoints addToCache() {
        getCachedPoints().add(this);
        return this;
    }

    @Getter
    public transient static final List<DiscordPoints> cachedPoints = new ArrayList<>();

    public static DiscordPoints getDPlayer(ISnowflake user) {
        return getDiscordPoints(user.getIdLong());
    }

    public static DiscordPoints getDiscordPoints(long discordID) {
        return cachedPoints.stream().filter(dPoint -> dPoint.getDiscordID() == discordID).findAny().orElseGet(() -> {
            DiscordPoints user;
            LogUtil.debug("Creating new DiscordPoint Object for user: " + discordID);
            user = (DiscordPoints) SQLUtil.getObject("discordID", discordID + "", DiscordPoints.class);
            if (user == null)
                user = new DiscordPoints(discordID, 0.0D);
            user.addToCache();
            return user;
        });
    }
}

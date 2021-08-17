package com.i0dev.object.discordLinking;

import com.i0dev.modules.linking.LinkData;
import com.i0dev.object.RoleQueueObject;
import com.i0dev.object.Type;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.SQLUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
public class DPlayer {

    // Internal
    public transient long lastUsedTime = System.currentTimeMillis();

    // Identifiers
    long discordID;
    long lastUpdatedMillis = System.currentTimeMillis();
    long lastBoostTime = 0;

    // Other
    long invitedByDiscordID = 0;

    // Values
    long ticketsClosed = 0;
    long messages = 0;
    long invites = 0;
    long warnings = 0;
    long boosts = 0;
    long boostCredits = 0;

    // Statuses
    boolean blacklisted = false;
    boolean muted = false;

    // Transient
    transient String minecraftSkin = !isLinked() ? null : "https://crafatar.com/renders/body/" + getMinecraftUUID() + ".png?scale=7&overlay.png";


    public DPlayer(long discordID) {
        this.discordID = discordID;
    }

    @Deprecated
    public DPlayer() {
    }

    public DPlayer used() {
        lastUsedTime = System.currentTimeMillis();
        return this;
    }

    public void addToCache() {
        getCachedUsers().add(this);
        used();
    }

    public void save() {
        lastUpdatedMillis = System.currentTimeMillis();
        SQLUtil.updateTable(this, "discordID", this.getDiscordID() + "");
    }

    public void removeFromCache() {
        getCachedUsers().remove(this);
        save();
    }

    public LinkData getLinkData() {
        return LinkData.getLinkData(getDiscordID());
    }

    public String getMinecraftIGN() {
        return getLinkData().getMinecraftIGN();
    }

    public String getMinecraftUUID() {
        return getLinkData().getMinecraftUUID();
    }

    public boolean isLinked() {
        return getLinkData().isLinked();
    }

    public void increase(DPlayerFieldType type, long... values) {
        long value = 1;
        if (values.length > 0) {
            value = 0;
            for (long l : values) {
                value += l;
            }
        }
        switch (type) {
            case TICKETS_CLOSED:
                this.setTicketsClosed(this.getTicketsClosed() + value);
                break;
            case INVITES:
                this.setInvites(this.getInvites() + value);
                break;
            case MESSAGES:
                this.setMessages(this.getMessages() + value);
                break;
            case WARNINGS:
                this.setWarnings(this.getWarnings() + value);
                break;
            case BOOSTS:
                this.setBoosts(this.getBoosts() + value);
                break;
            case BOOST_CREDITS:
                this.setBoostCredits(this.getBoostCredits() + value);
                break;
        }
        this.save();
        this.used();
    }

    public void decrease(DPlayerFieldType type, long... values) {
        long value = 1;
        if (values.length > 0) {
            value = 0;
            for (long l : values) {
                value += l;
            }
        }
        switch (type) {
            case TICKETS_CLOSED:
                this.setTicketsClosed(this.getTicketsClosed() - value);
                break;
            case INVITES:
                this.setInvites(this.getInvites() - value);
                break;
            case WARNINGS:
                this.setWarnings(this.getWarnings() - value);
                break;
            case MESSAGES:
                this.setMessages(this.getMessages() - value);
                break;
            case BOOSTS:
                this.setBoosts(this.getBoosts() - value);
                break;
            case BOOST_CREDITS:
                this.setBoostCredits(this.getBoostCredits() - value);
                break;
        }
        this.save();
    }

    public void clear(DPlayerFieldType type) {
        switch (type) {
            case TICKETS_CLOSED:
                this.setTicketsClosed(0);
                break;
            case INVITES:
                this.setInvites(0);
                break;
            case WARNINGS:
                this.setWarnings(0);
                break;
            case BOOSTS:
                this.setBoosts(0);
                break;
            case BOOST_CREDITS:
                this.setBoostCredits(0);
                break;
            case MESSAGES:
                this.setMessages(0);
                break;
            case BLACKLISTED:
                this.setBlacklisted(false);
                break;
            case LINKED:
                LinkData obj = LinkData.getLinkData(discordID);
                obj.setLinkCode("");
                obj.setLinkedTime(0);
                obj.setLinked(false);
                obj.setMinecraftUUID("");
                obj.setMinecraftIGN("");
                obj.save();
                break;
        }
        this.save();
    }

    public void link(String code, String ign, String UUID) {
        LinkData obj = LinkData.getLinkData(discordID);
        obj.setLinked(true);
        obj.setLinkCode(code);
        obj.setMinecraftUUID(UUID);
        obj.setLinkedTime(System.currentTimeMillis());
        obj.setLinked(true);
        obj.setMinecraftIGN(ign);
        obj.save();
    }

    public void giveRole(long roleID) {
        new RoleQueueObject(getDiscordID(), roleID, Type.ADD_ROLE).add();
    }

    public void removeRole(long roleID) {
        new RoleQueueObject(getDiscordID(), roleID, Type.REMOVE_ROLE).add();
    }

    //
    //   Static
    //

    @Getter
    public transient static final List<DPlayer> cachedUsers = new ArrayList<>();

    public static void loadAll() {
        SQLUtil.getAllObjects(DPlayer.class.getSimpleName(), "discordID", DPlayer.class).forEach(o -> {
            ((DPlayer) o).addToCache();
        });
    }

    public static DPlayer getDPlayer(ISnowflake user) {
        return getDPlayer(user.getIdLong());
    }

    public static DPlayer getDPlayer(long discordID) {
        return cachedUsers.stream().filter(dPlayer -> dPlayer.getDiscordID() == discordID).findAny().orElseGet(() -> {
            DPlayer user;
            LogUtil.debug("Creating new DPlayer Object for user: " + discordID);
            user = (DPlayer) SQLUtil.getObject("discordID", discordID + "", DPlayer.class);
            if (user == null)
                user = new DPlayer(discordID);
            user.addToCache();
            user.save();
            return user;
        });
    }

    public static DPlayer getDPlayerFromUUID(String uuid) {
        return cachedUsers.stream().filter(dPlayer -> Objects.equals(dPlayer.getMinecraftUUID(), uuid)).findAny().orElseGet(() -> (DPlayer) SQLUtil.getAllObjects(DPlayer.class.getSimpleName(), "discordID", DPlayer.class).stream().filter(ob -> {
            DPlayer dPlayer = ((DPlayer) ob);
            return dPlayer.getMinecraftUUID().equals(uuid);
        }).findAny().orElse(null));
    }

    public static DPlayer getDPlayerFromIGN(String ign) {
        return cachedUsers.stream().filter(dPlayer -> Objects.equals(dPlayer.getMinecraftIGN(), ign)).findAny().orElseGet(() -> (DPlayer) SQLUtil.getAllObjects(DPlayer.class.getSimpleName(), "discordID", DPlayer.class).stream().filter(ob -> {
            DPlayer dPlayer = ((DPlayer) ob);
            return dPlayer.getMinecraftIGN().equals(ign);
        }).findAny().orElse(null));
    }

    public transient static Runnable taskClearCache = () -> {
        long time = 1000L * 60L * 30L; // 30 minutes
        List<DPlayer> toRemove = new ArrayList<>();
        cachedUsers.stream().filter(dPlayer -> dPlayer.getLastUsedTime() + time < System.currentTimeMillis()).forEach(toRemove::add);
        LogUtil.debug("Updated the DPlayer Cache and removed " + toRemove.size() + " cached objects, with a remaining total of: " + (cachedUsers.size() - toRemove.size()));
        toRemove.forEach(DPlayer::save);
        toRemove.forEach(DPlayer::removeFromCache);
    };

}

package com.i0dev.modules.blacklist;

import com.i0dev.utility.SQLUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Blacklist {
    long discordID;
    boolean blacklisted;

    public void save() {
        SQLUtil.updateTable(this, "discordID", this.getDiscordID() + "");
    }

    public static Blacklist getBlacklist(long discordID) {
        Blacklist obj;
        obj = (Blacklist) SQLUtil.getObject("discordID", discordID + "", Blacklist.class);
        if (obj == null)
            obj = new Blacklist(discordID, false);
        obj.save();
        return obj;
    }
}

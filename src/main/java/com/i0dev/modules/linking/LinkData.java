package com.i0dev.modules.linking;

import com.i0dev.modules.points.DiscordPoints;
import com.i0dev.utility.SQLUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LinkData {

    long discordID;

    String minecraftIGN = "";
    String minecraftUUID = "";
    boolean linked = false;


    public void save() {
        SQLUtil.updateTable(this, "discordID", this.getDiscordID() + "");
    }

    public static LinkData getLinkData(long discordID) {
        LinkData obj;
        obj = (LinkData) SQLUtil.getObject("discordID", discordID + "", LinkData.class);
        if (obj == null)
            obj = new LinkData(discordID, "", "", false);
        obj.save();
        return obj;
    }

}

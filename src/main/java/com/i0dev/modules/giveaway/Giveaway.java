package com.i0dev.modules.giveaway;

import com.i0dev.utility.SQLUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.ISnowflake;


@Getter
@NoArgsConstructor
@Setter
public class Giveaway {

    //key
    long messageID;

    long channelID, hostID, endTime, winnerAmount;
    String prize;
    boolean ended;

    public void save() {
        SQLUtil.updateTable(this, "messageID", this.getMessageID() + "");
    }

    public static Giveaway getGiveaway(String id) {
        return (Giveaway) SQLUtil.getObject("messageID", id, Giveaway.class);
    }

}
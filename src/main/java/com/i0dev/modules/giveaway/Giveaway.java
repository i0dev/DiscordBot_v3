package com.i0dev.modules.giveaway;

import com.i0dev.Bot;
import com.i0dev.object.managers.SQLManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
        Bot.getBot().getManager(SQLManager.class).updateTable(this, "messageID", this.getMessageID() + "");
    }

    public static Giveaway getGiveaway(String id) {
        return (Giveaway) Bot.getBot().getManager(SQLManager.class).getObject("messageID", id, Giveaway.class);
    }

}
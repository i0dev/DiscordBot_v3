package com.i0dev.modules.ticket;

import com.i0dev.utility.SQLUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.ISnowflake;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    //key
    long channelID;

    String ticketName;
    long ticketOwnerID, ticketNumber;
    boolean adminOnlyMode;

    public static Ticket getTicket(ISnowflake idAble) {
        return (Ticket) SQLUtil.getObject("channelID", idAble.getId(), Ticket.class);
    }

    public void save() {
        SQLUtil.updateTable(this, "channelID", this.getChannelID() + "");
    }

    public void remove() {
        SQLUtil.deleteFromTable(Ticket.class.getSimpleName(), "channelID", this.getChannelID() + "");
    }

}
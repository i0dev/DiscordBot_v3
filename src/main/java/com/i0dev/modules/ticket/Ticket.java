package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.managers.SQLManager;
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
        return (Ticket) Bot.getBot().getManager(SQLManager.class).getObject("channelID", idAble.getId(), Ticket.class);
    }

    public void save() {
        Bot.getBot().getManager(SQLManager.class).updateTable(this, "channelID", this.getChannelID() + "");
    }

    public void remove() {
        Bot.getBot().getManager(SQLManager.class).deleteFromTable(Ticket.class.getSimpleName(), "channelID", this.getChannelID() + "");
    }

}
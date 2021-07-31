package com.i0dev.modules.suggestion;

import com.i0dev.utility.SQLUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {

    //key
    long messageID;

    long userID, channelID;
    String suggestion;
    boolean accepted, rejected;


    public void save() {
        SQLUtil.updateTable(this, "messageID", this.getMessageID() + "");
    }

}
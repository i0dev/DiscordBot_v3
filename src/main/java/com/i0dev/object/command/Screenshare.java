package com.i0dev.object.command;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Screenshare {

    private String ign;
    private String reason;
    private Long punisherID;
    private String punisherTag;


    public Screenshare() {
        this.ign = "";
        this.reason = "";
        this.punisherID = 0L;
        this.punisherTag = "";

    }


}
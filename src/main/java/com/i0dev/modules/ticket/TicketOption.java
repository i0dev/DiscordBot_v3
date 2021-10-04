package com.i0dev.modules.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketOption {

    List<String> questions;
    long category;
    String channelName, emoji, displayName;
    boolean pingStaff, adminOnlyDefault;
    List<Long> rolesToPing;

}

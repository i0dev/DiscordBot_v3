package com.i0dev.modules.movement;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MovementObject {
    long mainRole;
    String displayName;
    List<Long> extraRoles;
    String luckPermsRank;
}

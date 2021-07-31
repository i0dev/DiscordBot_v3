package com.i0dev.modules.movement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MovementObject {
    long mainRole;
    String displayName;
    List<Long> extraRoles;
}

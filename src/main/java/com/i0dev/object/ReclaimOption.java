package com.i0dev.object;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class ReclaimOption {
    private String displayName;
    private String permission;
    private ArrayList<String> commands;
}

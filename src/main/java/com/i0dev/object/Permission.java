package com.i0dev.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    public boolean lite = false;
    public boolean strict = true;
    public boolean admin = false;

    public static Permission strict() {
        return new Permission(false, true, false);
    }

    public static Permission none() {
        return new Permission(false, false, false);
    }

    public static Permission admin() {
        return new Permission(false, false, true);
    }

    public static Permission lite() {
        return new Permission(true, false, false);
    }
}

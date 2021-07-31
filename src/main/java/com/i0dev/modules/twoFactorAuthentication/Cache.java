package com.i0dev.modules.twoFactorAuthentication;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cache {
    @Getter
    List<UUID> cache = new ArrayList<>();

    @Getter
    List<TwoFactor> twoFactorCache = new ArrayList<>();

    public static Cache instance = new Cache();

    public static Cache getInstance() {
        return instance;
    }

}

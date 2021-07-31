package com.i0dev.modules.twoFactorAuthentication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Setter
@Getter
@NoArgsConstructor
public class TwoFactor {

    String code;
    User user;
    ProxiedPlayer player;


    public TwoFactor addToCache() {
        Cache.getInstance().getTwoFactorCache().add(this);
        return this;
    }


}

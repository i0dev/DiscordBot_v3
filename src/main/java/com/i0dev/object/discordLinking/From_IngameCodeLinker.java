package com.i0dev.object.discordLinking;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Setter
@Getter
public class From_IngameCodeLinker {

    private String code;
    private ProxiedPlayer player;

    public From_IngameCodeLinker(ProxiedPlayer player, String code) {
        this.code = code;
        this.player = player;
    }
}

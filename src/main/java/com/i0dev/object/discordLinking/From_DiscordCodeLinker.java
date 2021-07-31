package com.i0dev.object.discordLinking;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

@Getter
@Setter
public class From_DiscordCodeLinker {

    private String code;
    private User user;

    public From_DiscordCodeLinker(User user, String code) {
        this.code = code;
        this.user = user;
    }
}

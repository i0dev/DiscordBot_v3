package com.i0dev.object.discordLinking;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class CodeCache {

    private static CodeCache instance = new CodeCache();

    public static CodeCache getInstance() {
        return instance;
    }

    @Getter
    List<From_IngameCodeLinker> From_Ingame_cache = new ArrayList<>();
    @Getter
    List<From_DiscordCodeLinker> From_Discord_cache = new ArrayList<>();


    public From_IngameCodeLinker getObjectIngame(String code) {
        for (From_IngameCodeLinker object : getFrom_Ingame_cache()) {
            if (object.getCode().equals(code)) {
                return object;
            }
        }
        return null;
    }

    public From_DiscordCodeLinker getObjectDiscord(String code) {
        for (From_DiscordCodeLinker object : getFrom_Discord_cache()) {
            if (object.getCode().equals(code)) {
                return object;
            }
        }
        return null;
    }

}



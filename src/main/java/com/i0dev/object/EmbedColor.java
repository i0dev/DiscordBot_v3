package com.i0dev.object;

import com.i0dev.config.GeneralConfig;

public enum EmbedColor {
    NORMAL(GeneralConfig.get().normalColor),
    SUCCESS(GeneralConfig.get().successColor),
    FAILURE(GeneralConfig.get().failureColor);

    public String hexCode;

    EmbedColor(String s) {
        hexCode = s;
    }

    public EmbedColor setCustom(String custom) {
        hexCode = custom;
        return this;
    }

    public String getHexCode() {
        return hexCode;
    }
}
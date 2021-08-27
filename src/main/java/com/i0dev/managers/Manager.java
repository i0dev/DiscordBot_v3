package com.i0dev.managers;

import com.i0dev.DiscordBot;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Manager {

    public DiscordBot bot;

    public void initialize() {

    }

    public void deinitialize() {

    }
}

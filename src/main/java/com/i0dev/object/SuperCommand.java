package com.i0dev.object;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString
@Getter
public class SuperCommand extends BasicCommand {

    List<String> alternateCommands = new ArrayList<>();

    public SuperCommand(List<String> aliases, Permission permission, Class<? extends SuperDiscordCommand> clazz) {
        this.clazz = clazz;
        this.permission = permission;
        this.aliases = aliases;
    }

    public SuperCommand addAltCmd(String... s) {
        alternateCommands.addAll(Arrays.asList(s));
        return this;
    }

}

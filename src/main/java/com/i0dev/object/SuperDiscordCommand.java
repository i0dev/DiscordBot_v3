package com.i0dev.object;

public class SuperDiscordCommand extends DiscordCommand {


    public static SuperCommand getSuperCommand(Class<? extends DiscordCommand> clazz, Class<? extends AdvancedDiscordCommand> parent) {
        for (SuperCommand superCommand : ((AdvancedCommand) getBasicCommand(parent)).getSuperCommands()) {
            if (superCommand.getClazz().getName().equals(clazz.getName())) {
                return superCommand;
            }
        }
        return null;
    }

}

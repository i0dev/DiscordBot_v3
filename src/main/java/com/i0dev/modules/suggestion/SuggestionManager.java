package com.i0dev.modules.suggestion;

import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.object.managers.SQLManager;
import com.i0dev.utility.EmbedMaker;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SuggestionManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("add", new SuperCommand(s("add"), Permission.none(), Add.class).addAltCmd("suggest"));
        //   addSuperCommand("remove", new SuperCommand(s("remove"), Permission.strict(), Remove.class));
        //   addSuperCommand("list", new SuperCommand(s("list"), Permission.lite(), Retrieve.class));
        addSuperCommand("clear", new SuperCommand(s("clear"), Permission.admin(), Clear.class));
        //   addSuperCommand("info", new SuperCommand(s("info"), Permission.lite(), Info.class));
        addSuperCommand("accept", new SuperCommand(s("accept"), Permission.strict(), Accept.class));
        addSuperCommand("deny", new SuperCommand(s("deny"), Permission.strict(), Deny.class));

        addOption("pendingChannel", 0L);
        addOption("acceptedChannel", 0L);
        addOption("deniedChannel", 0L);
        addOption("upvoteEmoji", "U+1F44D");
        addOption("downvoteEmoji", "U+1F44E");
        addOption("acceptEmoji", "U+2705");
        addOption("denyEmoji", "U+274C");
        pending = Bot.getBot().getJda().getTextChannelById(getOption("pendingChannel").getAsLong());
        accepted = Bot.getBot().getJda().getTextChannelById(getOption("acceptedChannel").getAsLong());
        denied = Bot.getBot().getJda().getTextChannelById(getOption("deniedChannel").getAsLong());

        Bot.getBot().getManager(SQLManager.class).makeTable(Suggestion.class);


    }

    @SneakyThrows
    @CommandData(commandID = "cmd_suggestion", identifier = "Suggestion Manager")
    public static void run(CommandEvent e) {
    }

    public static TextChannel pending;
    public static TextChannel accepted;
    public static TextChannel denied;


    public static Message sendPending(EmbedMaker maker) {
        return pending.sendMessageEmbeds(EmbedMaker.create(maker)).complete();
    }

    public static Message sendAccepted(EmbedMaker maker) {
        return accepted.sendMessageEmbeds(EmbedMaker.create(maker)).complete();
    }

    public static Message sendDenied(EmbedMaker maker) {
        return denied.sendMessageEmbeds(EmbedMaker.create(maker)).complete();
    }

}

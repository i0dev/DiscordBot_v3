package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.managers.ConfigManager;
import com.i0dev.managers.SQLManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("add", new SuperCommand(s("add"), Permission.lite(), Add.class).addAltCmd("tadd"));
        addSuperCommand("adminOnly", new SuperCommand(s("adminOnly"), Permission.lite(), AdminOnly.class).addAltCmd("adminOnly", "tup", "tupgrade"));
        addSuperCommand("close", new SuperCommand(s("close"), Permission.lite(), Close.class).addAltCmd("close"));
        addSuperCommand("info", new SuperCommand(s("info"), Permission.lite(), Info.class).addAltCmd("tinfo"));
        addSuperCommand("remove", new SuperCommand(s("remove"), Permission.lite(), Remove.class).addAltCmd("tremove"));
        addSuperCommand("rename", new SuperCommand(s("rename"), Permission.lite(), Rename.class).addAltCmd("trename"));
        addSuperCommand("leaderboard", new SuperCommand(ls("lb", "leaderboard"), Permission.lite(), Leaderboard.class));
        addSuperCommand("panel", new SuperCommand(s("panel"), Permission.strict(), Panel.class).addAltCmd("tpanel"));
        addSuperCommand("manual", new SuperCommand(s("manual"), Permission.strict(), Manual.class).addAltCmd("tmanual"));

        Bot.getBot().getManager(SQLManager.class).makeTable(Ticket.class);
        addOption("adminLogsChannel", 0L);
        addOption("ticketLogsChannel", 0L);
        addOption("rolesToSeeTickets", Bot.getBot().getConfigManager().ObjectToJsonArr(rolesToSeeTickets));
        addOption("ticketOptions", Bot.getBot().getConfigManager().ObjectToJsonArr(options));

        addOption("ticketCreateChannel", 0);
        addOption("maxTicketsPerUser", 3);

        addOption("adminOnlyEmoji", "U+1F514");
        addOption("closeTicketEmoji", "U+1F5D1");

        rolesToSeeTickets = new ArrayList<>();
        options = new ArrayList<>();

        Bot.getBot().getManager(ConfigManager.class).getObjectFromInternalPath(getAnnotation(TicketManager.class).commandID() + ".options.rolesToSeeTickets", Bot.getBot().getConfigManager().getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> rolesToSeeTickets.add(jsonElement.getAsLong()));
        Bot.getBot().getManager(ConfigManager.class).getObjectFromInternalPath(getAnnotation(TicketManager.class).commandID() + ".options.ticketOptions", Bot.getBot().getConfigManager().getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> options.add((TicketOption) Bot.getBot().getConfigManager().JsonToObject(jsonElement, TicketOption.class)));

    }

    @Getter
    public static List<Long> rolesToSeeTickets = new ArrayList<>();

    @Getter
    public static List<TicketOption> options = Collections.singletonList(new TicketOption(new ArrayList<>(), 0L, "support-{num}", "U+1F39F", "Support Ticket", false, false, new ArrayList<>(), new ArrayList<>()));

    @SneakyThrows
    @CommandData(commandID = "cmd_ticket", identifier = "Ticket Manager")

    public static void run(CommandEvent e) {
    }

    public static boolean isTicket(ISnowflake idAble) {
        return Bot.getBot().getManager(SQLManager.class).objectExists(Ticket.class.getSimpleName(), "channelID", idAble.getId());
    }


}

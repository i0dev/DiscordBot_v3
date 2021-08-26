package com.i0dev.modules.ticket;

import com.i0dev.Bot;
import com.i0dev.modules.movement.MovementManager;
import com.i0dev.modules.movement.MovementObject;
import com.i0dev.object.*;
import com.i0dev.utility.ConfigUtil;
import com.i0dev.utility.SQLUtil;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.ArrayList;
import java.util.Collection;
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

        SQLUtil.makeTable(Ticket.class);
        addOption("adminLogsChannel", 0L);
        addOption("ticketLogsChannel", 0L);
        addOption("rolesToSeeTickets", ConfigUtil.ObjectToJsonArr(rolesToSeeTickets));
        addOption("ticketOptions", ConfigUtil.ObjectToJsonArr(options));

        addOption("ticketCreateChannel", 0);
        addOption("maxTicketsPerUser", 3);

        addOption("adminOnlyEmoji", "U+1F514");
        addOption("closeTicketEmoji", "U+1F5D1");

        rolesToSeeTickets = new ArrayList<>();
        options = new ArrayList<>();

        ConfigUtil.getObjectFromInternalPath(getAnnotation(TicketManager.class).commandID() + ".options.rolesToSeeTickets", ConfigUtil.getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> rolesToSeeTickets.add(jsonElement.getAsLong()));
        ConfigUtil.getObjectFromInternalPath(getAnnotation(TicketManager.class).commandID() + ".options.ticketOptions", ConfigUtil.getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> options.add((TicketOption) ConfigUtil.JsonToObject(jsonElement, TicketOption.class)));

    }

    @Getter
    public static List<Long> rolesToSeeTickets = new ArrayList<>();

    @Getter
    public static List<TicketOption> options = Collections.singletonList(new TicketOption(new ArrayList<>(), 0L, "support-{num}", "U+1F39F", "Support Ticket", false, false));

    @SneakyThrows
    @CommandData(commandID = "cmd_ticket", identifier = "Ticket Manager")

    public static void run(CommandEvent e) {
    }

    public static boolean isTicket(ISnowflake idAble) {
        return SQLUtil.objectExists(Ticket.class.getSimpleName(), "channelID", idAble.getId());
    }


}

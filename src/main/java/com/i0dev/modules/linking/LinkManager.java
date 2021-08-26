package com.i0dev.modules.linking;

import com.google.gson.JsonObject;
import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.managers.ConfigManager;
import com.i0dev.utility.LogUtil;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import java.util.*;

public class LinkManager extends AdvancedDiscordCommand {

    public static void load() {
        try {
            addSuperCommand("code", new SuperCommand(s("code"), Permission.none(), Code.class));
            addSuperCommand("force", new SuperCommand(s("force"), Permission.none(), Force.class));
            addSuperCommand("generate", new SuperCommand(s("generate"), Permission.none(), Generate.class));
            addSuperCommand("info", new SuperCommand(s("info"), Permission.lite(), Info.class));
            addSuperCommand("remove", new SuperCommand(s("remove"), Permission.strict(), Remove.class));
            addSuperCommand("resync", new SuperCommand(s("resync"), Permission.strict(), Resync.class));
            addSuperCommand("panel", new SuperCommand(s("panel"), Permission.strict(), Panel.class));
            addOption("nicknameFormat", "[VIP] {ign}");
            ConfigManager manager = Bot.getBot().getConfigManager();

            addOption("rolesToGiveAlways", manager.ObjectToJsonArr(rolesToGiveAlways));
            addOption("rolesThatBypassNicknameChange", manager.ObjectToJsonArr(rolesThatBypassNicknameChange));
            addOption("guildsThatBypassNicknameChange", manager.ObjectToJsonArr(guildsThatBypassNicknameChange));
            JsonObject json = new JsonObject();
            Map<String, Long> ranksToLink = Collections.singletonMap("rival", 791840959687163914L);
            for (Map.Entry<String, Long> entry : ranksToLink.entrySet()) {
                json.addProperty(entry.getKey(), entry.getValue());
            }
            addOption("ranksToLink", json);
            rolesThatBypassNicknameChange = new ArrayList<>();
            guildsThatBypassNicknameChange = new ArrayList<>();
            rolesToGiveAlways = new ArrayList<>();
            manager.getObjectFromInternalPath(getAnnotation(LinkManager.class).commandID() + ".options.rolesToGiveAlways", manager.getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> rolesToGiveAlways.add(jsonElement.getAsLong()));
            manager.getObjectFromInternalPath(getAnnotation(LinkManager.class).commandID() + ".options.rolesThatBypassNicknameChange", manager.getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> rolesThatBypassNicknameChange.add(jsonElement.getAsLong()));
            manager.getObjectFromInternalPath(getAnnotation(LinkManager.class).commandID() + ".options.guildsThatBypassNicknameChange", manager.getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> guildsThatBypassNicknameChange.add(jsonElement.getAsLong()));
        } catch (Exception e) {
            LogUtil.log(Arrays.toString(e.getStackTrace()));
        }
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_link", identifier = "Link", canBePrivateMessage = true)
    public static void run(CommandEvent e) {
    }


    public static List<Long> rolesToGiveAlways = new ArrayList<>();
    public static List<Long> rolesThatBypassNicknameChange = new ArrayList<>();
    public static List<Long> guildsThatBypassNicknameChange = new ArrayList<>();


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(e.getUser());
        RoleRefreshHandler.RefreshUserRank(dPlayer);
    }

}

package com.i0dev.modules.linking;

import com.google.gson.JsonObject;
import com.i0dev.Bot;
import com.i0dev.object.AdvancedDiscordCommand;
import com.i0dev.object.RoleQueueObject;
import com.i0dev.object.Type;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.ConfigUtil;
import com.i0dev.utility.NicknameUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoleRefreshHandler {


    public static void RefreshUserRank(DPlayer dPlayer) {
        if (dPlayer == null) return;
        User discordUser = Bot.getJda().retrieveUserById(dPlayer.getDiscordID()).complete();
        if (discordUser == null) return;
        if ("".equals(dPlayer.getMinecraftIGN())) return;
        if (!Utility.hasRole(discordUser, LinkManager.rolesThatBypassNicknameChange)) {
            String nickname = LinkManager.getOption("nicknameFormat", LinkManager.class).getAsString()
                    .replace("{ign}", dPlayer.getMinecraftIGN());
            Utility.getAllowedGuilds().stream().filter(guild -> !LinkManager.rolesThatBypassNicknameChange.contains(guild.getIdLong())).forEach(guild -> {
                Member member = guild.getMember(discordUser);
                if (member == null) return;
                NicknameUtil.modifyNickname(member, nickname);
            });
        }
        List<Long> ranksAlways = LinkManager.rolesToGiveAlways;
        for (Long roleID : ranksAlways) {
            if (Utility.hasRoleAlready(roleID, dPlayer.getDiscordID())) continue;
            new RoleQueueObject(dPlayer.getDiscordID(), roleID, Type.ADD_ROLE).add();
        }

        // in game stuff

        if (!dPlayer.isLinked()) return;
        if (!Bot.isPluginMode()) return;
        if (com.i0dev.BotPlugin.server.getPluginManager().getPlugin("LuckPerms") == null) return;
        net.luckperms.api.LuckPerms luckPerms = net.luckperms.api.LuckPermsProvider.get();
        net.luckperms.api.model.user.User user = luckPerms.getUserManager().loadUser(UUID.fromString(dPlayer.getMinecraftUUID())).join();
        if (user == null) return;
        List<String> groups = user.getNodes(net.luckperms.api.node.NodeType.INHERITANCE).stream()
                .map(net.luckperms.api.node.types.InheritanceNode::getGroupName)
                .collect(Collectors.toList());
        if (groups.isEmpty()) return;
        JsonObject jsonMap = ConfigUtil.getObjectFromInternalPath(AdvancedDiscordCommand.getAnnotation(LinkManager.class).commandID() + ".options.ranksToLink", ConfigUtil.getJsonObject(Bot.getBasicConfigPath())).getAsJsonObject();
        Map<String, Long> ranksToLink = new HashMap<>();
        jsonMap.entrySet().forEach(s -> ranksToLink.put(s.getKey(), s.getValue().getAsLong()));
        for (Object key : ranksToLink.keySet()) {
            long roleID = ranksToLink.get(key);
            if (groups.contains(key.toString())) {
                if (Utility.hasRoleAlready(roleID, dPlayer.getDiscordID())) continue;
                new RoleQueueObject(dPlayer.getDiscordID(), roleID, Type.ADD_ROLE).add();
            } else {
                if (!Utility.hasRoleAlready(roleID, dPlayer.getDiscordID())) continue;
                new RoleQueueObject(dPlayer.getDiscordID(), roleID, Type.REMOVE_ROLE).add();
            }
        }
    }
}

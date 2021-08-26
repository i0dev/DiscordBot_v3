package com.i0dev.modules.movement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.i0dev.Bot;
import com.i0dev.object.*;
import com.i0dev.utility.ConfigUtil;
import com.i0dev.utility.EmbedMaker;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovementManager extends AdvancedDiscordCommand {

    public static void load() {
        addSuperCommand("demote", new SuperCommand(s("demote"), Permission.strict(), Demote.class));
        addSuperCommand("promote", new SuperCommand(s("promote"), Permission.strict(), Promote.class));
        addSuperCommand("remove", new SuperCommand(s("remove"), Permission.strict(), Remove.class));
        addSuperCommand("resign", new SuperCommand(s("resign"), Permission.strict(), Resign.class));

        addOption("nicknameFormat", "[{displayName}] {ignOrName}");
        addOption("tracks", ConfigUtil.ObjectToJsonArr(Tracks));
        addOption("channel", 0L);
        movementChannel = Bot.getBot().getJda().getTextChannelById(getOption("channel").getAsLong());
        Tracks = new ArrayList<>();
        ConfigUtil.getObjectFromInternalPath(getAnnotation(MovementManager.class).commandID() + ".options.tracks", ConfigUtil.getJsonObject(Bot.getBot().getBasicConfigPath())).getAsJsonArray().forEach(jsonElement -> Tracks.add((MovementObject) ConfigUtil.JsonToObject(jsonElement, MovementObject.class)));
    }

    @SneakyThrows
    @CommandData(commandID = "cmd_movement", identifier = "Movement Manager")
    public static void run(CommandEvent e) {
    }


    //
    // movement util
    //

    public static TextChannel movementChannel;

    public static void sendMsg(EmbedMaker maker) {
        movementChannel.sendMessageEmbeds(EmbedMaker.create(maker)).queue();
    }

    @Getter
    private static List<MovementObject> Tracks = Collections.singletonList(new MovementObject(0, "", new ArrayList<>(), ""));

    public static Role getParentStaff(Member member) {
        for (MovementObject object : Tracks) {
            long mainRoleID = object.getMainRole();

            Role mainRole = Bot.getBot().getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;
            if (member.getRoles().contains(mainRole)) {
                return mainRole;
            }
        }
        return null;
    }

    public static void giveNewRoles(Member member, long mainRoleID) {
        for (MovementObject object : Tracks) {
            if (object.getMainRole() != (mainRoleID)) continue;
            List<Long> RoleIDS = object.getExtraRoles();
            RoleIDS.add(mainRoleID);
            for (long roleToGiveID : RoleIDS) {
                Role roleToGive = Bot.getBot().getJda().getRoleById(roleToGiveID);
                if (roleToGive == null) continue;
                new RoleQueueObject(member.getIdLong(), roleToGive.getIdLong(), Type.ADD_ROLE).add();
            }
        }
    }

    public static void removeOldRoles(Member member, long oldMainRoleID) {
        for (MovementObject object : Tracks) {
            if (object.getMainRole() != oldMainRoleID) continue;
            List<Long> RoleIDS = object.getExtraRoles();
            RoleIDS.add(oldMainRoleID);
            for (long roleToGiveID : RoleIDS) {
                Role roleToGive = Bot.getBot().getJda().getRoleById(roleToGiveID);
                if (roleToGive == null) continue;
                new RoleQueueObject(member.getIdLong(), roleToGive.getIdLong(), Type.REMOVE_ROLE).add();
            }
        }
    }

    public static MovementObject getObject(Role role) {
        for (MovementObject object : Tracks) {
            if (object.getMainRole() == role.getIdLong()) {
                return object;
            }
        }
        return null;
    }

    public static boolean isAlreadyStaff(Member member) {
        for (MovementObject object : Tracks) {
            long mainRoleID = object.getMainRole();
            Role mainRole = Bot.getBot().getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;
            if (member.getRoles().contains(mainRole)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHighestStaff(Member member) {
        long topRoleID = Tracks.get(Tracks.size() - 1).getMainRole();
        Role topRole = Bot.getBot().getJda().getRoleById(topRoleID);
        if (topRole == null) return false;
        if (member.getRoles().contains(topRole)) {
            return true;
        }
        return false;
    }

    public static boolean isLowestStaff(Member member) {
        long lowestRoleID = Tracks.get(0).getMainRole();
        Role lowestRole = Bot.getBot().getJda().getRoleById(lowestRoleID);
        if (lowestRole == null) return false;
        return member.getRoles().contains(lowestRole);
    }


    public static Role getNextRole(Role role) {

        for (int i = 0; i < Tracks.size(); i++) {
            long mainRoleID = (long) Tracks.get(i).getMainRole();
            Role mainRole = Bot.getBot().getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;

            if (mainRole == role) {

                try {
                    Tracks.get(i + 1).getMainRole();
                } catch (Exception ignored) {
                    return null;
                }

                long nextTrackID = Tracks.get(i + 1).getMainRole();

                Role nextRole = Bot.getBot().getJda().getRoleById(nextTrackID);
                if (nextRole == null) continue;

                return nextRole;
            }
        }
        return null;
    }

    public static Role getPreviousRole(Role role) {

        for (int i = 0; i < Tracks.size(); i++) {
            long mainRoleID = (long) Tracks.get(i).getMainRole();
            Role mainRole = Bot.getBot().getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;

            if (mainRole == role) {

                try {
                    Tracks.get(i - 1).getMainRole();
                } catch (Exception ignored) {
                    return null;
                }

                long nextTrackID = (long) Tracks.get(i - 1).getMainRole();

                Role nextRole = Bot.getBot().getJda().getRoleById(nextTrackID);
                if (nextRole == null) continue;

                return nextRole;
            }
        }
        return null;
    }

    public static MovementObject getNextRoleObject(Role role) {
        for (int i = 0; i < Tracks.size(); i++) {
            long mainRoleID = (long) Tracks.get(i).getMainRole();
            Role mainRole = Bot.getBot().getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;
            if (mainRole == role) {
                try {
                    Tracks.get(i + 1).getMainRole();
                } catch (Exception ignored) {
                    return null;
                }
                long nextTrackID = Tracks.get(i + 1).getMainRole();

                Role nextRole = Bot.getBot().getJda().getRoleById(nextTrackID);
                if (nextRole == null) continue;
                return getObject(nextRole);
            }
        }
        return null;
    }

    public static MovementObject getPreviousRoleObject(Role role) {
        for (int i = 0; i < Tracks.size(); i++) {
            long mainRoleID = (long) Tracks.get(i).getMainRole();
            Role mainRole = Bot.getBot().getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;
            if (mainRole == role) {
                try {
                    Tracks.get(i - 1).getMainRole();
                } catch (Exception ignored) {
                    return null;
                }
                long nextTrackID = (long) Tracks.get(i - 1).getMainRole();

                Role nextRole = Bot.getBot().getJda().getRoleById(nextTrackID);
                if (nextRole == null) continue;
                return getObject(nextRole);
            }
        }
        return null;
    }


}

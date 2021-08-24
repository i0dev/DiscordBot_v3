package com.i0dev;

import com.i0dev.config.GeneralConfig;
import com.i0dev.config.MiscConfig;
import com.i0dev.modules.giveaway.Giveaway;
import com.i0dev.modules.giveaway.GiveawayHandler;
import com.i0dev.modules.linking.RoleRefreshHandler;
import com.i0dev.modules.mute.MuteManager;
import com.i0dev.object.LogObject;
import com.i0dev.object.RoleQueueObject;
import com.i0dev.object.Type;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.dv8tion.jda.api.entities.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@FieldDefaults(level = AccessLevel.PUBLIC)
public class Engine {

    static void run() {
        ScheduledExecutorService executorService = Bot.getAsyncService();
        executorService.scheduleAtFixedRate(taskExecuteMemberCountUpdate, 1, 2, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(taskUpdateDPlayerCache, 1, 1, TimeUnit.HOURS);
        executorService.scheduleAtFixedRate(taskExecuteGiveaways, 1, 10, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskAppendToFile, 1, 10, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskUpdateActivity, 1, 30, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskVerifyAuthentication, 15, 15 * 60, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskGiveContinuousRoles, 1, 60, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(taskExecuteRoleQueue, 1, 2, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(taskBackupConfig, 1, 2 * 60, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(taskAssureMuted, 1, 10, TimeUnit.MINUTES);

        executorService.scheduleAtFixedRate(DPlayer.taskClearCache, 25, 5, TimeUnit.MINUTES);
    }

    @Getter
    private static final ArrayList<RoleQueueObject> roleQueueList = new ArrayList<>();

    static Runnable taskExecuteRoleQueue = () -> {
        try {
            if (roleQueueList.isEmpty()) return;
            RoleQueueObject queueObject = roleQueueList.get(0);
            roleQueueList.remove(queueObject);
            User user = Bot.getJda().getUserById(queueObject.getUserID());
            Role role = Bot.getJda().getRoleById(queueObject.getRoleID());
            if (user == null || role == null) return;
            Guild guild = role.getGuild();
            Member member = guild.getMemberById(user.getId());
            if (member == null) return;
            if (queueObject.getType().equals(Type.ADD_ROLE)) {
                if (member.getRoles().contains(role)) return;
                guild.addRoleToMember(user.getId(), role).queue();
                LogUtil.debug("Applied the role {" + role.getName() + "} to the user: {" + member.getEffectiveName() + "}");
            } else if (queueObject.getType().equals(Type.REMOVE_ROLE)) {
                if (!member.getRoles().contains(role)) return;
                guild.removeRoleFromMember(user.getId(), role).queue();
                LogUtil.debug("removed the role {" + role.getName() + "} from the user: {" + member.getEffectiveName() + "}");
            }

        } catch (Exception ignored) {

        }
    };

    static Runnable taskAssureMuted = () -> {
        Role mutedRole = MuteManager.mutedRole;
        if (mutedRole == null) return;
        List<Object> muted = SQLUtil.getListWhere(DPlayer.class.getSimpleName(), "muted", "1", DPlayer.class, "discordID");
        AtomicInteger total = new AtomicInteger();
        muted.forEach(o -> {
            DPlayer dPlayer = ((DPlayer) o);
            if (Utility.hasRoleAlready(mutedRole.getIdLong(), dPlayer.getDiscordID())) return;
            total.getAndIncrement();
            new RoleQueueObject(dPlayer.getDiscordID(), mutedRole.getIdLong(), Type.ADD_ROLE).add();
        });
        if (total.get() != 0) LogUtil.log("Assured " + total + " muted users have the muted role.");
    };

    static Runnable taskGiveContinuousRoles = () -> {
        AtomicInteger count = new AtomicInteger(0);
        for (User user : Bot.getJda().getUsers()) {
            for (Long roleID : MiscConfig.get().rolesToConstantlyGive) {
                Role role = Bot.getJda().getRoleById(roleID);
                if (role == null) continue;
                if (Utility.hasRoleAlready(roleID, user.getIdLong())) continue;
                count.getAndIncrement();
                new RoleQueueObject(user.getIdLong(), roleID, Type.ADD_ROLE).add();
            }
        }
        if (count.get() != 0) LogUtil.debug("Gave " + count + " users missing roles.");
    };


    static Runnable taskBackupConfig = () -> {
        //month-day-year
        //8-6-2021
        String date = ZonedDateTime.now().getMonthValue() + "-" + ZonedDateTime.now().getDayOfMonth() + "-" + ZonedDateTime.now().getYear();
        try {
            File commandsFile = new File(Bot.getStoragePath() + "/" + "CommandsConfigBackup-" + date);
            if (!commandsFile.exists()) {
                Files.write(Paths.get(commandsFile.getAbsolutePath()), ConfigUtil.getJsonObject(Bot.getBasicConfigPath()).toString().getBytes());
            }

            File generalFile = new File(Bot.getStoragePath() + "/" + "GeneralConfigBackup-" + date);
            if (!generalFile.exists()) {
                Files.write(Paths.get(generalFile.getAbsolutePath()), ConfigUtil.getJsonObject(Bot.getConfigPath()).toString().getBytes());
            }

            File miscFile = new File(Bot.getStoragePath() + "/" + "MiscConfigBackup-" + date);
            if (!miscFile.exists()) {
                Files.write(Paths.get(miscFile.getAbsolutePath()), ConfigUtil.getJsonObject(Bot.getMiscConfigPath()).toString().getBytes());
            }

            File customCmdFile = new File(Bot.getStoragePath() + "/" + "CustomCommandsBackup-" + date);
            if (!customCmdFile.exists()) {
                Files.write(Paths.get(customCmdFile.getAbsolutePath()), ConfigUtil.getJsonObject(Bot.getCustomCommandsConfigPath()).toString().getBytes());
            }
        } catch (Exception ignored) {

        }
    };

    static Runnable taskExecuteMemberCountUpdate = () -> {
        if (!MiscConfig.instance.memberCount_enabled) return;
        GuildChannel channel = Bot.getJda().getGuildChannelById(MiscConfig.get().getMemberCount_channel());
        if (channel == null) return;
        channel.getManager().setName(MiscConfig.get().memberCount_format.replace("{count}", channel.getGuild().getMemberCount() + "")).queue();
    };

    static Runnable taskUpdateDPlayerCache = () -> {
        SQLUtil.getAllObjects(DPlayer.class.getSimpleName(), "discordID", DPlayer.class).stream().filter(o -> ((DPlayer) o).isLinked()).forEach(o -> {
            DPlayer dPlayer = (DPlayer) o;

            RoleRefreshHandler.RefreshUserRank(dPlayer);

            if (dPlayer.getLastUpdatedMillis() + 259200000 < System.currentTimeMillis()) return;
            String ign = APIUtil.getIGNFromUUID(dPlayer.getMinecraftUUID());
            if (ign == null) return;
            dPlayer.setMinecraftIGN(ign);
            dPlayer.setLastUpdatedMillis(System.currentTimeMillis());
            dPlayer.save();
        });
    };

    static Runnable taskExecuteGiveaways = () -> {
        SQLUtil.getAllObjects(Giveaway.class.getSimpleName(), "messageID", Giveaway.class).stream().filter(o -> !((Giveaway) o).isEnded()).forEach(o -> GiveawayHandler.endGiveawayFull(((Giveaway) o), false, false, false, null));
    };

    static Runnable taskUpdateActivity = () -> {
        String activity = PlaceholderUtil.convert(GeneralConfig.get().getActivity(), null, null);
        switch (GeneralConfig.get().activityType.toLowerCase()) {
            case "watching":
                Bot.getJda().getPresence().setActivity(Activity.watching(activity));
                break;
            case "listening":
                Bot.getJda().getPresence().setActivity(Activity.listening(activity));
                break;
            case "playing":
                Bot.getJda().getPresence().setActivity(Activity.playing(activity));
                break;
        }
    };

    static Runnable taskVerifyAuthentication = () -> {
        if (Bot.getJda() == null || Bot.getJda().getGuildById("773035795023790131") == null) {
            LogUtil.severe("Failed to verify with authentication servers.");
            Bot.shutdown();
            MessageUtil.sendPluginMessage("bot_command", "shutdown");
        }
    };


    @Getter
    static final List<LogObject> toLog = new LinkedList<>();

    public static Runnable taskAppendToFile = () -> {
        if (toLog.isEmpty()) return;
        List<LogObject> cache = new LinkedList<>(toLog);
        for (LogObject object : cache) {
            try {
                FileWriter fileWriter = new FileWriter(object.getFile(), true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);

                printWriter.println(object.getContent());

                bufferedWriter.close();
                printWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        toLog.removeAll(cache);
    };


}
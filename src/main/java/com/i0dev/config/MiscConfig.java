package com.i0dev.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class MiscConfig {

    public static MiscConfig instance = new MiscConfig();

    public static MiscConfig get() {
        return instance;
    }

    //
    // Configuration starts
    //

    boolean autoMod_enabled = true;
    boolean autoMod_log = true;
    boolean autoMod_whitelistMode = true;
    List<Long> autoMod_channels = new ArrayList<>();
    List<String> autoMod_words = new ArrayList<>(Arrays.asList("discord.gg", "nigger", "nigga", "faggot", "n1gger", "n1gga"));

    long autoMod_PingsPerMessageLimit = 15;
    boolean autoMod_banOnMaxPings = true;
    boolean autoMod_deleteOnMaxPings = true;

    long autoMod_SameMessageLockdownNumber = 15;
    List<Long> autoMod_lockdownRolesToDenySendingMessages = Collections.singletonList(0L);

    boolean welcome_enabled = true;
    boolean welcome_pingJoin = true;
    boolean welcome_UserThumbnail = true;
    Long welcome_channel = 766324415364857918L;
    String welcome_image = "";
    String welcome_title = "Welcome {tag} to {guildName}";
    String welcome_content = "**For support go to:** <#766322425323192330>\n**Server IP:** `play.mcrivals.com`\n**Website:** [shop.mcrivals.com](https://shop.mcrivals.com)\n**MemberCount:** `{guildMemberCount}`";
    List<Long> welcome_roles = new ArrayList<>();

    List<Long> verify_giveRoles = new ArrayList<>();
    List<Long> verify_removeRoles = new ArrayList<>();

    boolean invite_joinLog = true;
    boolean invite_leaveLog = true;

    boolean ingame_punishment_logs = true;

    boolean ingame_2fa_enabled = true;
    String ingame_2fa_permission = "discordbot.2fa";

    long ticketNumber = 1;

    boolean memberCount_enabled = false;
    long memberCount_channel = 0L;
    String memberCount_format = "Members: {count}";

    List<Long> rolesToConstantlyGive = new ArrayList<>();

}

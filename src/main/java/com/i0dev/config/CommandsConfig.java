package com.i0dev.config;

import com.i0dev.object.AdvancedCommand;
import com.i0dev.object.BasicCommand;
import com.i0dev.object.Permission;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class CommandsConfig {

    public static CommandsConfig instance = new CommandsConfig();

    public static CommandsConfig get() {
        return instance;
    }

    public static <T> List<T> s(T s) {
        return Collections.singletonList(s);
    }

    @SafeVarargs
    static <T> List<T> ls(T... ts) {
        List<T> ret = new ArrayList<>();
        Collections.addAll(ret, ts);
        return ret;
    }

    //
    // Configuration starts
    //

    //basic
    BasicCommand cmd_avatar = new BasicCommand(s("avatar"), Permission.none());
    BasicCommand cmd_heapDump = new BasicCommand(s("heapDump"), Permission.strict());
    BasicCommand cmd_help = new BasicCommand(s("help"), Permission.none());
    BasicCommand cmd_serverLookup = new BasicCommand(s("serverLookup"), Permission.none());
    BasicCommand cmd_members = new BasicCommand(s("members"), Permission.none());
    BasicCommand cmd_profile = new BasicCommand(s("profile"), Permission.none());
    BasicCommand cmd_reload = new BasicCommand(s("reload"), Permission.strict());
    BasicCommand cmd_roleInfo = new BasicCommand(s("roleInfo"), Permission.none());
    BasicCommand cmd_roles = new BasicCommand(s("roles"), Permission.none());
    BasicCommand cmd_serverInfo = new BasicCommand(s("serverInfo"), Permission.none());
    BasicCommand cmd_version = new BasicCommand(s("version"), Permission.none());

    //fun
    BasicCommand cmd_coinFlip = new BasicCommand(s("coinflip"), Permission.none());
    BasicCommand cmd_dice = new BasicCommand(s("dice"), Permission.none());
    BasicCommand cmd_magic8ball = new BasicCommand(s("8ball"), Permission.none());
    BasicCommand cmd_hey = new BasicCommand(s("hey"), Permission.none());
    BasicCommand cmd_pat = new BasicCommand(s("pat"), Permission.none());
    BasicCommand cmd_slap = new BasicCommand(s("slap"), Permission.none());

    //moderation
    BasicCommand cmd_announce = new BasicCommand(s("announce"), Permission.strict());
    BasicCommand cmd_ban = new BasicCommand(s("ban"), Permission.strict());
    BasicCommand cmd_changelog = new BasicCommand(s("changelog"), Permission.strict());
    BasicCommand cmd_directMessage = new BasicCommand(ls("directMessage", "dm"), Permission.strict());
    BasicCommand cmd_kick = new BasicCommand(s("kick"), Permission.strict());
    BasicCommand cmd_purge = new BasicCommand(ls("purge", "prune"), Permission.strict());
    BasicCommand cmd_unban = new BasicCommand(s("unban"), Permission.strict());
    BasicCommand cmd_verifyPanel = new BasicCommand(s("verifyPanel"), Permission.strict());
    BasicCommand cmd_dataDump = new BasicCommand(s("dataDump"), Permission.admin());


    //advanced
    AdvancedCommand cmd_blacklist = new AdvancedCommand(s("blacklist"), Permission.strict());
    AdvancedCommand cmd_invite = new AdvancedCommand(s("invite"), Permission.none());
    AdvancedCommand cmd_link = new AdvancedCommand(s("link"), Permission.none());
    AdvancedCommand cmd_movement = new AdvancedCommand(s("movement"), Permission.strict());
    AdvancedCommand cmd_mute = new AdvancedCommand(s("mute"), Permission.lite());
    AdvancedCommand cmd_suggestion = new AdvancedCommand(s("suggestion"), Permission.none());
    AdvancedCommand cmd_ticket = new AdvancedCommand(s("ticket"), Permission.lite());
    AdvancedCommand cmd_giveaway = new AdvancedCommand(s("giveaway"), Permission.lite());

    AdvancedCommand cmd_factions = new AdvancedCommand(s("factions"), Permission.lite());
    AdvancedCommand cmd_skyblock = new AdvancedCommand(s("skyblock"), Permission.lite());
    AdvancedCommand cmd_prison = new AdvancedCommand(s("prison"), Permission.lite());

}


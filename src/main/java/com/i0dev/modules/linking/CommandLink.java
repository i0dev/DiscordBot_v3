package com.i0dev.modules.linking;

import com.i0dev.Bot;
import com.i0dev.config.GeneralConfig;
import com.i0dev.object.discordLinking.CodeCache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.From_DiscordCodeLinker;
import com.i0dev.object.discordLinking.From_IngameCodeLinker;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.LogUtil;
import com.i0dev.utility.MessageUtil;
import com.i0dev.utility.Utility;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandLink extends Command {


    public CommandLink(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer commandSender = (ProxiedPlayer) sender;
        if (args.length == 1 && args[0].equalsIgnoreCase("generate")) {
            DPlayer dPlayer = DPlayer.getDPlayerFromIGN(commandSender.getName());
            if (dPlayer == null) {
                String code = Utility.GenerateRandomString(5);
                From_IngameCodeLinker codeLinker = new From_IngameCodeLinker(commandSender, code);
                CodeCache.getInstance().getFrom_Ingame_cache().add(codeLinker);
                MessageUtil.sendMessageInGame(commandSender, "&7Type &o&c{prefix}link code {code}&r&7 in discord to finish linking.".replace("{prefix}", GeneralConfig.get().getPrefixes().get(0)).replace("{code}", code));
            } else
                MessageUtil.sendMessageInGame(commandSender, "&7You are already linked to the discord tag &c{tag}".replace("{tag}", Bot.getJda().retrieveUserById(dPlayer.getDiscordID()).complete().getAsTag()));
            return;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("code")) {
            DPlayer preDPlayer = DPlayer.getDPlayerFromIGN(commandSender.getName());
            if (preDPlayer == null) {
                String code = args[1];
                From_DiscordCodeLinker codeLinker = CodeCache.getInstance().getObjectDiscord(code);
                if (codeLinker == null) {
                    MessageUtil.sendMessageInGame(commandSender, "&7The code &c{code} &7is invalid. Please try again.".replace("{code}", code));
                    return;
                }
                DPlayer dPlayer = DPlayer.getDPlayer(codeLinker.getUser());
                dPlayer.link(code, commandSender.getName(), commandSender.getUniqueId().toString());
                MessageUtil.sendMessageInGame(commandSender, "&7You have linked yourself to the discord tag: &c{tag}".replace("{tag}", codeLinker.getUser().getAsTag()));
                LogUtil.logDiscord(EmbedMaker.builder().content("{tag} is now linked to the ign: `{ign}`").user(codeLinker.getUser()).build());
                RoleRefreshHandler.RefreshUserRank(dPlayer);
                CodeCache.getInstance().getFrom_Discord_cache().remove(codeLinker);

            } else
                MessageUtil.sendMessageInGame(commandSender, "&7You are already linked to the discord tag &c{tag}".replace("{tag}", Bot.getJda().retrieveUserById(preDPlayer.getDiscordID()).complete().getAsTag()));

            return;
        }

        commandSender.sendMessage(Utility.c("&9&l/link command:"));
        commandSender.sendMessage(Utility.c("&c/link generate &f- &7Generates a code to link your account to discord."));
        commandSender.sendMessage(Utility.c("&c/link code <code> &f- &7Links your account with that code."));
        commandSender.sendMessage(Utility.c("&f"));
    }
}

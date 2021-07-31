package com.i0dev.modules.twoFactorAuthentication;

import com.i0dev.utility.EncryptionUtil;
import com.i0dev.utility.MessageUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

public class Command2fa extends Command {

    public Command2fa(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = ((ProxiedPlayer) commandSender);
        if (!Cache.getInstance().getCache().contains(player.getUniqueId()))
            return;

        if (args.length != 1 || !TwoFactorAuthentication.isCodeValid(args[0])) {
            MessageUtil.sendMessageInGame(player, "&c{code} &7is invalid!".replace("{code}", args.length != 1 ? "No Code Provided" : args[0]));
            return;
        }

        TwoFactor twoFactor = TwoFactorAuthentication.getObject(player);
        if (twoFactor.getCode().equalsIgnoreCase(args[0])) {
            Cache.getInstance().getTwoFactorCache().remove(twoFactor);
            Cache.getInstance().getCache().remove(player.getUniqueId());
            MessageUtil.sendMessageInGame(player, "&aYou have passed two factor authentication!");
            byte[] ip = player.getAddress().getAddress().getAddress();
            TwoFactorAuthentication.getIpCache().put(player.getUniqueId(), EncryptionUtil.encrypt(Arrays.toString(ip), player.getUniqueId().toString()));
            return;
        } else {
            MessageUtil.sendMessageInGame(player, "&c{code} &7is invalid!".replace("{code}", args[0]));

        }
        MessageUtil.sendMessageInGame(player, "&c/2fa <code>");


    }
}

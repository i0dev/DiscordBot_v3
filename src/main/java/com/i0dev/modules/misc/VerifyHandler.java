package com.i0dev.modules.misc;

import com.i0dev.Bot;
import com.i0dev.config.MiscConfig;
import com.i0dev.modules.moderation.CmdVerifyPanel;
import com.i0dev.object.EmbedColor;
import com.i0dev.object.RoleQueueObject;
import com.i0dev.object.Type;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.utility.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class VerifyHandler extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getButton() == null) return;
        if (!"BUTTON_VERIFY_PANEL_2".equalsIgnoreCase(e.getButton().getId())) return;
        if (e.getUser().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        DPlayer dPlayer = Bot.getBot().getDPlayerManager().getDPlayer(e.getUser());
        if (dPlayer.isBlacklisted()) return;
        MessageUtil.sendPrivateMessage(e.getMessage(), e.getUser(), EmbedMaker.builder().authorName(CmdVerifyPanel.getOption("title", CmdVerifyPanel.class).getAsString()).authorImg(e.getUser().getEffectiveAvatarUrl()).embedColor(EmbedColor.SUCCESS).content("You have successfully verified yourself in the **{guildName}** discord!".replace("{guildName}", e.getGuild().getName())).build());
        e.getInteraction().deferReply(true).setContent("You have successfully verified yourself.").queue();
        MiscConfig.get().verify_removeRoles.forEach(roleID -> new RoleQueueObject(e.getUser().getIdLong(), roleID, Type.REMOVE_ROLE).add());
        MiscConfig.get().verify_giveRoles.forEach(roleID -> new RoleQueueObject(e.getUser().getIdLong(), roleID, Type.ADD_ROLE).add());
        LogUtil.logDiscord(EmbedMaker.builder().author(e.getUser()).user(e.getUser()).content("{tag} has successfully verified themselves.").embedColor(EmbedColor.SUCCESS).build());
    }
}

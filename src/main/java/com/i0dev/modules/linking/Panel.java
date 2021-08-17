package com.i0dev.modules.linking;

import com.i0dev.Bot;
import com.i0dev.config.MiscConfig;
import com.i0dev.modules.invite.InviteManager;
import com.i0dev.modules.moderation.CmdVerifyPanel;
import com.i0dev.object.*;
import com.i0dev.object.discordLinking.CodeCache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.From_DiscordCodeLinker;
import com.i0dev.utility.EmbedMaker;
import com.i0dev.utility.MessageUtil;
import com.i0dev.utility.SQLUtil;
import com.i0dev.utility.Utility;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class Panel extends SuperDiscordCommand {

    public void load() {
        addOption("title", "In-Game Linking");
        addOption("content", "To ensure a safe and mutually beneficial experience, all users are required to verify themselves as an actual human.\n\nBy doing this we require that you link your account in game before you have access to talk in the discord.\n\nClick the button below to generate a link code that you can then enter ingame with `/link code <code>`");
        addOption("pin", true);
        addOption("buttonLabel", "Click to link");
        addOption("buttonEmoji", "U+1F517");
    }

    @CommandData(commandID = "panel", identifier = "Link Panel", messageLength = 1, parentClass = LinkManager.class)
    public static void run(CommandEvent e) {
        Message msg = e.getChannel().sendMessageEmbeds(EmbedMaker.create(EmbedMaker.builder().content(getOption("content").getAsString()).authorImg(Bot.getJda().getSelfUser().getEffectiveAvatarUrl()).authorName(getOption("title").getAsString()).build()))
                .setActionRow(Button.success("BUTTON_LINK_PANEL", getOption("buttonLabel").getAsString()).withEmoji(Emoji.fromMarkdown(getOption("buttonEmoji").getAsString())))
                .complete();
        if (getOption("pin").getAsBoolean()) msg.pin().queue();
    }

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getButton() == null) return;
        if (!"BUTTON_LINK_PANEL".equalsIgnoreCase(e.getButton().getId())) return;
        if (e.getUser().isBot()) return;
        if (!Utility.isValidGuild(e.getGuild())) return;
        DPlayer dPlayer = DPlayer.getDPlayer(e.getUser());
        if (dPlayer.isBlacklisted()) return;

        if (dPlayer.isLinked()) {
            e.getInteraction().deferReply(true).setContent("You are already linked to the ign: {ign}".replace("{ign}", dPlayer.getMinecraftIGN())).queue();
            return;
        }

        e.getInteraction().deferReply(true).setContent("Check your direct messages for instructions!").queue();

        String code = Utility.GenerateRandomString(5);

        From_DiscordCodeLinker from_discordCodeLinker = new From_DiscordCodeLinker(e.getUser(), code);
        CodeCache.getInstance().getFrom_Discord_cache().add(from_discordCodeLinker);

        MessageUtil.sendPrivateMessage(e.getMessage(), e.getUser(), EmbedMaker.builder().field(
                new MessageEmbed.Field("Your code is: `{code}`".replace("{code}", code), "Use the command: `/link code {code}` in game to finish linking!".replace("{code}", code), false)
        ).build());

    }


}

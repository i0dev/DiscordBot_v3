package com.i0dev.utility;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class NicknameUtil {

    public static void modifyNicknameGlobally(User user, String nickname) {
        Utility.getAllowedGuilds().forEach(guild -> {
                    Member member = guild.getMember(user);
                    if (member == null || member.getEffectiveName().equalsIgnoreCase(nickname)) return;
                    System.out.println("Sent a request to change " + user.getAsTag() + "'s nickname");
                    try {
                        member.modifyNickname(nickname).queue();
                    } catch (Exception ignored) {
                    }
                }
        );
    }

    public static void modifyNickname(Member member, String nickname) {
        if (member == null || member.getEffectiveName().equalsIgnoreCase(nickname)) return;
        System.out.println("Sent a request to change " + member.getUser().getAsTag() + "'s nickname");
        try {
            member.modifyNickname(nickname).queue();
        } catch (Exception ignored) {
        }
    }
}

package com.i0dev.utility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class TempNicknameUtil {

    public static void modifyNickname(User user, String nickname) {
        Utility.getAllowedGuilds().forEach(guild -> {
                    Member member = guild.getMember(user);
                    if (member == null) return;
                    if (member.getEffectiveName().equals(nickname)) return;
                    System.out.println("Sent a request to change " + user.getAsTag() + "'s nickname");
                    member.modifyNickname(nickname).queue(null, throwable -> {
                        if (!(throwable instanceof HierarchyException)) {
                            throwable.printStackTrace();
                        }
                    });
                }
        );
    }


    public static boolean isNicknameSame(long userId, String nickname) {

        for (Guild guild : Utility.getAllowedGuilds()) {
            Member member = guild.getMemberById(userId);
            if (member == null) continue;
            if (member.getEffectiveName().equals(nickname)) return true;
        }
        return false;
    }
}

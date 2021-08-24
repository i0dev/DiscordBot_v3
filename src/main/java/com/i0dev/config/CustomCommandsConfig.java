package com.i0dev.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.*;

@Getter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CustomCommandsConfig {

    public static CustomCommandsConfig instance = new CustomCommandsConfig();

    public static CustomCommandsConfig get() {
        return instance;
    }

    //
    // Configuration starts
    //

    List<MessageSetting> customCommands = Collections.singletonList(new MessageSetting());

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class MessageSetting {

        List<String> callers = Collections.singletonList("Example Custom Message");
        boolean callersIgnoreCase = true;
        String content = "You can set any value to null and it will remove it from the config. If you ever need to see which fields you can use, you can always resort to looking at this custom message.";
        boolean reply = false;

        //Embed
        boolean embedEnabled = true;
        String title = "I recommend not deleting this custom message so you can use it for config help.";
        List<MessageEmbed.Field> fields = Collections.singletonList(new MessageEmbed.Field("header", "content", false));
        String footerText = "footer";
        String footerIconUrl = "iconUrl";
        String authorName = "name";
        String authorUrl = "url";
        String authorIconUrl = "iconUrl";
        String image = "url";
        String colorHex = "NORMAL_COLOR";
        boolean timestamp = false;
        String thumbnail = "url";


        @Getter
        @Setter
        public static class AuthorInfo {
            protected final String name;
            protected final String url;
            protected final String iconUrl;

            public AuthorInfo(String name, String url, String iconUrl) {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }
        }

        @Getter
        public static class Footer {
            protected final String text;
            protected final String iconUrl;

            public Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }
        }

    }

}


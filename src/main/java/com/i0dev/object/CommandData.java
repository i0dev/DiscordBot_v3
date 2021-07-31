package com.i0dev.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandData {
    String identifier();

    String commandID();

    long messageLength() default -1;

    long maxMessageLength() default -1;

    long minMessageLength() default -1;

    String usage() default "";

    boolean requireLinked() default false;

    boolean requirePluginMode() default false;

    Class<? extends AdvancedDiscordCommand> parentClass() default AdvancedDiscordCommand.class;
}

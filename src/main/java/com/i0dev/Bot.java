package com.i0dev;

import com.i0dev.utility.APIUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Scanner;

public class Bot {

    @Getter
    public static DiscordBot bot;

    @SneakyThrows
    public static void main(String[] args) {
        bot = new DiscordBot(false);
        bot.initialize();

        Scanner scanner = new Scanner(System.in);
        String incomingCommand = scanner.nextLine();

        switch (incomingCommand) {
            case "stop":
            case "end": {
                bot.shutdown();
            }
        }
    }
}
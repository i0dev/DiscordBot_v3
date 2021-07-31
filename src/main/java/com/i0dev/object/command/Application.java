package com.i0dev.object.command;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Application {

    private Long userID;
    private String userTag;
    private List<String> questions;
    private List<String> answers;
    private Long timeSubmitted;

    public Application(User user) {
        this.userID = user.getIdLong();
        this.userTag = user.getAsTag();
        this.answers = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.timeSubmitted = System.currentTimeMillis();
    }

    public Application() {
        this.userID = 0L;
        this.userTag = "";
        this.answers = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.timeSubmitted = System.currentTimeMillis();
    }
}

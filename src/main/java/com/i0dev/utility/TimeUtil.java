package com.i0dev.utility;


import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static Calendar calender = getCalenderTimeZone();

    private static Calendar getCalenderTimeZone() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return calendar;
    }

    public static int getYear() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.YEAR);
    }

    public static int getMonth() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.MONTH) + 1;
    }

    public static int getDay() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar addDay(int days) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, days);
        return calendar2;
    }

    public static int getDay(boolean incrementBy1) {
        if (!incrementBy1) {
            return getDay();
        }
        calender = Calendar.getInstance(calender.getTimeZone());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, 1);

        return calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public static int getTimeHours() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.HOUR_OF_DAY);
    }

    public static int getTimeMinutes() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.MINUTE);
    }

    public static int getTimeSeconds() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.SECOND);
    }

    public static int getTimeMilliseconds() {
        calender = Calendar.getInstance(calender.getTimeZone());
        return calender.get(Calendar.MILLISECOND);
    }

    public static String getTimeString() {
        int time = getTimeHours();
        int minutes = getTimeMinutes();
        return time > 12 ? time - 12 + ":" + (minutes < 10 ? "0" + minutes : minutes) + "PM" : time + ":" + (minutes < 10 ? "0" + minutes : minutes) + "AM";
    }

    public static String getTimeStringSeconds() {
        int time = getTimeHours();
        int minutes = getTimeMinutes();
        int seconds = getTimeSeconds();
        return time > 12 ? time - 12 + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + "PM" : time + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + "AM";
    }

    public static String formatSecondsAsTime(int seconds) {
        int minutes = seconds / 60;
        if (minutes < 1)
            return String.valueOf(seconds) + "s";
        else {
            int secs = seconds - minutes * 60;
            return String.valueOf(minutes) + "m " + String.valueOf(secs) + "s";
        }
    }

    public static String formatTimeFormat(long timePeriod) {
        long millis = timePeriod;

        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        if (days > 1) output += days + "d ";
        else if (days == 1) output += days + "d ";

        if (hours > 1) output += hours + "h ";
        else if (hours == 1) output += hours + " h ";

        if (minutes > 1) output += minutes + "m ";
        else if (minutes == 1) output += minutes + "m ";

        if (output.isEmpty()) return "None";

        return output.trim();
    }

    public static String formatPlayTime(long playTime) {
        long millis = playTime;

        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output += days + " days ";
        else if (days == 1) output += days + " day ";

        if (hours > 1) output += hours + " hours ";
        else if (hours == 1) output += hours + " hour ";

        if (minutes > 1) output += minutes + " minutes ";
        else if (minutes == 1) output += minutes + " minute ";

        if (seconds > 1) output += seconds + " seconds";
        else if (seconds == 1) output += seconds + " second";

        if (output.isEmpty()) return "0 seconds";

        return output;
    }

    public static String formatTime(long timePeriod) {
        long millis = timePeriod;

        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output += bold(days) + " days, ";
        else if (days == 1) output += bold(days) + " day, ";

        if (hours > 1) output += bold(hours) + " hours, ";
        else if (hours == 1) output += bold(hours) + " hour, ";

        if (minutes > 1) output += bold(minutes) + " minutes, ";
        else if (minutes == 1) output += bold(minutes) + " minute, ";

        if (seconds > 1) output += bold(seconds) + " seconds ";
        else if (seconds == 1) output += bold(seconds) + " second ";

        if (output.isEmpty()) return "just now ";

        if (output.endsWith(",")){
            output = output.substring(0,output.length()-1);
        }

        return output;
    }

    static String bold(long t) {
        return "**" + t + "**";
    }

    public static String formatTime(int seconds) {
        int days = seconds / 86400;
        int hours = seconds % 86400 / 3600;
        int minutes = seconds % 86400 % 3600 / 60;

        StringBuilder sb = new StringBuilder();

        if (days != 0) {
            if (days > 1) sb.append(days + " days ");
            else if (days == 1) sb.append("1 day ");
        }

        if (hours != 0) {
            if (hours > 1) sb.append(hours + " hours ");
            else if (hours == 1) sb.append("1 hour ");
        }

        if (minutes != 0) {
            if (minutes > 1) sb.append(minutes + " minutes ");
            else if (minutes == 1) sb.append("1 minute ");
        }

        if (sb.toString().isEmpty()) return "just now ";

        return sb.toString();
    }


}
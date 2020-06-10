package com.conquestmc.core.util;

public class TimeUtil {

    public static String formatTimeToFormalDate(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String time = days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;

        return time;
    }
}

package com.conquestmc.core.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static String formatTimeToFormalDate(long milliseconds) {
        final long dy = TimeUnit.MILLISECONDS.toDays(milliseconds);
        final long hr = TimeUnit.MILLISECONDS.toHours(milliseconds)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        final long min = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        return String.format("%dD %dH %dM %dS", dy, hr, min, sec);
    }
}
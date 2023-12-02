package com.eclipse.mxd.util;

import java.util.Date;
import java.util.TimeZone;


public class DateTimeUtil {

    public static Date getCurrentDateTimeInAsiaKolkata() {
        // Set the time zone to Asia/Kolkata
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Kolkata");

        // Get the current time in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Adjust the current time to the specified time zone
        int offset = timeZone.getOffset(currentTimeMillis);
        long adjustedTimeMillis = currentTimeMillis + offset;

        // Create a new Date object using the adjusted time
        return new Date(adjustedTimeMillis);
    }

}

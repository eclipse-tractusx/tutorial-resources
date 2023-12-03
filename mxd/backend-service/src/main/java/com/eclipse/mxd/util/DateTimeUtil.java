package com.eclipse.mxd.util;

import java.util.Date;

public class DateTimeUtil {

    public static Date getCurrentDateTimeInAsiaKolkata() {
        long currentTimeMillis = System.currentTimeMillis();
        // Create a new Date object using the current time
        return new Date(currentTimeMillis);
    }

}

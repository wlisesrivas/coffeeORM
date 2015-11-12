package com.coffeeorm.util;

import java.util.Calendar;

/**
 * Created by wrivas on 9/24/14.
 */
public class UtilDB {

    public static String escape(String str) {
        return str.replaceAll("'|\"", "\'");
    }

    /**
     * Current Timestamp
     * @return
     */
    public static java.sql.Timestamp currentTimestamp() {
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        return new java.sql.Timestamp(now.getTime());
    }

    /**
     * Get current Date.
     * @return
     */
    public static java.util.Date currentDate() {
        return Calendar.getInstance().getTime();
    }

}

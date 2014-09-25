package com.coffeeorm.util;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * @author wlises.rivas
 */
public class Debug {

    private static boolean enabled = true;

    private static String Prefix = "CoffieeORM";

    private static SimpleDateFormat dateFormat;

    static {
        if(!Config.getInstance().getProperty(Config.DEBUG).equals("true")) {
            Debug.Disabled();
        }
        dateFormat = getDateFormat();
    }

    private static SimpleDateFormat getDateFormat() {
        if( dateFormat == null )
            dateFormat = new SimpleDateFormat("y/w/d k:m:s.S");
        return dateFormat;
    }

    /**
     * Standard output log
     *
     * @param obj
     * @param error
     */
    public static void log(Object obj, boolean error) {
        if(!enabled) return;
        if (error)
            System.err.printf("%s[%s] -> %s\n",Prefix, dateFormat.format(new Date()), obj.toString());
        else
            System.out.printf("%s[%s] -> %s\n",Prefix, dateFormat.format(new Date()), obj.toString());
    }

    /**
     * Standard output log
     *
     * @param obj
     */
    public static void log(Object obj) {
        Debug.log(obj, false);
    }

    public static void Enabled() {enabled = true;}
    public static void Disabled() {enabled = false;}
}

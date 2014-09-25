package com.coffeeorm.util;

/**
 * Created by wrivas on 9/24/14.
 */
public class Db {

    public static String escape(String str) {
        return str.replaceAll("'|\"", "\'");
    }
}

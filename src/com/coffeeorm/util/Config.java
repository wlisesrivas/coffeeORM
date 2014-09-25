package com.coffeeorm.util;

import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * Created by wrivas on 9/24/14.
 */
class Config extends Properties {

    public static final String DEBUG = "debug";
    public static final String DB_HOST = "db_host";
    public static final String DB_NAME = "db_name";
    public static final String DB_USER = "db_host";
    public static final String DB_PASS = "db_pass";

    private static Config instance = null;

    /**
     * Singleton Instance.
     *
     * @return CoffeeORM
     */
    public static Config getInstance(String configFile) {
        if (instance == null)
            instance = new Config(configFile);
        return instance;
    }

    public static Config getInstance() {
        return getInstance("./config.ini");
    }

    public Config(String configFile) {
        super();
        try {
            this.load(new FileInputStream(configFile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Config() {
        this("./config.ini");
    }

}
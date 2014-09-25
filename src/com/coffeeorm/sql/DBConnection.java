package com.coffeeorm.sql;
import com.coffeeorm.util.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wrivas on 9/24/14.
 */
public class DBConnection {

    private Properties properties;

    private static DBConnection instance = null;

    /**
     * Singleton Instance.
     *
     * @return CoffeeORM
     */
    public static DBConnection getInstance() {
        if( instance == null )
            instance = new DBConnection();
        return instance;
    }

    public DBConnection() {

    }

}


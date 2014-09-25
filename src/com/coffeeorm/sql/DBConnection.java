package com.coffeeorm.sql;

import com.coffeeorm.util.Config;

import static com.coffeeorm.util.Debug.log;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by wrivas on 9/24/14.
 */
public class DBConnection {

    private Properties properties;

    private static DBConnection instance = null;

    private final String HOST;
    private final String PORT;
    private final String DATABASE;
    private final String USER;
    private final String PASS;

    private static Connection connection;

    private static Statement statement;

    /**
     * Singleton Instance.
     *
     * @return CoffeeORM
     */
    public static DBConnection getInstance() {
        if (instance == null)
            instance = new DBConnection();
        return instance;
    }

    public DBConnection() {
        Config config = Config.getInstance();
        HOST = config.getProperty(Config.DB_HOST);
        PORT = config.getProperty(Config.DB_PORT);
        DATABASE = config.getProperty(Config.DB_NAME);
        USER = config.getProperty(Config.DB_USER);
        PASS = config.getProperty(Config.DB_PASS);
        connect();
    }

    private void connect() {
        if (connection != null) {
            return;
        }
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"
                    + HOST + ":" + PORT + "/" + DATABASE, USER, PASS);
            statement = connection.createStatement();
            log("Connection to DB successfully.");
        } catch (SQLException ex) {
            log("Couldn't not connect to database", true);
            log(ex.getMessage(), true);
            System.exit(0);
        }
    }

    public boolean closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
                log("Connection to DB has been closed.");
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static Statement getStatement() {
        return statement;
    }

    public static Connection getConnection() {
        return connection;
    }

}

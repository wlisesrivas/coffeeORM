package com.coffeeorm.sql;

import static com.coffeeorm.util.Debug.log;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * ActiveRecord A simple Java Active Record like CodeIgniter
 *
 * @author wlises.rivas
 */
public class ActiveRecord extends QueryBuilder {

    /**
     * Initialize database credentials
     *
     * @param host
     * @param port
     * @param database
     * @param user
     * @param pass
     */
    public ActiveRecord(String host, String port, String database,
                        String user, String pass) {
        this._host = host;
        this._port = port;
        this._database = database;
        this._user = user;
        this._pass = pass;

        log("Loaded");
        _connect();
        log("Connection to database successfully");
    }

    /**
     *
     * @param connection
     */
    public ActiveRecord(Connection connection) {
        log("Loaded");
        _connect(connection);
        log("Connection to database successfully");
    }


    // ------------------- Active Record Query -------------------
    // - SELECT
    public ActiveRecord select(String str) {
        this._select = str;
        return this;
    }

    public ActiveRecord from(String table) {
        this._table = table;
        return this;
    }

    // - Join
    public ActiveRecord join(String table, String closure, String joinType) {
        this._join.put(table + "|" + joinType, closure);
        return this;
    }

    public ActiveRecord join(String table, String closure) {
        return this.join(table, closure, "inner");
    }

    // - WHERE
    public ActiveRecord where(String column, String value, boolean scape) {
        this._where.put(column, (scape ? "'" + value + "'" : value));
        return this;
    }

    public ActiveRecord where(String column, String value) {
        return this.where(column, value, true);
    }

    public ActiveRecord where(String column, int value) {
        return this.where(column, String.valueOf(value), true);
    }

    public ActiveRecord where(String column, int value, boolean scape) {
        return this.where(column, String.valueOf(value), scape);
    }

    public ActiveRecord where_in(String column, String... clause) {
        this._where_in.put(column, clause);
        return this;
    }

    // - LIKE

    /**
     * Like closure
     *
     * @param column
     * @param value
     * @param match  | L, R, [default B]: %Left, Right% OR %Both%
     * @return
     */
    public ActiveRecord like(String column, String value, String match) {
        String s = match.toUpperCase();
        if (s.equals("L")) {
            value = "%" + value;
        } else if (s.equals("R")) {
            value = value + "%";
        } else {
            value = "%" + value + "%";
        }

        this._like.put(column, value);
        return this;
    }

    public ActiveRecord like(String column, String value) {
        return this.like(column, value, "");
    }

    // - OR LIKE

    /**
     * Like closure
     *
     * @param column
     * @param value
     * @param math   | L, R, [default B]: %Left, Right% OR %Both%
     * @return
     */
    public ActiveRecord or_like(String column, String value, String math) {

        String s = math.toUpperCase();
        if (s.equals("L")) {
            value = "%" + value;
        } else if (s.equals("R")) {
            value = value + "%";
        } else {
            value = "%" + value + "%";
        }

        this._or_like.put(column, value);
        return this;
    }

    public ActiveRecord or_like(String column, String value) {
        return this.or_like(column, value, "");
    }

    // - LIMIT
    public ActiveRecord limit(int limit) {
        return this.limit(limit, 0);
    }

    public ActiveRecord limit(int limit, int offset) {
        this._limit = String.valueOf(offset) + "," + String.valueOf(limit);
        return this;
    }

    // - ORDER BY
    public ActiveRecord orderBy(String syntax) {
        this._order_by = syntax;
        return this;
    }

    public ActiveRecord orderBy(String column, String value) {
        this._order_by = column + " " + value;
        return this;
    }

    // - GROUP BY
    public ActiveRecord groupBy(String syntax) {
        this._group_by = syntax;
        return this;
    }

    public ActiveRecord groupBy(String column, String value) {
        this._group_by = column + " " + value;
        return this;
    }

    /**
     * Set database prefix
     *
     * @param str
     */
    public void setDbPrefix(String str) {
        this._db_prefix = str;
    }

	/*
     * public List get(Class<? extends Serializable> table) { return
	 * get(table.getAnnotation(Table.class).name()); }
	 */

    /**
     * Get list of records List<HashMap<String,String>>, if not record found
     * null is returned
     *
     * @return List<HashMap<String,String>>
     */

    public List get() {
        return get(this._table);
    }

    /**
     * Get list of records List<HashMap<String,String>>, if not record found
     * null is returned
     *
     * @return List<HashMap<String,String>>
     */
    public List get(String table) {

        this._table = table;

        String SQL = prepareQuery();
        try {
            _resultset = _statement.executeQuery(SQL);
            List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

            while (_resultset.next()) {

                ResultSetMetaData rsData = _resultset.getMetaData();
                HashMap columName = new HashMap();
                int totalColumn = rsData.getColumnCount();

                for (int i = 1; i <= totalColumn; i++) {
                    columName.put(rsData.getColumnLabel(i),
                            _resultset.getString(i));
                }
                result.add(columName);
            }
            log(String.format("Query executed: \"%s\"", SQL));
            return result;

        } catch (SQLException ex) {
            log(ex.getMessage(), true);
            log(SQL, true);
        }
        return null;
    }

    /**
     * Get result count
     *
     * @return
     */
    // public int get_count() {
    // return get_count(this._table.);
    // }
    /**
     * Get result count
     *
     * @param table
     * @return
     */
    /*
	 * public int get_count(Class<Serializable> table) { List result =
	 * this.get(table); if (result != null) { return result.size(); } return 0;
	 * }
	 */


    /**
     * Connect to database
     */
    protected void _connect() {
        if (_db_connection != null) {
            return;
        }
        try {
            _db_connection = DriverManager.getConnection("jdbc:mysql://"
                    + _host + ":" + _port + "/" + _database, _user, _pass);
            _statement = _db_connection.createStatement();
        } catch (SQLException ex) {
            log("Could not connect to database", true);
            log(ex.getMessage(), true);
            System.exit(0);
        }
    }

    private void _connect(Connection connection) {
        try {
            _db_connection = connection;
            _statement = _db_connection.createStatement();
        } catch (SQLException ex) {
            log("Could not connect to database", true);
            log(ex.getMessage(), true);
            System.exit(0);
        }
    }

    /**
     * Close active connection
     */
    protected void _closeConnection() {
        try {
            if (_db_connection != null) {
                _db_connection.close();
                _db_connection = null;
            }
        } catch (Exception ex) {
        }
    }


    private String getParams(List<? extends Serializable> params,
                             boolean withQuotes) {

        StringBuilder sb = new StringBuilder();

        for (Serializable param : params) {

            if (withQuotes) {

                if (param instanceof String) {
                    sb.append(String.format("\'%s\',", param));
                } else {
                    sb.append(String.format("%s,", param));
                }

            } else {
                sb.append(String.format("%s,", param));
            }

        }
        return sb.substring(0, sb.length() - 1);
    }
}

package com.coffeeorm.sql;

import com.coffeeorm.util.Db;

import static com.coffeeorm.util.Debug.log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author wlises.rivas
 */
public class QueryBuilder {

    protected static Connection _db_connection = null;
    protected Statement _statement = null;
    protected ResultSet _resultset = null;
    protected Map<String, String> _join = new HashMap<String, String>();
    protected Map<String, String> _where = new HashMap<String, String>();
    protected Map<String, String[]> _where_in = new HashMap<String, String[]>();
    protected Map<String, String> _like = new HashMap<String, String>();
    protected Map<String, String> _or_like = new HashMap<String, String>();

    protected String // Active record
            _db_prefix = "",
            _select = "*",
            _group_by = "",
            _order_by = "",
            _limit = "",
            // Database Configuration
            _host = "localhost",
            _port = "3306",
            _database = null,
            _user = "root",
            _pass = "";

    protected String _table;

    /**
     * Prepare Query build
     *
     * @return String SQL generated
     */
    protected String prepareQuery() {
        Iterator it;
        String SQL = "SELECT " + this._select + " FROM " + this._table;

        // JOIN
        if (_join.size() > 0) {
            it = _join.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry map = (Map.Entry) it.next();
                // Get table and type of join... e.g: table|left
                String[] joinType = map.getKey().toString().split("\\|");
                SQL += " " + joinType[1].toUpperCase() + " JOIN " + joinType[0]
                        + " ON " + map.getValue();
            }

        }

        boolean needAnd = false;

        // WHERE
        if (_where.size() > 0) {

            SQL += " WHERE";
            it = _where.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry map = (Map.Entry) it.next();
                SQL += (needAnd ? " AND " : " ") + map.getKey() + " = "
                        + map.getValue();

                needAnd = true;
            }

        }

        // WHERE
        if (_where_in.size() > 0) {

            if (!needAnd) {
                SQL += " WHERE";
            }
            it = _where_in.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry map = (Map.Entry) it.next();
                StringBuilder sb = new StringBuilder();

                for (String str : _where_in.get(map.getKey())) {
                    sb.append("'" + str + "',");
                }

                sb.setLength(sb.length() - 1);

                SQL += (needAnd ? " AND " : " ") + map.getKey() + " IN("
                        + sb.toString() + ")";
            }
            needAnd = true;
        }

        // LIKE
        if (_like.size() > 0) {

            if (!needAnd) {
                SQL += " WHERE";
            }

            it = _like.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry map = (Map.Entry) it.next();
                SQL += (needAnd ? " AND " : " ") + map.getKey() + " LIKE('"
                        + map.getValue() + "')";
            }
            needAnd = true;
        }

        // OR LIKE
        if (_or_like.size() > 0) {

            if (!needAnd) {
                SQL += " WHERE";
            }

            it = _or_like.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry map = (Map.Entry) it.next();
                SQL += (needAnd ? " OR " : " ") + map.getKey() + " LIKE('"
                        + map.getValue() + "')";
            }
            needAnd = true;
        }

        // ORDER BY
        if (_order_by.length() > 0) {
            SQL += " ORDER BY " + _order_by;
        }
        // GROUP BY
        if (_group_by.length() > 0) {
            SQL += " GROUP BY " + _group_by;
        }
        // LIMIT
        if (_limit.length() > 0) {
            SQL += " LIMIT " + _limit;
        }
        this.clearQuery();
        return SQL;
    }

    public QueryBuilder clearQuery() {

        _where_in.clear();
        _join.clear();
        _where.clear();
        _like.clear();
        _or_like.clear();
        _select = "*";
        _group_by = "";
        _order_by = "";

        return this;
    }

    /**
     * Escape SQL parameters, removing ' " # -- ; `
     *
     * @param value
     * @return String
     */
    protected static String escape(String value) {
        return Db.escape(value);
    }

    /**
     * Insert record
     *
     * @param table
     * @param obj
     * @return boolean TRUE on success, FALSE on failure
     */
    public boolean insert(String table, HashMap<String, String> obj) {

        if (obj.size() == 0)
            return false;

        String sql = "INSERT INTO `" + this._db_prefix + table + "`(%s) VALUES(%s)";

        Iterator it = obj.entrySet().iterator();
        String columns = "", values = "";
        while (it.hasNext()) {
            Map.Entry map = (Map.Entry) it.next();
            columns += "`" + map.getKey() + "`,";
            values += map.getValue() == null ? "NULL," : "'" + QueryBuilder.escape(map.getValue().toString()) + "',";
        }

        sql = String.format(sql, columns.substring(0, columns.length() - 1), values.substring(0, values.length() - 1));

        try {
            _statement.executeUpdate(sql);
            log(String.format("Query executed: \"%s\"", sql));
            return true;

        } catch (SQLException ex) {
            log(ex.getMessage(), true);
            log(sql, true);
            return false;
        }

    }

}

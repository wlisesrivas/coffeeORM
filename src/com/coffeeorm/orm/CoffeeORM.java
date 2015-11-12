package com.coffeeorm.orm;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;
import com.coffeeorm.exceptions.InvalidEntityException;
import com.coffeeorm.exceptions.OrmSaveException;
import com.coffeeorm.exceptions.OrmDeleteException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.coffeeorm.reflectcache.EntityCache;
import com.coffeeorm.sql.ActiveRecord;
import com.coffeeorm.sql.DBConnection;
import com.coffeeorm.util.UtilDB;

import javax.rmi.CORBA.Util;

import static com.coffeeorm.util.Debug.log;

/**
 * Created by wrivas on 9/24/14.
 */
public class CoffeeORM {

    private EntityCache entityCache;
    private static CoffeeORM instance = null;
    private static DBConnection dbConnection = null;
    private static ActiveRecord activeRecord = null;

    /**
     * Singleton Instance.
     *
     * @return CoffeeORM
     */
    public static CoffeeORM getInstance() {
        if (instance == null)
            instance = new CoffeeORM();
        return instance;
    }

    public CoffeeORM() {
        dbConnection = DBConnection.getInstance();
        entityCache = EntityCache.getInstance();
        activeRecord = new ActiveRecord(dbConnection.getConnection());
    }

    /**
     *
     * @return
     */
    public ActiveRecord activeRecord() {return activeRecord; }

    /**
     * Check if is a valid entity.
     *
     * @param entity
     * @return
     */
    private void checkValidEntity(Object entity) throws InvalidEntityException {
        Class entityClass = entity.getClass();
        // TODO verify if is a public class
        if (entityClass.getAnnotation(Entity.class) == null)
            throw new InvalidEntityException("Invalid entity. Missing @Entity(TableName = \"TABLE_NAME\")");

        if (getPrimaryField(entity.getClass()) == null)
            throw new InvalidEntityException("The entity specified does not have Primary Key defined.");
    }

    /**
     * Save an entity to database.
     * If the object has the primary key with a value, the record will be updated,
     * Otherwise inserted.
     *
     * @param entity
     * @throws OrmSaveException
     * @throws InvalidEntityException
     */
    public void save(Object entity) throws OrmSaveException, InvalidEntityException {
        checkValidEntity(entity);

        java.lang.Class entityClass = entity.getClass();
        String tableName = getTableName(entityClass);
        ArrayList<Field> fields = getFieldsAndValues(entity);

        // Verified if is updating a record.
        boolean isUpdate = false;
        String fieldPrimary = getPrimaryField(entity.getClass());
        for (Field field : fields) {
            if (field.name == fieldPrimary && !(field.value.equals(String.valueOf("0")) || field.value == "")) {
                isUpdate = true;
                break;
            }
        }

        String SQL = isUpdate ? "UPDATE `" + tableName + "` SET %s WHERE %s"
                : "INSERT INTO `" + tableName + "`(%s) VALUES(%s)";

        String sqlStrLeft = "", sqlStrRight = "";

        // If Primary-key Autoincrement field is found,
        // then mark to modify after inserted into the entity instance.
        String autoIncrement = null;

        for (Field field : fields) {
            if (!field.tableField.AutoIncrement()) {
                if (isUpdate) {
                    sqlStrLeft += "`" + field.name + "` = " +
                            (field.value == null ?
                                    "NULL," : "'" + UtilDB.escape(field.value) + "',");
                } else {
                    sqlStrLeft += "`" + field.name + "`,";
                    sqlStrRight += (field.value == null || field.value.equals("null") ?
                            "NULL," : "'" + UtilDB.escape(field.value) + "',");
                }
            }
            if (!isUpdate && (field.tableField.AutoIncrement() &&
                    field.tableField.Index() == TableField.Index.PRIMARY)) {
                autoIncrement = field.name;
            }
            if (isUpdate && field.tableField.Index() == TableField.Index.PRIMARY) {
                sqlStrRight = "`" + field.name + "` = '" + UtilDB.escape(field.value) + "',";
            }
        }
        // Removing last comma (,)
        sqlStrLeft = sqlStrLeft.substring(0, sqlStrLeft.length() - 1);

        sqlStrRight = sqlStrRight.substring(0, sqlStrRight.length() - 1);

        // Format SQL with columns and values
        SQL = String.format(SQL, sqlStrLeft, sqlStrRight);
        try {
            dbConnection.getStatement().execute(SQL);
            log(String.format("Query executed: \"%s\"", SQL));
            if (autoIncrement != null) {
                setPrimaryKeyValue(entity);
            }
        } catch (SQLException e) {
            throw new OrmSaveException(e.getMessage());
        }

    }

    private String getTableName(java.lang.Class entity) {
        return ((Entity) entity.getAnnotation(Entity.class)).TableName();
    }

    /**
     * Get fields mapped to the table.
     *
     * @param className
     * @return
     */
    private ArrayList<Field> getFieldsAndValues(Object entity) {
        Class entityClass = entity.getClass();
        String className = entityClass.toString();
        ArrayList<Field> fields = entityCache.getOrmFields(className);

        if(null == fields) fields = new ArrayList<Field>();

        ArrayList<java.lang.reflect.Field> reflectFieldsList = entityCache.getReflectFields(className);
        java.lang.reflect.Field reflectFields[];

        if(reflectFieldsList != null)
            reflectFields = (java.lang.reflect.Field[]) reflectFieldsList.toArray();
        else
            reflectFields = entityClass.getFields();

        for (java.lang.reflect.Field field : reflectFields) {
            TableField tableField = (TableField) field.getAnnotation(TableField.class);
            if (tableField != null) {
                try {
                    fields.add(new Field(field.getName(), String.valueOf(field.get(entity)), tableField));
                } catch(NullPointerException e) {
                    fields.add(new Field(field.getName(), "", tableField));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return fields;
    }

    /**
     * Get last ID generated.
     *
     * @return int
     */
    public int getLastInsertId() {
        String SQL = "SELECT last_insert_id() AS id";
        try {
            ResultSet resultSet = dbConnection.getStatement().executeQuery(SQL);
            int id = 0;
            if (resultSet.next()) {
                id = resultSet.getInt("id");
                resultSet.close();
                log("Last ID generated: " + id);
            }
            return id;
        } catch (SQLException e) {
            log(e.getMessage(), true);
            return 0;
        }
    }

    /**
     * Delete a records based on her Primary-Key value.
     * @param entity
     * @return TRUE if success, FALSE on error
     * @throws OrmDeleteException
     * @throws InvalidEntityException
     */
    public boolean delete(Object entity) throws OrmDeleteException, InvalidEntityException {
        if(null == entity) return false;

        checkValidEntity(entity);

        String primaryKey = getPrimaryField(entity.getClass());
        String primaryKeyValue = getPrimaryKeyValue(entity);

        try {
            DBConnection.getStatement().executeUpdate(String.format("DELETE FROM `%s` WHERE `%s` = %s;",
                    getTableName(entity.getClass()), primaryKey, UtilDB.escape(primaryKeyValue)));
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    /**
     * Get primary key field name.
     *
     * @param entity
     * @return String
     */
    private String getPrimaryField(java.lang.Class entity) {
        String fieldFromCache = entityCache.getEntityPrimaryKey(entity.toString());
        if (fieldFromCache != null)
            return fieldFromCache;
        for (java.lang.reflect.Field field : entity.getFields()) {
            TableField tableField = (TableField) field.getAnnotation(TableField.class);
            if (tableField != null && tableField.Index() == TableField.Index.PRIMARY) {
                entityCache.setEntityPrimaryKey(entity.toString(), field.getName());
                return field.getName();
            }
        }
        return null;
    }

    /**
     * Set the primary key for a specified entity (object).
     *
     * @param entity
     */
    private void setPrimaryKeyValue(Object entity) {
        String primaryField = getPrimaryField(entity.getClass());
        for (java.lang.reflect.Field field : entity.getClass().getFields()) {
            if (field.getName().equals(primaryField)) {
                try {
                    field.set(entity, getLastInsertId());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get the primary key value for a specified entity (object).
     *
     * @param entity
     */
    private String getPrimaryKeyValue(Object entity) {
        String primaryField = getPrimaryField(entity.getClass());
        for (java.lang.reflect.Field field : entity.getClass().getFields()) {
            if (field.getName().equals(primaryField)) {
                try {
                    return field.get(entity).toString();
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Get first row by primary-key
     *
     * @param entity
     * @param primaryKey
     * @return
     */
    public Object getFirst(java.lang.Class entity, String primaryKey) {
        return this.getFirstBy(entity, primaryKey, getPrimaryField(entity));
    }
    /**
     * Get first row by primary-key
     *
     * @param entity
     * @param primaryKey
     * @return
     */
    public Object getFirst(java.lang.Class entity, int primaryKey) {
        return getFirst(entity, String.valueOf(primaryKey));
    }

    /**
     * Get first by fieldName.
     *
     * @param entity
     * @param value
     * @param fieldName
     * @return
     */
    public Object getFirstBy(java.lang.Class entity, String value, String fieldName) {

        String sql = String.format("SELECT * FROM `%s` WHERE `%s` = '%s' LIMIT 1;", getTableName(entity), fieldName, UtilDB.escape(value));

        ArrayList<Object> rows = execute(sql, entity);
        return rows.size() > 0 ? rows.get(0) : null;
    }

    /**
     * Get first by fieldName.
     * @param entity
     * @param value
     * @param fieldName
     * @return
     */
    public Object getFirstBy(java.lang.Class entity, int value, String fieldName) {
        return this.getFirstBy(entity, String.valueOf(value), fieldName);
    }

    /**
     * Execute a query row and return the object initialized.
     * @param sql
     * @param entity
     * @return Object
     */
    private ArrayList<Object> execute(String sql, java.lang.Class entity) {
        ArrayList<Object> objects = new ArrayList<Object>();
        try {
            ResultSet resultSet = dbConnection.getStatement().executeQuery(sql);
            while (resultSet.next()) {
                Object rowObject = Class.forName(entity.getCanonicalName()).newInstance();
                for (java.lang.reflect.Field field : rowObject.getClass().getFields()) {
                    // Add fields to cache
                    fillFieldByType(field, resultSet, rowObject, field.getType().toString());
                }
                objects.add(rowObject);
            }
            resultSet.close();
            return objects;
        } catch (SQLException e) {
            log(e.getMessage(), true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Statement getStatement() {
        return dbConnection.getStatement();
    }

    /**
     * Fill object variables by her type.
     *
     * @param field
     * @param resultSet
     * @param entity
     * @param dataType
     * @throws SQLException
     */
    private void fillFieldByType(java.lang.reflect.Field field, ResultSet resultSet, Object entity, String dataType)
            throws SQLException, IllegalAccessException {
        // Fill fields types
        switch (dataType) {
            case "int":
                field.set(entity, resultSet.getInt(field.getName()));
                break;
            case "float":
                field.set(entity, resultSet.getFloat(field.getName()));
                break;
            case "double":
                field.set(entity, resultSet.getDouble(field.getName()));
                break;
            case "class java.sql.Timestamp":
                field.set(entity, resultSet.getTimestamp(field.getName()));
                break;
            case "class java.util.Date":
            case "class java.sql.Date":
                field.set(entity, resultSet.getDate(field.getName()));
                break;
            case "class java.sql.Time":
                field.set(entity, resultSet.getTime(field.getName()));
                break;
            default:
                // String
                field.set(entity, resultSet.getString(field.getName()));
        }
    }

    /**
     * Start transaction.
     * Note: startTransaction must be called after this.
     */
    public void startTransaction() {
        try {
            dbConnection.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Commit changes.
     * Note: startTransaction must be called after this.
     */
    public void commit() {
        try {
            dbConnection.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rollback changes.
     * Note: startTransaction must be called after this.
     */
    public void rollback() {
        try {
            dbConnection.getConnection().rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

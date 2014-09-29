package com.coffeeorm.orm;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;
import com.coffeeorm.exceptions.InvalidEntityException;
import com.coffeeorm.exceptions.OrmSaveException;
import com.coffeeorm.exceptions.OrmDeleteException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.coffeeorm.reflectcache.EntityCache;
import com.coffeeorm.sql.DBConnection;
import com.coffeeorm.util.Db;

import static com.coffeeorm.util.Debug.log;

/**
 * Created by wrivas on 9/24/14.
 */
public class CoffeeORM {
    private EntityCache entityCache;
    private static CoffeeORM instance = null;
    private static DBConnection dbConnection = null;

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
    }

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
            throw new InvalidEntityException("Entity invalid.");

        if (getFieldPrimary(entity) == null)
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

        String tableName = getTableName(entity);
        ArrayList<Field> fields = getFieldsAndValues(entity);

        // Verified if is updating a record.
        boolean isUpdate = false;
        String fieldPrimary = getFieldPrimary(entity);
        for (Field field : fields) {
            if (field.name == fieldPrimary && !(field.value.equals(String.valueOf("0")) || field.value == "")) {
                isUpdate = true;
                break;
            }
        }

        String SQL = isUpdate ? "UPDATE `" + tableName + "` SET %s WHERE %s"
                : "INSERT INTO `" + tableName + "`(%s) VALUES(%s)";

        String sqlStrLeft = "", sqlStrRight = "";

        // If Primary Autoincrement field is found,
        // then mark to modify after inserted into the entity instance.
        String autoIncrement = null;

        for (Field field : fields) {
            if (!field.tableField.AutoIncrement()) {
                if (isUpdate) {
                    sqlStrLeft += "`" + field.name + "` = " +
                            (field.value == null ?
                                    "NULL," : "'" + Db.escape(field.value) + "',");
                } else {
                    sqlStrLeft += "`" + field.name + "`,";
                    sqlStrRight += (field.value == null ?
                            "NULL," : "'" + Db.escape(field.value) + "',");
                }
            }
            if (!isUpdate && (field.tableField.AutoIncrement() &&
                    field.tableField.Index() == TableField.Index.PRIMARY)) {
                autoIncrement = field.name;
            }
            if (isUpdate && field.tableField.Index() == TableField.Index.PRIMARY) {
                sqlStrRight = "`" + field.name + "` = '" + Db.escape(field.value) + "',";
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

    private String getTableName(Object entity) {
        return ((Entity) entity.getClass().getAnnotation(Entity.class)).TableName();
    }

    /**
     * Get fields mapped to the table.
     *
     * @param entity
     * @return
     */
    private ArrayList<Field> getFieldsAndValues(Object entity) {
        ArrayList<Field> fields = new ArrayList<>();

        for (java.lang.reflect.Field field : entity.getClass().getFields()) {
            TableField tableField = (TableField) field.getAnnotation(TableField.class);
            if (tableField != null) {
                try {
                    fields.add(new Field(field.getName(), field.get(entity).toString(), tableField));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        entityCache.setFields(entity, fields);
        return fields;
    }

    /**
     * Get last ID generated.
     *
     * @return int
     */
    private int getLastInsertId() {
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

    public void delete(Object entity) throws OrmDeleteException, InvalidEntityException {
        checkValidEntity(entity);

    }

    /**
     * Get primary key field name.
     *
     * @param entity
     * @return String
     */
    private String getFieldPrimary(Object entity) {
        String fieldFromCache = entityCache.getEntityPrimaryKey(entity);
        if (fieldFromCache != null)
            return fieldFromCache;
        for (java.lang.reflect.Field field : entity.getClass().getFields()) {
            TableField tableField = (TableField) field.getAnnotation(TableField.class);
            if (tableField != null && tableField.Index() == TableField.Index.PRIMARY) {
                entityCache.setEntityPrimaryKey(entity, field.getName());
                return field.getName();
            }
        }
        return null;
    }

    private void setPrimaryKeyValue(Object entity) {
        for (java.lang.reflect.Field field : entity.getClass().getFields()) {
            if (field.getName().equals(getFieldPrimary(entity))) {
                try {
                    field.set(entity, getLastInsertId());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get an entity object from database.
     *
     * @param entity
     * @param value
     * @param fieldName
     * @return
     */
    public Object getFirstBy(Object entity, String value, String fieldName) {
        ArrayList<Field> fields = entityCache.getFields(entity);
        if (fields == null) {
            getFieldsAndValues(entity);
            fields = entityCache.getFields(entity);
        }
        String SQL = String.format("SELECT * FROM `%s` WHERE `%s` = '%s'",
                getTableName(entity),
                getFieldPrimary(entity), value);
        Class cEntity;
        Object object = new Object();
        try {
            ResultSet resultSet = dbConnection.getStatement().executeQuery(SQL);
            if (resultSet.next()) {
                cEntity = Class.forName(entity.getClass().getCanonicalName());
                object = cEntity.newInstance();
                for (java.lang.reflect.Field field : entity.getClass().getFields()) {
                    try {
                        log(field.toString());
                        if (field.getType().toString().equals("int")) {
                            // int
                            field.set(entity, resultSet.getInt(field.getName()));
                        } else {
                            // String
                            field.set(entity, resultSet.getString(field.getName()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (resultSet.next()) {
                resultSet.getInt("id");
                resultSet.close();
            }
            return object;
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

    public Object getFirstBy(Object entity, String value) {
        return this.getFirstBy(entity, value, getFieldPrimary(entity));
    }
}

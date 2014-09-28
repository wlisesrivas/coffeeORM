package com.coffeeorm.orm;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;
import com.coffeeorm.exceptions.InvalidEntityException;
import com.coffeeorm.exceptions.OrmSaveException;
import com.coffeeorm.exceptions.OrmDeleteException;

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
        if(entityClass.getAnnotation(Entity.class) == null)
            throw new InvalidEntityException("Entity invalid.");

        // TODO verify that have a primary key
        if(true)
            throw new InvalidEntityException("The entity specified does not have Primary Key defined.");
    }

    public void save(Object entity) throws OrmSaveException, InvalidEntityException {
        checkValidEntity(entity);

        String tableName = getTableName(entity);
        ArrayList<Field> fields = getFieldsAndValues(entity);

        // TODO verified if is updating.
        boolean isUpdate = true;

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
            dbConnection.getStatement().executeUpdate(SQL);
            log(String.format("Query executed: \"%s\"", SQL));
            if (autoIncrement != null) {
                // TODO assign new id generated.
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
        return fields;
    }

    public void delete(Object entity) throws OrmDeleteException, InvalidEntityException {
        checkValidEntity(entity);

    }

    /**
     * Get primary key field name.
     * @param entity
     * @return String
     */
    private String getFieldPrimary(Object entity) {
        String fieldFromCache = entityCache.getEntityPrimaryKey(entity);
        if( fieldFromCache != null )
            return fieldFromCache;
        for (java.lang.reflect.Field field : entity.getClass().getFields()) {
            TableField tableField = (TableField) field.getAnnotation(TableField.class);
            if (tableField != null && tableField.Index() == TableField.Index.PRIMARY ) {
                entityCache.setEntityPrimaryKey(entity, field.getName());
                return field.getName();
            }
        }
        return null;
    }

    public static Class getFirstBy(Object entity, String field, String value) {

        return null;
    }
}

package com.coffeeorm.orm;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;
import com.coffeeorm.exceptions.OrmSaveException;
import com.coffeeorm.exceptions.OrmDeleteException;
import com.coffeeorm.exceptions.OrmEntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
    private boolean isValidEntity(Object entity) {
        Class entityClass = entity.getClass();
        // TODO verify if is a public class
        return entityClass.getAnnotation(Entity.class) != null;
    }

    public void save(Object entity) throws OrmSaveException, OrmEntityException {
        if (!isValidEntity(entity)) throw new OrmEntityException("Invalid entity");

        // TODO check for insert or update
        insert(entity);

        log("Entity saved successfully");
    }

    private boolean insert(Object entity) throws OrmSaveException, OrmEntityException {
        String tableName = getTableName(entity);
        ArrayList<Field> fields = getFieldsAndValues(entity);

        String SQL = "INSERT INTO `" + tableName + "`(%s) VALUES(%s)",
               columns = "",
               values = "";

        // If Primary Autoincrement field is found,
        // then mark to modify after inserted into the entity instance.
        String autoIncrement = null;

        for(Field field : fields) {
            if(!field.tableField.AutoIncrement()) {
                columns += "`" + field.name + "`,";
                values += (field.value == null ?
                            "NULL," : "'" + Db.escape(field.value) + "',");
            }
            if( field.tableField.AutoIncrement() &&
                field.tableField.Index() == TableField.Index.PRIMARY ) {
                autoIncrement = field.name;
            }
        }
        // Format SQL with columns and values, removing last comma (,)
        SQL = String.format(SQL, columns.substring(0, columns.length() - 1), values.substring(0, values.length() - 1));

        try {
            dbConnection.getStatement().executeUpdate(SQL);
            log(String.format("Query executed: \"%s\"", SQL));
            if(autoIncrement != null) {
                // TODO assign new id generated.
            }
        } catch (SQLException e) {
            log("Error :: " + e.getMessage(),true);
            return false;
        }
        return true;
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

    public void delete(Object entity) throws OrmDeleteException, OrmEntityException {
        if (!isValidEntity(entity)) throw new OrmEntityException("Invalid entity");

    }

    public static Class getFirstBy(Object entity, String field, String value) {

        return null;
    }
}

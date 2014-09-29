package com.coffeeorm.reflectcache;

import com.coffeeorm.orm.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wrivas on 9/25/14.
 */
public class EntityCache {

    private HashMap<String, String> PrimaryKey;

    private HashMap<String, String> className;

    private HashMap<String, ArrayList<Field>> fields;

    private static EntityCache instance = null;

    /**
     * Singleton Instance.
     *
     * @return CoffeeORM
     */
    public static EntityCache getInstance() {
        if (instance == null)
            instance = new EntityCache();
        return instance;
    }

    public EntityCache() {
        PrimaryKey = new HashMap<>();
        className = new HashMap<>();
        fields = new HashMap<>();
    }

    /**
     * Get field primary key for the specified Entity.
     * @param entity
     * @return String or null if not found.
     */
    public String getEntityPrimaryKey(Object entity) {
        return PrimaryKey.get(entity.getClass().toString());
    }

    /**
     * Set field primary key for the specified Entity.
     * @param entity
     * @param fieldName
     */
    public void setEntityPrimaryKey(Object entity, String fieldName) {
        PrimaryKey.put(entity.getClass().toString(), fieldName);
    }

    /**
     * Get entity table name.
     *
     * @param entity
     * @return
     */
    public String getTableName(Object entity) {
        return className.get(entity.getClass().toString());
    }

    /**
     * Set the table name for the specified entity.
     *
     * @param entity
     * @param value
     */
    public void setTableName(Object entity, String value) {
        className.put(entity.getClass().toString(), value);
    }

    /**
     * Get the fields entity.
     * @param entity
     * @return
     */
    public ArrayList<Field> getFields(Object entity) {
        return fields.get(entity.getClass().toString());
    }

    /**
     * The the entity fields.
     *
     * @param entity
     * @param entityFields
     */
    public void setFields(Object entity, ArrayList<Field> entityFields) {
        fields.put(entity.getClass().toString(), entityFields);
    }

}

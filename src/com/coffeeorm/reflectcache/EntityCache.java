package com.coffeeorm.reflectcache;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wrivas on 9/25/14.
 */
public class EntityCache {

    private HashMap<String, String> PrimaryKey;

    private HashMap<String, String> className;

    private HashMap<String, ArrayList<com.coffeeorm.orm.Field>> ormField;

    private HashMap<String, ArrayList<java.lang.reflect.Field>> reflectFields;

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
        ormField = new HashMap<>();
        reflectFields = new HashMap<>();
    }

    /**
     * Get field primary key for the specified Entity.
     * @param className
     * @return String or null if not found.
     */
    public String getEntityPrimaryKey(String className) {
        return PrimaryKey.get(className);
    }

    /**
     * Set field primary key for the specified Entity.
     * @param className
     * @param fieldName
     */
    public void setEntityPrimaryKey(String className, String fieldName) {
        PrimaryKey.put(className, fieldName);
    }

    /**
     * Get entity table name.
     *
     * @param className
     * @return
     */
    public String getTableName(String className) {
        return this.className.get(className);
    }

    /**
     * Set the table name for the specified entity.
     *
     * @param className
     * @param value
     */
    public void setTableName(String className, String value) {
        this.className.put(className, value);
    }

    /**
     * Get the ormField entity.
     * @param className
     * @return
     */
    public ArrayList<com.coffeeorm.orm.Field> getOrmFields(String className) {
        return ormField.get(className);
    }

    /**
     * The the entity ormField.
     *
     * @param className
     * @param entityFields
     */
    public void setOrmFields(String className, ArrayList<com.coffeeorm.orm.Field> entityFields) {
        ormField.put(className, entityFields);
    }

    /**
     *
     * @param className
     * @return
     */
    public ArrayList<java.lang.reflect.Field> getReflectFields(String className) {
        return reflectFields.get(className);
    }

    /**
     *
     * @param className
     * @param fields
     */
    public void setReflectFields(String className, ArrayList<java.lang.reflect.Field> fields) {
        reflectFields.put(className, fields);
    }

}

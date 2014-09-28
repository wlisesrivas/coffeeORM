package com.coffeeorm.reflectcache;

import com.coffeeorm.orm.Field;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wrivas on 9/25/14.
 */
public class EntityCache {

    private HashMap<String, String> entityPrimaryKey;

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
        entityPrimaryKey = new HashMap<>();
    }

    /**
     * Get field primary key for the specified Entity.
     * @param entity
     * @return String or null if not found.
     */
    public String getEntityPrimaryKey(Object entity) {
        return entityPrimaryKey.get(entity.getClass().toString());
    }

    /**
     * Set field primary key for the specified Entity.
     * @param entity
     * @param fieldName
     */
    public void setEntityPrimaryKey(Object entity, String fieldName) {
        entityPrimaryKey.put(entity.getClass().toString(), fieldName);
    }

}

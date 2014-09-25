package com.coffeeorm.orm;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.exceptions.OrmSaveException;
import com.coffeeorm.exceptions.OrmDeleteException;
import com.coffeeorm.exceptions.OrmEntityException;
import java.lang.annotation.Annotation;

import static com.coffeeorm.util.Debug.log;

/**
 * Created by wrivas on 9/24/14.
 */
public class CoffeeORM {

    private static CoffeeORM instance = null;

    /**
     * Check if is a valid entity.
     *
     * @param obj
     * @return
     */
    private boolean isEntity(Object obj) {
        Class objClass = obj.getClass();
        for(Annotation annotation : objClass.getAnnotations()) {
            if( annotation.annotationType().equals(Entity.class) ) return true;
        }
        return false;
    }

    /**
     * Singleton Instance.
     *
     * @return CoffeeORM
     */
    public static CoffeeORM getInstance() {
        if( instance == null )
            instance = new CoffeeORM();
        return instance;
    }

    public void save(Object entity) throws OrmSaveException, OrmEntityException {
        if( !isEntity(entity) ) throw new OrmEntityException("Invalid entity");

        log("Entity saved successfully");
    }

    public void delete(Object entity) throws OrmDeleteException, OrmEntityException {
        if( !isEntity(entity) ) throw new OrmEntityException("Invalid entity");

    }

    public static Class getFirstBy(Object entity, String field, String value) {

        return null;
    }
}

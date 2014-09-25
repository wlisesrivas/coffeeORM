package com.coffeeorm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.coffeeorm.orm.CoffeeORM;

/**
 * Annotation to tells the ORM who fields must be mapped to table.
 * <p/>
 * Created by wrivas on 9/24/14.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {

    public boolean AutoIncrement() default false;

    public FieldType Type() default FieldType.VARCHAR;

    public Index Index() default Index.NOTHING;

    public enum Index {
        PRIMARY,
        UNIQUE,
        NOTHING
    }

    public enum FieldType {
        INTEGER,
        VARCHAR,
        DATE,
        DATETIME,
    }

    /**
     * Foreign key
     *
     * @return
     */
    public Class<? extends CoffeeORM> Foreign() default ForeignNull.class;
}

/**
 * Foreign null class.
 */
class ForeignNull extends CoffeeORM {
}

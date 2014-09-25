package com.coffeeorm.orm;

import com.coffeeorm.annotations.TableField;

/**
 * Created by wrivas on 9/24/14.
 */
public class Field {
    public TableField tableField;
    public String name;
    public String value;

    public Field(String name, String value, TableField tableField) {
        this.name = name;
        this.value = value;
        this.tableField = tableField;
    }
}

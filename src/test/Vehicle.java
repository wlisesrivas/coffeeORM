package test;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;

/**
 * Created by wrivas on 9/24/14.
 */
@Entity(TableName = "vehicles")
public class Vehicle {

    @TableField(AutoIncrement = true, Index = TableField.Index.PRIMARY)
    public int id;

    @TableField
    public String brand;

    @TableField(Type = TableField.FieldType.INTEGER)
    public int year;

    public String testing;
}
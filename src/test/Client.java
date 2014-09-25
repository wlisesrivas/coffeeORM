package test;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;

import java.util.Date;

/**
 * Created by wrivas on 9/24/14.
 */
@Entity(TableName = "clients")
public class Client {

    @TableField(AutoIncrement = true, Index = TableField.Index.PRIMARY)
    public int id;

    @TableField
    public int is_active;

    @TableField
    public String name;

    @TableField
    public String email;

    @TableField
    public String telephone;

    @TableField
    public String comment;

//    @TableField
    public Date updated_at;
    public Date created_at;
}

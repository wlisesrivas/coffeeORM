package test;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;

import java.sql.Timestamp;
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

    @TableField
    public String updated_at;

    @TableField
    public Timestamp created_at;

    @TableField
    public int is_deleted;

    @TableField
    public int payment_type;

    @TableField
    public int balance;

    @TableField
    public String last_bill_date;

    @TableField
    public String next_bill_date;

    @TableField
    public String identification;

    @TableField
    public String client_type;

    @TableField
    public String discount;

    @TableField
    public String discount_type;

}

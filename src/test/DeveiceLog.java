package test;

import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;

import java.sql.Time;
import java.util.Date;
import java.sql.Timestamp;

/**
 * Created by WLISES.RIVAS on 11/5/2015.
 */
@Entity(TableName = "devices_log")
public class DeveiceLog {

    @TableField(AutoIncrement = true, Index = TableField.Index.PRIMARY)
    public int id;

    @TableField
    public int device_id;

    @TableField
    public float latitude;

    @TableField
    public float longitude;

    @TableField
    public String degrees;

    @TableField
    public double speed;

    @TableField
    public float mileage;

    @TableField
    public Timestamp device_date;

    @TableField
    public int INP_0;

    @TableField
    public int INP_1;

    @TableField
    public int INP_2;

    @TableField
    public int INP_3;

    @TableField
    public int INP_4;

    @TableField
    public int INP_5;

    @TableField
    public int INP_6;

    @TableField
    public int INP_7;

    @TableField
    public int INP_8;

    @TableField
    public int INP_9;

    @TableField
    public String data;

    @TableField
    public Date created_at;

}

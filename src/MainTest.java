
import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;
import com.coffeeorm.exceptions.OrmEntityException;
import com.coffeeorm.exceptions.OrmSaveException;
import com.coffeeorm.orm.CoffeeORM;
import com.coffeeorm.sql.DBConnection;

import static com.coffeeorm.util.Debug.log;
import static com.coffeeorm.util.Db.escape;

/**
 * Created by wrivas on 9/24/14.
 */
public class MainTest {

    public static void main(String[] args) {

        log("Starting application.");

        DBConnection.getInstance();

    }

}

@Entity
class Vehicle {

    @TableField(AutoIncrement = true, Index = TableField.Index.PRIMARY)
    public int id;

    @TableField
    public String brand;

    @TableField(Type = TableField.FieldType.INTEGER)
    public int year;
}

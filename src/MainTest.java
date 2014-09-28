
import com.coffeeorm.annotations.Entity;
import com.coffeeorm.annotations.TableField;
import com.coffeeorm.exceptions.InvalidEntityException;
import com.coffeeorm.exceptions.OrmEntityException;
import com.coffeeorm.exceptions.OrmSaveException;
import com.coffeeorm.orm.CoffeeORM;
import com.coffeeorm.sql.DBConnection;
import test.Client;
import test.Vehicle;

import static com.coffeeorm.util.Debug.log;
import static com.coffeeorm.util.Db.escape;

/**
 * Created by wrivas on 9/24/14.
 */
public class MainTest {

    public static void main(String[] args) {

        log("Starting application.");

        CoffeeORM coffeeORM = CoffeeORM.getInstance();

        Client client = new Client();

        client.name = "CoffeeORM";
        client.email = "una@vaina.bien";
        client.is_active = 1;
        client.telephone = "+1 809-239-0076";
        client.comment = "Generated from CoffeeORM";

        try {
             coffeeORM.save(client);
        } catch (OrmSaveException e) {
            e.printStackTrace();
        } catch (InvalidEntityException e) {
            e.printStackTrace();
        }

    }
}


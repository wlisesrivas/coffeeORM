import com.coffeeorm.exceptions.InvalidEntityException;
import com.coffeeorm.exceptions.OrmDeleteException;
import com.coffeeorm.exceptions.OrmSaveException;
import com.coffeeorm.orm.CoffeeORM;
import com.coffeeorm.util.UtilDB;
import test.Client;

import static com.coffeeorm.util.Debug.log;

/**
 * Created by wrivas on 9/24/14.
 */
public class MainTest {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        log("Starting application.");

        CoffeeORM db = CoffeeORM.getInstance();

        /*List rows = db.activeRecord()
                .from("clients")
                .select("name,email,telephone")
                .where("id", 7)
                .get();

        log(rows.toString());*/

        java.util.Calendar.getInstance().getTime().getTime();

//        Client client = new Client();
//        client.name = "CoffeeORM";
//        client.email = "una@vaina.bien";
//        client.is_active = 1;
//        client.payment_type = 1;
//        client.balance = 0;
//        client.telephone = "+1 809-239-0076";
//        client.created_at = UtilDB.currentTimestamp();
//        client.comment = "Generated from CoffeeORM";

//        db.startTransaction();

        Client client = (Client) db.getFirst(Client.class, 19);

        try {
//            db.save(client);
            db.delete(client);
//            db.commit();
        } catch (InvalidEntityException e) {
            log("Error :: " + e.getMessage());
//            db.rollback();
        } catch (OrmDeleteException e) {
            log("Error :: " + e.getMessage());
//            db.rollback();
        }

//        for(int i = 1; i < 10; i++) {
//            Client client = (Client) db.getFirst(Client.class, i);
//            if(null != client)
//                log("ID: " + i + ", " + client.name + ", " + client.identification + ", " + client.email + ", " + client.created_at.getTime());
//        }

        System.out.printf("Total time: %sms\n", (System.currentTimeMillis() - start));

//        try {
//             db.save(client);
//        } catch (OrmSaveException e) {
//            e.printStackTrace();
//        } catch (InvalidEntityException e) {
//            e.printStackTrace();
//        }

    }
}


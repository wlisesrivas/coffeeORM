import com.coffeeorm.orm.CoffeeORM;
import test.Client;
import test.DeveiceLog;

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

//        Client client = new Client();
//        client.name = "CoffeeORM";
//        client.email = "una@vaina.bien";
//        client.is_active = 1;
//        client.telephone = "+1 809-239-0076";
//        client.comment = "Generated from CoffeeORM";

        for(int i = 1; i < 10; i++) {
            Client client = (Client) db.getFirst(Client.class, i);
            if(null != client)
                log("ID: " + i + ", " + client.name + ", " + client.identification + ", " + client.email + ", " + client.created_at.getTime());
        }
//        for(int i = 281910 - 1000; i < 281910; i++) {
//            DeveiceLog deveiceLog = (DeveiceLog) db.getFirst(DeveiceLog.class, i);
//            System.out.format("ID: %s, latitude: %s, longitude: %s\n",i, deveiceLog.latitude, deveiceLog.longitude);
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


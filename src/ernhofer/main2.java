package ernhofer;

import ernhofer.Station.Station;
import ernhofer.connection.jdbc.MySQLConnection;
import ernhofer.transactionManager.TransactionManager;

/**
 * Created by andie on 03.02.2016.
 */
public class main2 {

    public static void main(String args[]) {

        MySQLConnection mc = new MySQLConnection();

        Station station = new Station(mc);
        station.connect();
        station.listen();


        TransactionManager tm = new TransactionManager();
        tm.begin();
        System.out.println("Geben Sie etwas ein!");
    }
}

package ernhofer;

import ernhofer.Station.Station;
import ernhofer.connection.jdbc.MySQLConnection;
import ernhofer.transactionManager.TransactionManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by andie on 03.02.2016.
 */
public class example {

    private static final Logger logger = LogManager.getLogger(example.class);

    public static void main(String args[]) {

        try {
            Properties props = new Properties();
            props.load(new FileInputStream("logs/log4j.properties"));
            PropertyConfigurator.configure(props);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("START");

        TransactionManager tm = new TransactionManager();
        tm.begin();

        ArrayList<Station> stationen = new ArrayList<Station>();

        stationen.add(new Station(new MySQLConnection()));
        stationen.add(new Station(new MySQLConnection("192.168.48.237")));

        for(Station station:stationen) {
            try {
                station.connect();
                station.listen();
            }catch (SQLException e){
                logger.error("Konnte keine Verbindung zu "+station.getAddress()+" herstellen.");
            }
        }

        Runnable ra = new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter(";");
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    //System.out.println(token);
                    // check if line contains "exit"
                    //TODO: Contains auf equals aendern!!!!! -> schwer weil token moeglicherweisse \n besitzt
                    if (token.toLowerCase().contains("exit")) {
                        logger.info("Programm wird beendet");
                        for(Station station:stationen) {
                            station.close();
                        }
                        tm.end();
                        break;
                    }else {
                        try {
                            tm.send("start");
                            tm.send(token);
                            tm.send("end");
                            tm.send("prepare");
                        }catch (Exception e){

                        }
                    }
                }
                if (scanner != null) {
                    scanner.close();
                }

            }
        };
        Thread t = new Thread(ra);
        t.setName("Console Input");
        t.start();

        System.out.println("\nGeben Sie etwas ein!");
    }
}

//TODO: TM anzahl consumer beachten!
//TODO: Logging für jede Station und TM einzeln
//TODO: TM Timeout beachten

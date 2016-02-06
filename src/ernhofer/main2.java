package ernhofer;

import ernhofer.Station.Station;
import ernhofer.connection.jdbc.MySQLConnection;
import ernhofer.transactionManager.TransactionManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by andie on 03.02.2016.
 */
public class main2 {

    private static final Logger logger = LogManager.getLogger(main2.class);

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

        Station[] stationen = new Station[2];

        stationen[0] = new Station(new MySQLConnection());
        stationen[1] = new Station(new MySQLConnection("192.168.48.237"));

        for(Station station:stationen) {
            station.connect();
            station.listen();
        }


        Runnable ra = new Runnable() {
            @Override
            public void run() {
                logger.warn("Eingabe");
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter(";");
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    //System.out.println(token);
                    // check if line contains "exit"
                    //TODO: Contains auf equals aendern!!!!! -> schwer weil token moeglicherweisse \n besitzt
                    if (token.toLowerCase().contains("exit")) {
                        logger.info("Programm wird druch den Befehl '"+token+"' beendet");
                        tm.end();
                        for(Station station:stationen) {
                            station.close();
                        }
                        break;
                    }else {
                        tm.send(token);
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

        System.out.println("Geben Sie etwas ein!");
    }
}

//TODO: TM anzahl consumer beachten!
//TODO: Logging für jede Station und TM einzeln
//TODO: TM Timeout beachten

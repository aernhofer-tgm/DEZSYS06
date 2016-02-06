package ernhofer.Stationen;

import ernhofer.Station.Station;
import ernhofer.connection.jdbc.MySQLConnection;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by andie on 06.02.2016.
 */
public class Station1 {

    public static void main (String[] args) {

        try {
            Properties props = new Properties();
            props.load(new FileInputStream("logs/log4j.properties"));
            PropertyConfigurator.configure(props);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Station station = new Station(new MySQLConnection());
        try {
            station.connect();
            station.listen();
        } catch (SQLException e) {
            System.err.println("Konnte keine Verbindung zu " + station.getAddress() + " herstellen.");
        }

        Runnable ra = new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter(";");
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    if (token.toLowerCase().contains("exit")) {
                        station.close();
                        break;
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

    }
}

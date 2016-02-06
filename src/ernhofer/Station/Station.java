package ernhofer.Station;

import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import ernhofer.connection.jms.Producer;
import ernhofer.connection.jms.Subscriber;
import org.apache.log4j.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by andie on 03.02.2016.
 */
public class Station{

    private Subscriber subscriber;
    private Producer producer;
    Connection connection;
    private static final Logger logger = LogManager.getLogger(Station.class.getName());

    public Station(Connection connection) {

        try {
            PatternLayout layout = new PatternLayout( "<%d{yyyy-MM-dd HH:mm:ss}> %-5p: [%t]: %m%n" );
            //ConsoleAppender consoleAppender = new ConsoleAppender( layout );
            //logger.addAppender( consoleAppender );
            //Layout, File, keep old data
            FileAppender fileAppender = new FileAppender( layout, "logs/Stationen.log", false );
            logger.addAppender( fileAppender );
            // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
            logger.setLevel( Level.INFO );
        } catch( Exception ex ) {
            System.out.println(ex);
        }

        this.producer = new Producer();
        this.producer.setTopic("antwort");
        this.connection = connection;
        subscriber = new Subscriber() {
            @Override
            public void executeCallback(Message message) {
                try {
                    Thread.currentThread().setName("Station "+connection.getAdress());
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        //System.out.println("Received message: '"
                        //        + textMessage.getText() + "'");
                        String query = textMessage.getText();
                        //execute(query);
                        if(textMessage.getText().equals("commit")){
                            doCommit();
                        }else if(textMessage.getText().equals("abort")) {
                            doAbort();
                        }else if(transaction(query)){
                            //Erfolgreich
                            producer.send("ACK");
                            System.out.println("Transaktion erfolgreich Station: "+Thread.currentThread().getName());
                        }else{
                            //Fehlgeschlagen
                            producer.send("NCK");
                            System.out.println("Transaktion fehlgeschlagen"+Thread.currentThread().getName());
                        }
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                } catch (CommunicationsException | SocketTimeoutException e) {
                    producer.send("TIMEOUT");
                    logger.error("Timeout beim ausführen des Befehles!",e);
                } catch (MySQLNonTransientConnectionException e) {
                    logger.error("Es besteht keine Verbindung!",e);
                }
            }
        };
    }

    public void connect() throws SQLException{
        logger.info("Starten der Station "+connection.getAdress());
        subscriber.connect();
        connection.connect();
        producer.connect();
        producer.send("anmelden");
    }

    public void listen(){
        subscriber.listen();
    }

    public void execute(String query) throws SocketTimeoutException{
        try {
            connection.print(connection.execute(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean transaction(String query) throws CommunicationsException, SocketTimeoutException, MySQLNonTransientConnectionException {
        try {
            connection.execute("XA START \"dezsys06\"");
            ResultSet rm = connection.execute(query);
            connection.print(rm);
            connection.execute("XA END \"dezsys06\"");
            connection.execute("XA PREPARE \"dezsys06\"");
            return true;
        } catch (SQLException e) {
            switch (e.getErrorCode()) {
                case 1054:
                    logger.error("Umlaute sind nicht erlaubt!");
                    break;
                case 1064:
                    logger.error("Die SQL Syntax ist nicht korrekt!");
                    break;
                case 1146:
                    logger.error("Die angegebene Tabelle ist nicht vorhanden!");
                    break;
                default:
                    logger.error("Errorcode: " + e.getErrorCode() + "von Station: " + Thread.currentThread().getName()+"\n",e);
                    throw new SocketTimeoutException();
            }
            return false;
        }
    }

    public void doCommit() throws SocketTimeoutException {
        try {
            connection.execute("XA COMMIT \"dezsys06\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doAbort() throws CommunicationsException, SocketTimeoutException , MySQLNonTransientConnectionException{
        try {
            connection.execute("XA END \"dezsys06\"");
            connection.execute("XA ROLLBACK \"dezsys06\"");
        } catch (SQLException e) {
            switch (e.getErrorCode()){
                case 1399:
                    logger.error("Error bei den Verteilten Transaktionen",e);
                    break;
                case 0:
                    throw new MySQLNonTransientConnectionException();
                default:
                    System.out.println("Errorcode bei Abort: "+e.getErrorCode());
                    e.printStackTrace();
            }
        }
    }

    public String getAddress(){
        return connection.getAdress();
    }

    public void send(String message){
        logger.info("Sendet '"+message+"' an den TM");
        producer.send(message);
    }

    public void close(){
        logger.info("Beenden der Station "+getAddress());
        producer.send("abmelden");
        subscriber.close();
        producer.close();
        connection.close();
    }
}

package ernhofer.Station;

import ernhofer.connection.jms.Subscriber;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.sql.SQLException;

/**
 * Created by andie on 03.02.2016.
 */
public class Station{

    private Subscriber subscriber;
    Connection connection;
    private static final Logger logger = LogManager.getLogger(Station.class.getName());

    public Station(Connection connection) {
        this.connection = connection;
        subscriber = new Subscriber() {
            @Override
            public String executeCallback(Message message) {
                try {
                    Thread.currentThread().setName("Station "+connection.getAdress());
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        //System.out.println("Received message: '"
                        //        + textMessage.getText() + "'");
                        String query = textMessage.getText();
                        //execute(query);
                        if(transaction(query)){
                            //Erfolgreich
                            //TODO
                            doCommit();
                        }else{
                            //Fehlgeschlagen
                            //TODO
                            doAbort();
                        }
                        return textMessage.getText();
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    public void connect(){
        subscriber.connect();
        connection.connect();
    }

    public void listen(){
        subscriber.listen();
    }

    public void execute(String query){
        try {
            connection.print(connection.execute(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean transaction(String query){
        try {
            connection.execute("XA START \"dezsys06\"");
            connection.print(connection.execute(query));
            connection.execute("XA END \"dezsys06\"");
            connection.execute("XA PREPARE \"dezsys06\"");
            return true;
        } catch (SQLException e) {
            switch (e.getErrorCode()){
                case 1064:
                    logger.error("Die SQL Syntax ist nicht korrekt!");
                    break;
                case 1146:
                    logger.error("Die angegebene Tabelle ist nicht vorhanden!");
                    break;
                default:
                    System.out.println("Errorcode: "+e.getErrorCode());
                    e.printStackTrace();
                    break;
            }
            return false;
        }
    }

    public void doCommit(){
        try {
            connection.execute("XA COMMIT \"dezsys06\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doAbort(){
        try {
            connection.execute("XA ROLLBACK \"dezsys06\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void close(){
        subscriber.close();
        connection.close();
    }
}

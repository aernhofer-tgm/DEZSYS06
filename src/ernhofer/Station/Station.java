package ernhofer.Station;

import ernhofer.connection.jms.Producer;
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
    private Producer producer;
    Connection connection;
    private static final Logger logger = LogManager.getLogger(Station.class.getName());

    public Station(Connection connection) {
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
                            System.out.println("COMMIT!!!");
                        }else if(textMessage.getText().equals("abort")) {
                            doAbort();
                            System.out.println("Abort!!!");
                        }else if(transaction(query)){
                            //Erfolgreich
                            //TODO
                            producer.send("ACK");
                            System.out.println("Transaktion erfolgreich");
                            //doCommit();
                        }else{
                            //Fehlgeschlagen
                            //TODO
                            producer.send("NCK");
                            System.out.println("Transaktion fehlgeschlagen");
                            //doAbort();
                        }
                        //return textMessage.getText();
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                }
                //return null;
            }
        };
    }

    public void connect(){
        subscriber.connect();
        connection.connect();
        producer.connect();

        producer.send("anmelden");
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
            connection.execute("XA END \"dezsys06\"");
            connection.execute("XA ROLLBACK \"dezsys06\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void send(String message){
        producer.send(message);
    }

    public void close(){
        subscriber.close();
        producer.close();
        connection.close();
    }
}

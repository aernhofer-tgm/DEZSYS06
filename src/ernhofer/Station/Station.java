package ernhofer.Station;

import ernhofer.connection.jms.Subscriber;

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

    public Station(Connection connection) {
        this.connection = connection;
        subscriber = new Subscriber() {
            @Override
            public String executeCallback(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("Received message: '"
                                + textMessage.getText() + "'");
                        String query = textMessage.getText();
                        execute(query);
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
}

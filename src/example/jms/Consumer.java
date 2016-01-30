package example.jms;
import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.IOException;

public class Consumer {
    // URL of the JMS server
    //private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String url = "tcp://192.168.48.234:61616";

    // Name of the topic from which we will receive messages from = " testt"

    public static void main(String[] args) throws JMSException {
        System.out.println(url);
        // Getting JMS connection from the server

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic("dezsys");

        MessageConsumer consumer = session.createConsumer(topic);

        MessageListener listner = new MessageListener() {
            public void onMessage(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("Received message: '"
                                + textMessage.getText() + "'");
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                }
            }
        };

        consumer.setMessageListener(listner);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.close();

    }
}
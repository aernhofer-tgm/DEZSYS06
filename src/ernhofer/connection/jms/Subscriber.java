package ernhofer.connection.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public abstract class Subscriber implements ConSettings{

    private Connection connection;
    private MessageConsumer consumer;
    private Topic t;

    public Subscriber(){
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        try {
            this.connection = connectionFactory.createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        try {
            this.connection.start();
            this.createSession();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void createSession(){
        try {
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            this.t = session.createTopic(topic);
            this.consumer = session.createConsumer(t);



        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void listen(){
        MessageListener listener = message -> executeCallback(message);
        try {
            this.consumer.setMessageListener(listener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            this.connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract String executeCallback(Message message);

}
package ernhofer.connection.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public abstract class Subscriber implements ConSettings{

    private Connection connection;
    private MessageConsumer consumer;
    //private MessageProducer producer;
    private Session session;
    private String topic;
    //private Destination destination;
    private Topic t;

    public Subscriber(){
        this.topic = ConSettings.topic;
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
            session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            this.t = session.createTopic(this.topic);
            this.consumer = session.createConsumer(t);


            //destination = session.createQueue(tmip);
            //producer = session.createProducer(destination);
            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

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

    /*
    public void send(String text){
        // Create a messages
        String t = "Queue Message: "+text;
        TextMessage message = null;
        try {
            message = session.createTextMessage(text);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        // Tell the producer to send the message
        try {
            System.out.println("Sent message: "+ message.getText() + "\nAbsernder: " + Thread.currentThread().getName());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    */

    public void close(){
        try {
            this.session.close();
            this.connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    public abstract void executeCallback(Message message);

}
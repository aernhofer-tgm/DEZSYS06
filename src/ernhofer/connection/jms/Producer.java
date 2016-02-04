package ernhofer.connection.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Producer implements ConSettings {

    private Connection connection;
    private MessageProducer producer;
    private TextMessage message;
    private Topic t;
    private String topic;

    public Producer(){
        this.topic = ConSettings.topic;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        try {
            this.connection = connectionFactory.createConnection();
        } catch (JMSException e) {
            System.out.println("Producer konnte keine Verbindung erzeugen!!");
            e.printStackTrace();
        }
    }

    public void connect(){
        try {
            this.connection.start();
            this.createSession();
        } catch (JMSException e) {
            System.out.println("Producer konnte nicht verbinden!!");
            e.printStackTrace();
        }
    }

    public void createSession(){
        Session session = null;
        try {
            session = this.connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            this.t = session.createTopic(this.topic);
            this.producer = session.createProducer(t);
            this.message = session.createTextMessage();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void send(String message){

        try {
            this.message.setText(message);
            // Here we are sending the message!
            producer.send(this.message);
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

    public void setTopic(String topic){
        this.topic = topic;
    }
}
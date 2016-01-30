package ernhofer;

import ernhofer.connection.jms.Producer;
import ernhofer.connection.jms.Subscriber;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Created by andie on 30.01.2016.
 */
public class main {
    public static void main(String args[]){

        Subscriber s = new Subscriber() {
            @Override
            public String excecuteCallback(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("Received message: '"
                                + textMessage.getText() + "'");
                        return textMessage.getText();
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                }
                return null;
            }
        };


        s.connect();
        s.listen();


        Producer p = new Producer();
        p.connect();
        p.send("hallo");
        System.out.println("Sent message: 'hallo'");


        p.close();
        s.close();
    }
}

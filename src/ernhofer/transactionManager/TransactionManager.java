package ernhofer.transactionManager;

import ernhofer.connection.jms.Producer;
import ernhofer.connection.jms.Subscriber;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Scanner;

/**
 * Created by andie on 03.02.2016.
 */
public class TransactionManager extends Thread{

    private static final Logger logger = LogManager.getLogger(TransactionManager.class.getName());
    private boolean running;
    private int anzahlConsumer;
    private Producer producer;
    private Subscriber subscriber;

    public TransactionManager(){
        producer = new Producer();
        subscriber = new Subscriber() {
            @Override
            public void executeCallback(Message message) {
                read(message);
            }
        };
        subscriber.setTopic("antwort");
        anzahlConsumer = 0;
        running=true;
    }

    @Override
    public void run(){
        while(running){
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println(running);
        }
    }

    public void begin(){
        producer.connect();
        subscriber.connect();
        subscriber.listen();
        this.start();
        //this.read();
    }

    public void end(){
        running = false;
        producer.close();
        subscriber.close();
    }

    public void read(Message message){
        try {
            Thread.currentThread().setName("TM Subscriber");
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                switch (textMessage.getText()){
                    case "anmelden":
                        break;
                    case "ACK":
                        producer.send("commit");
                        break;
                    case "NCK":
                        producer.send("abort");
                        break;
                    default:
                        System.out.println("TM Received message: '"
                                + textMessage.getText() + "'");
                        break;
                }
            }
        } catch (JMSException e) {
            System.out.println("Caught:" + e);
            e.printStackTrace();
        }
    }

    public void send(String message){
        producer.send(message);
    }

    /*

    public void anmelden(){
        this.anzahlConsumer++;
    }

    public void abmelden(){
        this.anzahlConsumer--;
    }

    //Fuer ACK/NOK
    public void answer(){

    }
    */
}

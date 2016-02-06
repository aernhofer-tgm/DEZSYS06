package ernhofer.transactionManager;

import ernhofer.Station.Station;
import ernhofer.connection.jms.Producer;
import ernhofer.connection.jms.Subscriber;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by andie on 03.02.2016.
 */
public class TransactionManager extends Thread{

    private static final Logger logger = LogManager.getLogger(TransactionManager.class.getName());
    private boolean running;
    private int anzahlConsumer;
    private int ack;
    private int nck;
    private int timeout;
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
        ack=0;
        nck=0;
        timeout=0;
        running=true;
    }

    @Override
    public void run(){
        while(running){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(ack+nck+timeout==anzahlConsumer&&anzahlConsumer>0){
                System.out.println("ACK: "+ack+", NCK: "+nck+", Timeout: "+timeout);
                if(ack==anzahlConsumer){
                    producer.send("commit");
                }else{
                    producer.send("abort");
                }
                ack=0;
                nck=0;
                timeout=0;
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
                        anmelden();
                        break;
                    case "abmelden":
                        abmelden();
                        break;
                    case "ACK":
                        ack++;
                        break;
                    case "NCK":
                        nck++;
                        break;
                    case "TIMEOUT":
                        timeout++;
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

    public void anmelden(){
        this.anzahlConsumer++;
        logger.info("neue Verbindung");
    }

    public void abmelden(){
        this.anzahlConsumer--;
    }

    //Fuer ACK/NOK
    public void answer(){

    }

    public static void main (String[] args){
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("logs/log4j.properties"));
            PropertyConfigurator.configure(props);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("START");

        TransactionManager tm = new TransactionManager();
        tm.begin();

        Runnable ra = new Runnable() {
            @Override
            public void run() {
                logger.warn("Eingabe");
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter(";");
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    //System.out.println(token);
                    // check if line contains "exit"
                    //TODO: Contains auf equals aendern!!!!! -> schwer weil token moeglicherweisse \n besitzt
                    if (token.toLowerCase().contains("exit")) {
                        logger.info("Programm wird druch den Befehl '"+token+"' beendet");
                        tm.end();
                        break;
                    }else {
                        tm.send(token);
                    }
                }
                if (scanner != null) {
                    scanner.close();
                }

            }
        };
        Thread t = new Thread(ra);
        t.setName("Console Input");
        t.start();

        System.out.println("Geben Sie etwas ein!");
    }

}

package ernhofer.transactionManager;

import ernhofer.connection.jms.Producer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * Created by andie on 03.02.2016.
 */
public class TransactionManager extends Thread{


    private static final Logger logger = LogManager.getLogger(TransactionManager.class.getName());
    private boolean running;
    private Producer producer;

    public TransactionManager(){
        producer = new Producer();
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
        this.start();
        //this.read();
    }

    public void end(){
        running = false;
        producer.close();
    }

    public void read(){

    }

    public void send(String message){
        producer.send(message);
    }
}

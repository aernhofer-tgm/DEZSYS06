package ernhofer.transactionManager;

import ernhofer.connection.jms.Producer;

import java.util.Scanner;

/**
 * Created by andie on 03.02.2016.
 */
public class TransactionManager extends Thread{

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
            System.out.println(running);
        }
    }

    public void begin(){
        producer.connect();
        this.start();
        this.read();
    }

    public void end(){
        running = false;
        producer.close();
    }

    public void read(){
        Runnable ra = new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter(";");
                while (scanner.hasNext()&&running) {
                    String token = scanner.next();
                    System.out.println(token);
                    producer.send(token+";");
                    // check if line contains "exit"
                    //TODO: Contains auf equals aendern!!!!!
                    if (token.toLowerCase().contains("exit")) {
                        end();
                        System.out.println("Programm wird beendet!");
                        break;
                    }
                }
                if (scanner != null) {
                    scanner.close();
                }

            }
        };
        Thread t = new Thread(ra);
        t.start();
    }
}

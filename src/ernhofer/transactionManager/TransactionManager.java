package ernhofer.transactionManager;

import java.util.Scanner;

/**
 * Created by andie on 03.02.2016.
 */
public class TransactionManager extends Thread{

    private String code;
    private boolean running;

    public TransactionManager(){
        running=true;
    }

    @Override
    public void run(){
        while(running){

        }
    }

    public void begin(){
        this.start();
        this.read();
    }

    public void end(){
        running = false;
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
                    // check if line contains "exit"
                    if (token.toLowerCase().contains("exit")) {
                        running=false;
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

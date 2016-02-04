package ernhofer;

import ernhofer.connection.jdbc.MySQLConnection;
import ernhofer.connection.jms.Subscriber;
import ernhofer.transactionManager.TransactionManager;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.sql.SQLException;

/**
 * Created by andie on 30.01.2016.
 */
public class main {
    public static void main(String args[]){

        Subscriber s = new Subscriber() {
            @Override
            public void executeCallback(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        System.out.println("Received message: '"
                                + textMessage.getText() + "'");
                        //return textMessage.getText();
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                }
                //return null;
            }
        };

        s.connect();
        s.listen();

        TransactionManager tm = new TransactionManager();
        tm.begin();
        System.out.println("Geben Sie etwas ein!");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.print("Subscriber schließen");
        s.close();


        String query = "SELECT * FROM bezirk";
        MySQLConnection mysql = new MySQLConnection();
        mysql.connect();
        try {
            mysql.print(mysql.execute(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mysql.close();
    }
}

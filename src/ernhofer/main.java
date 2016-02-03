package ernhofer;

import ernhofer.transactionManager.TransactionManager;

/**
 * Created by andie on 30.01.2016.
 */
public class main {
    public static void main(String args[]){
        /*

        Subscriber s = new Subscriber() {
            @Override
            public String executeCallback(Message message) {
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
*/

        TransactionManager tm = new TransactionManager();
        tm.begin();
        System.out.println("Geben Sie etwas ein!");
    }
}

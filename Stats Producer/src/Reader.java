package src;

import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import java.util.Scanner;

/**
 * Created by jmcalves275 on 10/10/14.
 */
public class Reader extends  Thread {
    private TopicConnection connection;
    boolean check;
    TopicSession topicSession;
    public Reader(TopicConnection topicConn,TopicSession topicSession,boolean check){

        this.connection=topicConn;
        this.topicSession=topicSession;
        this.check=check;
    }

    public void run(){
        while(true) {
            Scanner sc = new Scanner(System.in);
            if (sc.nextLine().compareTo("exit") == 0) {
                check = true;
                try {
                    connection.close();


                    System.exit(0);
                } catch (JMSException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    System.out.println("Erro: " + e.getMessage());
                }

            }
        }
    }
}

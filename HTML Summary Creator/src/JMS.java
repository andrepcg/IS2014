/**
 * Created by André on 28-09-2014.
 */
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Scanner;


public class JMS implements Runnable{

    private String topic, user, password;


    private TopicConnection topicConn;
    private TopicSession topicSession;
    private TopicPublisher topicPublisher;
    TopicSubscriber topicSubscriber;

    private boolean connected, producer;

    public void setKeepConnecting(Boolean keepConnecting) {
        this.keepConnecting = keepConnecting;
    }

    private boolean keepConnecting = true;

    public JMS(String topic, String user, String password, Boolean producer) {
        this.topic = topic;
        this.user = user;
        this.password = password;
        this.producer = producer;
        connected = connect();

    }

    private boolean connect(){
        try{
            InitialContext ctx = new InitialContext();
            Topic topic = (Topic) ctx.lookup(this.topic);

            TopicConnectionFactory connFactory = (TopicConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
            topicConn = connFactory.createTopicConnection(user, password);
            topicConn.setClientID("peddy");
            topicConn.start();

            topicSession = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

            if(this.producer)
                topicPublisher = topicSession.createPublisher(topic);
            else
                topicSubscriber=topicSession.createDurableSubscriber(topic, "sub");

            ctx = new InitialContext();
            topic = (Topic) ctx.lookup("jms/topic/project");
            connFactory = (TopicConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
            topicConn = connFactory.createTopicConnection(user, password);
            topicConn.setClientID("peddy");
            topicConn.start();
            topicSession = topicConn.createTopicSession(false,Session.CLIENT_ACKNOWLEDGE);
            if(this.producer)
                topicPublisher = topicSession.createPublisher(topic);
            else
                topicSubscriber=topicSession.createDurableSubscriber(topic, "MySub");

        }catch(JMSException e){
            System.out.println("Server is down! " + e.toString());
            return false;
        }catch(NamingException e){
            System.out.println("Server is down! 1 " + e.toString());
            return false;
        }

        return true;
    }

    public boolean send(String xmlString){
        if(this.producer) {
            try {

                Message message = null;
                message = topicSession.createMessage();
                message.setStringProperty("xml", xmlString);
                topicPublisher.publish(message);
            } catch (JMSException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
        else
            return false;

    }

    public String receive() {
        if(!this.producer && this.connected) {
            String d = null;
            try {
                TextMessage msg = (TextMessage) this.topicSubscriber.receive();
                d = msg.getText();
            } catch (JMSException e) {
                e.printStackTrace();
                return null;
            }
            catch (Exception e1){
                e1.printStackTrace();
                return null;
            }

            return d;
        }

        return null;
        
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        while (keepConnecting){
            if(!connected)
                connected = connect();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //logger.log("Thread sleep error");
                e.printStackTrace();
            }

        }
    }
}

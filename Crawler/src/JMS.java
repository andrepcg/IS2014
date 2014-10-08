/**
 * Created by André on 28-09-2014.
 */
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class JMS implements Runnable{

    private String topic, user, password;


    private TopicConnection topicConn;
    private TopicSession topicSession;
    private TopicPublisher topicPublisher;
    //private LinkedBlockingQueue<String> pool;

    private boolean connected;

    public void setKeepConnecting(Boolean keepConnecting) {
        this.keepConnecting = keepConnecting;
    }

    private Boolean keepConnecting = true;

    public JMS(String topic, String user, String password) {
        this.topic = topic;
        this.user = user;
        this.password = password;

        connected = connect();

    }

    private boolean connect(){
        try{
            // get the initial context
            InitialContext ctx = new InitialContext();
            // lookup the topic object
            Topic topic = (Topic) ctx.lookup("jms/topic/project");

            // lookup the topic connection factory
            TopicConnectionFactory connFactory = (TopicConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");

            // create a topic connection
            topicConn = connFactory.createTopicConnection(user, password);

            // create a topic session
            topicSession = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

            // create a topic publisher
            topicPublisher = topicSession.createPublisher(topic);
            topicConn.start();

        }catch(JMSException e){

            System.out.println("Server is down! ");




        }catch(NamingException e){
            System.out.println("Server is down! ");

        }

        return true;
    }

    public boolean sendMessage(String xmlString){


        try {
            
            Message message=null;
            message=topicSession.createMessage();
            message.setStringProperty("xml",xmlString);
            topicPublisher.publish(message);
        } catch (JMSException e) {
            e.printStackTrace();
            return false;
        }

        return true;

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

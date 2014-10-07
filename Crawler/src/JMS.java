/**
 * Created by André on 28-09-2014.
 */
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMS {

    private String topic, user, password;
    private ConnectionFactory cf;
    private Connection conn;
    private Session s;
    private Destination d;
    private MessageProducer mp;
    //private LinkedBlockingQueue<String> pool;

    private boolean connected;

    public JMS(String topic, String user, String password) {
        this.topic = topic;
        this.user = user;
        this.password = password;

        connected = connect();

    }

    private boolean connect(){
        try {
            InitialContext init;
            init = new InitialContext();
            this.cf = (QueueConnectionFactory) init.lookup("jms/RemoteConnectionFactory");
            this.d = (Destination) init.lookup(this.topic);
            this.conn = (Connection) this.cf.createConnection(this.user, this.password);
            this.conn.start();
            this.s = this.conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.mp = this.s.createProducer(this.d);

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
            return false;
        } finally {
            return true;
        }
    }

    public boolean send(String msg) {
        TextMessage tm;
        if(!connected)
            connected = connect();

        try {

            tm = this.s.createTextMessage(msg);
            this.mp.send(tm);

        } catch (JMSException e) {
            connected = false;
            return false;
        } finally {
            return true;
        }
    }

    public boolean isConnected() {
        return connected;
    }
}

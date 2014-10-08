
import java.util.*;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.*;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.Text;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import classes.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.Scanner;

/**
 * Created by jmcalves275 on 03/10/14.
 */
public class StatsProducer {
    NewsList listaNoticias;
     TopicSubscriber topicSubscriber;
     InitialContext ctx;
     Topic topic;
     TopicConnectionFactory connFactory;
     TopicConnection topicConn;
    TopicSession topicSession;
    public StatsProducer()  throws NamingException, JMSException {
        while(!initConection());
        System.out.println("You are connected!");
    }
    public boolean initConection(){
        try{
            ctx = new InitialContext();

            // lookup the topic object
            topic = (Topic) ctx.lookup("jms/topic/project");

            // lookup the topic connection factory
            connFactory = (TopicConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");

            // create a topic connection
            topicConn = connFactory.createTopicConnection("admin1", "admin");

            topicConn.setClientID("admin4");

            // create a topic session
            topicSession = topicConn.createTopicSession(false,Session.CLIENT_ACKNOWLEDGE);

            // create a topic subscriber
            topicSubscriber=topicSession.createDurableSubscriber(topic, "topic1");

            topicConn.start();




        }catch(JMSException e){
            System.out.println(e.getMessage());
            System.out.println("Can't establish connection to the server. We'll retry in 5 seconds");
            try{
                Thread.sleep(5000);
            }catch(InterruptedException s){
                System.out.println(s.getMessage());
            }
            return false;

        }catch(NamingException e){
            System.out.println(e.getMessage());
            System.out.println("Can't establish connection to the server. We'll retry in 5 seconds");
            try{
                Thread.sleep(5000);
            }catch(InterruptedException s){
                System.out.println(s.getMessage());
            }
            return false;

        }
        return true;
    }
    public void receive(){

            boolean checkFicheiro=false;

            try {

                Message m = topicSubscriber.receive();



                String doc = m.getStringProperty("xml");

                System.out.println("Message received from topic!\n");
                //System.out.println(doc);


                checkFicheiro=validateXML("esquema.xsd","filename.xml");
                System.out.println(checkFicheiro);
                if(!checkFicheiro){
                    System.out.println("O ficheiro XML é inválido");
                    receive();
                }
                else {
                    unmarshal(doc);
                    produceStats();

                }
            } catch (JMSException e) {
                //System.out.println(e.getMessage());


            } catch (NullPointerException e) {

            }

    }
    public void produceStats(){
        List<Article> listaArtigos=listaNoticias.getArticle();
        System.out.println(listaArtigos.get(0).getAuthor());
    }

    public void unmarshal(String doc){
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(NewsList.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(doc);
            listaNoticias = (NewsList) unmarshaller.unmarshal(reader);
        }catch(JAXBException e){
                System.out.println("Erro ao fazer unmarshal");
            }
    }

    public static boolean validateXML(String xsdPath, String xmlPath){

        try {
            SchemaFactory factory =SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }catch( SAXException e){
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
    }
    public void close(){

        try {

            topicSubscriber.close();
            topicSession.unsubscribe("topic1");
            System.exit(0);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws JMSException, NamingException {

        StatsProducer statsProducer = new StatsProducer();

         statsProducer.receive();
         statsProducer.close();


    }




}


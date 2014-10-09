package src;

import java.util.*;
import java.sql.Timestamp;
import java.util.Date;
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
import sun.jvm.hotspot.utilities.Interval;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
            //topicConn.close();
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

            System.out.println("Can't establish connection to the server. We'll retry in 5 seconds");
            try{
                Thread.sleep(5000);
            }catch(InterruptedException s){
                System.out.println(s.getMessage());
            }
            return false;

        }catch(NamingException e){

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
    public boolean retryConnection() {

        try{
            topicConn.close();
            // create a topic connection
            //connFactory = (TopicConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
            topicConn = connFactory.createTopicConnection("admin1", "admin");

            topicConn.setClientID("admin1");

            // create a topic session
            TopicSession topicSession = topicConn.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);

            // create a topic subscriber
            topicSubscriber=topicSession.createDurableSubscriber(topic, "topic1");

            topicConn.start();



            System.out.println("Connection to the topic retablished!");
            return true;
        }catch(Exception e){

            System.out.println("Topic is down! We will retry the connection in 5 seconds...");
            try{
                Thread.sleep(5000);
            }catch(InterruptedException s){
                System.out.println(s.getMessage());
            }

            return false;
        }
    }
    public void receive(){

        boolean checkFicheiro=false;
        while(true) {
            try {

                Message m = topicSubscriber.receive();


                String doc = m.getStringProperty("xml");

                System.out.println("Messages received from topic!\n");
                System.out.println(doc);


                checkFicheiro = validateXML("/Users/jmcalves275/Desktop/Faculdade/Mestrado/IS/Assignment_1/IS2014/Stats Producer/esquema.xsd", "/Users/jmcalves275/Desktop/Faculdade/Mestrado/IS/Assignment_1/IS2014/Stats Producer/filename.xml");

                if (!checkFicheiro) {
                    System.out.println("O ficheiro XML é inválido");
                    receive();
                } else {
                    unmarshal(doc);
                    produceStats();

                }
            } catch (JMSException e) {
                //System.out.println(e.getMessage());
                while (!retryConnection()) ;
                //receive();


            } catch (NullPointerException e) {
                while (!retryConnection()) ;
                //receive();

            }
        }

    }

    public void produceStats(){


        long timestamp=System.currentTimeMillis();


        for(int i=0;i<listaNoticias.getArticle().size();i++){

            long x=(listaNoticias.getArticle().get(i).getTimestamp()).longValue();

            Date startDate = new Date(x);
            Date endDate   =new Date(System.currentTimeMillis());
            long interval  = endDate.getTime() - startDate.getTime();
            interval=TimeUnit.MILLISECONDS.toMinutes(interval);
            System.out.println("Tempo actual: "+endDate+" Tempo qda noticia: "+startDate+" Tempo duracao: "+interval+" Titulo: "+listaNoticias.getArticle().get(i).getTitle());
            if (interval<(12*60))//converter 12 horas em minutos
            {
                System.out.println("oisd");
                listaNoticias.getArticle().remove(listaNoticias.getArticle().get(i));
                System.out.println("Print cenas: "+listaNoticias.getArticle().get(i).getTitle());
            }


        }
        for(int i=0;i<listaNoticias.getArticle().size();i++){
            System.out.println("Cenas: "+listaNoticias.getArticle().get(i).getTitle());
        }
        //

    }

    public void unmarshal(String doc){
        try {
            NewsList listaProvisoria;
            JAXBContext jaxbContext = JAXBContext.newInstance(NewsList.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(doc);
            listaProvisoria = (NewsList) unmarshaller.unmarshal(reader);

            mergeLists(listaProvisoria);
        }catch(JAXBException e){
            System.out.println("Erro ao fazer unmarshal");
        }
    }
    public void mergeLists(NewsList listaProvisoria){
        listaNoticias=new NewsList();
        listaNoticias.getArticle().add(listaProvisoria.getArticle().get(0));
        boolean check;
        for(int i=0;i<listaProvisoria.getArticle().size();i++ ){
           System.out.println("Titulo: "+listaProvisoria.getArticle().get(i).getTitle()+" e na lista ta: "+listaNoticias.getArticle().get(0).getTitle()+" "+listaNoticias.getArticle().get(0).getAuthor());

           check=listaNoticias.getArticle().contains(listaProvisoria.getArticle().get(i));
            System.out.println(check);
              if(check){
                    System.out.print("MERDA: "+listaProvisoria.getArticle().get(i).getTitle());
              }
            else{
                  listaNoticias.addArticle(listaProvisoria.getArticle().get(i));
              }


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


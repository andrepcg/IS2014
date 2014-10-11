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
public class StatsProducer extends Thread {
    NewsList listaNoticias=new NewsList();
    HashMap<String,Integer> hashmap = new HashMap<String, Integer>();
    TopicSubscriber topicSubscriber;
    InitialContext ctx;
    Topic topic;
    TopicConnectionFactory connFactory;
    TopicConnection topicConn=null;
    TopicSession topicSession;
    boolean check=false;
    public StatsProducer()  throws NamingException, JMSException {
        while(!initConection());

        new Reader(topicConn,topicSession,check).start();
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
            topicConn = connFactory.createTopicConnection("admin", "admin1");

            topicConn.setClientID("admin1");

            // create a topic session
            topicSession = topicConn.createTopicSession(false,Session.CLIENT_ACKNOWLEDGE);

            // create a topic subscriber
            topicSubscriber=topicSession.createDurableSubscriber(topic, "sub");

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


            topicConn = connFactory.createTopicConnection("admin", "admin1");

            topicConn.setClientID("admin1");

            // create a topic session

            TopicSession topicSession = topicConn.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);

            // create a topic subscriber
            topicSubscriber=topicSession.createDurableSubscriber(topic, "sub");

            topicConn.start();



            System.out.println("Connection to the topic restablished!");
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

        boolean checkFicheiro;
        while(true) {
            try {

                Message m = topicSubscriber.receive();


                String doc = m.getStringProperty("xml");

                System.out.println("Messages received from topic!\n");
                System.out.println(doc);


                checkFicheiro = validateXML("/Users/jmcalves275/Desktop/Faculdade/Mestrado/IS/Assignment_1/IS2014/Stats Producer/scheme.xsd", doc);

                if (!checkFicheiro) {
                    System.out.println("XML file is invalid!!!");

                } else {
                    System.out.println("XML file is valid!!!");



                    unmarshal(doc);
                    produceStats();

                }
            } catch (JMSException e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                while (!check && !retryConnection()  ) ;



            } catch (NullPointerException e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                while (!check && !retryConnection() ) ;


            }
        }

    }

    public void produceStats(){



        hashmap.put("U.S.", 0);
        hashmap.put("Europe", 0);
        hashmap.put("Africa", 0);
        hashmap.put("Asia", 0);
        hashmap.put("Latin America", 0);
        hashmap.put("Middle East", 0);


        for (Iterator<Article> iterator = listaNoticias.getArticle().iterator(); iterator.hasNext();) {
            Article a = iterator.next();

            long dataNoticia=(a.getTimestamp()).longValue();

            Date startDate = new Date(dataNoticia);
            Date endDate   =new Date(System.currentTimeMillis());

            long interval  = endDate.getTime() - startDate.getTime();

            interval=TimeUnit.MILLISECONDS.toMinutes(interval);
            System.out.println("Titulo: "+a.getTitle()+" section:"+a.getSection()+" data: "+startDate+" intervalo: "+interval);
            if (interval>12*60) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }

      int numberNews=listaNoticias.getArticle().size();
        System.out.println(numberNews);
        for(Article a:listaNoticias.getArticle()){
            if(hashmap.containsKey(a.getSection()))
                hashmap.put(a.getSection(),hashmap.get(a.getSection())+1);


        }

        //escreve no ficheiro
        try {
            PrintWriter out = new PrintWriter("stats.txt");
            out.println("Noticias com menos de 12 horas: "+numberNews);


            out.println("Numero de noticias por categoria:");

            for(String section:hashmap.keySet()){
                out.println("\t"+section+":"+hashmap.get(section));
            }

            out.println("Criado a: "+new Date(System.currentTimeMillis()));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


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



        for(Article prov : listaProvisoria.getArticle()){

            boolean exists = false;
            for(Article a : listaNoticias.getArticle()){
                if(a.getTitle().equals(prov.getTitle())) {
                    exists = true;
                    break;
                }
            }

            if(!exists)
                listaNoticias.addArticle(prov);


        }


    }
    public static boolean validateXML(String xsdPath, String xml){

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsdPath));

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(xml.getBytes())));

        } catch(SAXException e) {
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static void main(String[] args) throws JMSException, NamingException {

        StatsProducer statsProducer = new StatsProducer();

        statsProducer.receive();



    }




}


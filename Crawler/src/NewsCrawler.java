import classes.NewsList;
import org.xml.sax.SAXException;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


public class NewsCrawler implements Runnable {

    public static Logger logger = new Logger("testeapp");

    private String xsdFile = "scheme.xsd";
    private Boolean on = true;

    private Crawler c;
    private JMS jms;
    private LinkedBlockingQueue<String> pool;
    private NewsList n;

    public NewsCrawler() {
        this.pool = new LinkedBlockingQueue<String>();
        this.c = new Crawler();
        this.jms = new JMS("topico", "admin", "admin1");
        (new Thread(jms)).start();
    }

    private void fetch(){
        if((n = this.c.crawl("CNN", 1)) != null)
            populateNewsList(this.n);
        else
            logger.log("Crawling error");
    }

    private void try2send(String xml){
        if(!jms.send(xml)) {
            logger.log("Send failed");
            this.pool.add(xml);

        }else{


       }

    }


    private void populateNewsList(NewsList n) {

        StringWriter sw = new StringWriter();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("classes");

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", "<?xml-stylesheet type=\"text/xsl\" href=\"transform.xsl\"?>\n");
            jaxbMarshaller.marshal(n, sw);

        } catch (JAXBException e) {
            logger.log(e.toString());
            e.printStackTrace();
            return;
        }

        if (this.validateXML(sw.toString())) {
            this.pool.add(sw.toString());

            //this.logger.log(Logger.poolSize + this.pool.size());
        }
    }

    private boolean validateXML(String xml) {

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource("/Users/jmcalves275/Desktop/Faculdade/Mestrado/IS/Assignment_1/IS2014/Crawler/scheme.xsd"));

            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(xml.getBytes())));

        } catch(SAXException e) {
            e.printStackTrace();
            logger.log(e.toString());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(e.toString());
            return false;
        }

        return true;
    }

    public void shutdown(){
        System.out.println("Saving shit");
    }

    public static void main(String[] args)  throws InterruptedException {


        NewsCrawler crawler = null;

        logger.log("Starting crawler");

        try {
            crawler = new NewsCrawler();
        } catch (Exception e) {
            crawler = null;
            logger.log("Couldn't start crawler");
        }

        if (crawler != null) {
            //crawler.loadQueue();
            (new Thread(crawler)).start();

        }
    }

    @Override
    public void run() {
        String data;
        while(this.on) {

            try {
                System.out.println("asd");
                logger.log("Fecthing news");
                this.fetch();
                data = this.pool.take();
                try2send(data);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                logger.log("Thread killed");
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.log("Thread sleep error");
                //e.printStackTrace();
            }
        }
    }
}

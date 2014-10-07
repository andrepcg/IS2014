import classes.Article;
import classes.NewsList;
import org.xml.sax.SAXException;
import parsers.CNN;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Scanner;
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
        this.pool = new LinkedBlockingQueue<>();
        this.c = new Crawler();
        this.jms = new JMS("topico", "admin", "admin");
    }

    private void running(){

    }

    private void menu(){
        while(on){
            int escolha;
            Scanner sc = new Scanner(System.in);
            

            System.out.println("### News Crawler ###");
            System.out.println("What do you want to crawl?");
            System.out.println("1. CNN");

            System.out.println("0. Exit");

            do{
                System.out.print("> ");
                escolha = sc.nextInt();
            }while (escolha > 1 || escolha < 0);

            if(escolha == 0)
                on = false;

            if(escolha == 1)
                cnnSections(sc);
        }
    }

    private void cnnSections(Scanner sc){
        int escolha;

        System.out.println("What sections do you want to crawl?");
        System.out.println("1. U.S.");
        System.out.println("2. Africa");
        System.out.println("3. Asia");
        System.out.println("4. Europe");
        System.out.println("5. Latin America");
        System.out.println("6. Middle East");
        System.out.println("7. All");
        System.out.println("8. Local file");
        System.out.println("0. Go back");

        do{
            System.out.print("> ");
            escolha = sc.nextInt();
        }while (escolha > 7 || escolha < 0);

        if(escolha == 0)
            return;
        else{
            NewsList n = this.c.crawl("CNN", escolha);
        }


    }


    private void fetch(){
        n = this.c.crawl("CNN", 1);
        populateClasses();
    }

    private void try2send(String xml){
        if(!jms.send(xml))
            this.pool.add(xml);

    }


    private void populateClasses() {

        StringWriter sw = new StringWriter();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("classes");

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", "<?xml-stylesheet type=\"text/xsl\" href=\"transform.xsl\"?>\n");
            jaxbMarshaller.marshal(this.n, sw);

        } catch (JAXBException e) {
            //this.logger.log(Logger.marshall);
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
            Schema schema = factory.newSchema(new StreamSource("scheme.xsd"));

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

    public static void main(String[] args) {

        NewsCrawler crawler = null;

        try {
            crawler = new NewsCrawler();
        } catch (Exception e) {
            crawler = null;
            //System.out.println(Logger.logFileError);
            System.out.println("Erro: caput, ja fostes");
        }

        if (crawler != null) {
            //crawler.loadQueue();
            //crawler.menu();

            (new Thread(crawler)).start();

            //crawler.fetch();

        }
    }

    @Override
    public void run() {
        String data;
        while(this.on) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                //this.logger.log(Logger.sleep);
                e.printStackTrace();
            }

            try {
                this.fetch();
                data = this.pool.take();
                try2send(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //this.logger.log(Logger.threadKilled);
            }
        }
    }
}

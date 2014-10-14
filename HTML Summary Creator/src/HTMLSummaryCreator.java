import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by Andre on 11-10-2014.
 */
public class HTMLSummaryCreator implements Runnable{

    private final String XSL = "html.xsl";
    private final String receiveFolder = ".\\received\\";

    private JMS jms;
    private boolean on = true;

    public static void main(String[] args)  throws InterruptedException {

        HTMLSummaryCreator summary = null;

        try {
            summary = new HTMLSummaryCreator();
        } catch (Exception e) {
            summary = null;
        }

        if (summary != null) {
            summary.generateHTML("123","out_1413207418677.xml");
            //(new Thread(summary)).start();
        }
    }

    public HTMLSummaryCreator(){
        this.jms = new JMS("jms/topic/project", "admin", "admin1", false, "SummaryCreator");
        (new Thread(jms)).start();
    }

    private boolean generateFiles(String XMLmessage){
        if(validateXML(XMLmessage)){
            boolean success = true;
            String timestamp = new Timestamp((new java.util.Date()).getTime()).toString();
            String xmlFile = "out_" + timestamp + ".xml";
            BufferedWriter fW;

            try {
                fW = new BufferedWriter(new FileWriter(receiveFolder + xmlFile));
                fW.write(XMLmessage, 0, XMLmessage.length());
                fW.newLine();
                fW.close();
                success = true;
            } catch (IOException e) {
                success = false;
                e.printStackTrace();

            }

            String html;
            if ( success && (html = this.generateHTML(timestamp, xmlFile)) != null) {
                System.out.println("HTML generated " + html);

            } else {


            }

        }

        return false;
    }

    private String generateHTML(String timestamp, String xmlFile) {

        String html = "HTML_" + timestamp + ".html";

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xslt = new StreamSource(this.XSL);

        try {

            Transformer transformer = factory.newTransformer(xslt);
            StreamSource text = new StreamSource(receiveFolder + xmlFile);
            transformer.transform(text, new StreamResult(html));

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
        return html;
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

    @Override
    public void run() {
        String data;
        while(this.on) {


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            data = this.jms.receive();

            if(data != null)
                generateFiles(data);
            else
                System.out.println("No data");
        }
    }
}

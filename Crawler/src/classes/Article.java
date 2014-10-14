package classes;

/**
 * Created by André on 14/10/2014.
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="image" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="video" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="author" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="corpus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="section" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "title",
        "url",
        "image",
        "video",
        "timestamp",
        "author",
        "corpus",
        "section"
})
public class Article {

    protected String title;
    @XmlSchemaType(name = "anyURI")
    protected String url;
    @XmlSchemaType(name = "anyURI")
    protected String image;
    @XmlSchemaType(name = "anyURI")
    protected String video;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger timestamp;
    protected List<String> author;
    protected String corpus;
    protected String section;

    /**
     * Gets the value of the title property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the url property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the image property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the value of the image property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setImage(String value) {
        this.image = value;
    }

    /**
     * Gets the value of the video property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVideo() {
        return video;
    }

    /**
     * Sets the value of the video property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVideo(String value) {
        this.video = value;
    }

    /**
     * Gets the value of the timestamp property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setTimestamp(BigInteger value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the author property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the author property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthor().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getAuthor() {
        if (author == null) {
            author = new ArrayList<String>();
        }
        return this.author;
    }

    /**
     * Gets the value of the corpus property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCorpus() {
        return corpus;
    }

    /**
     * Sets the value of the corpus property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCorpus(String value) {
        this.corpus = value;
    }

    /**
     * Gets the value of the section property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSection() {
        return section;
    }

    /**
     * Sets the value of the section property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSection(String value) {
        this.section = value;
    }

}
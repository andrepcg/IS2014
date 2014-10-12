package parsers;

import org.jsoup.nodes.Document;
import classes.Article;
import classes.NewsList;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by André on 21-09-2014.
 */
public class CNN extends Parser {
    private static String[] sections = {"US", "AFRICA", "ASIA", "EUROPE", "LATINAMERICA", "MIDDLEEAST"};
    String baseUrl = "http://edition.cnn.com/";
    String linkSelector = "#cnn_mtt1rgtarea .cnn_bulletbin a:not([href*=http]):not([href*=video]), .cnn_sectt2cntnt a:not([href*=video]), #cnn_maintoplive .cnn_relpostn a:first-of-type";


    SimpleDateFormat dateParser = new SimpleDateFormat("MMMM dd, yyyy HHmm", Locale.ENGLISH);


    public NewsList getNewsList(int section){
        Document doc = null;
        NewsList n = null;

        if(section <= sections.length)
            doc = getFromURL(baseUrl + sections[section - 1]);
        else if(section == sections.length + 1){
            NewsList novo = new NewsList();
            for(int i = 0; i < sections.length - 1; i++){
                NewsList a;
                doc = getFromURL(baseUrl + sections[i]);
                a = parseNews(doc.select(linkSelector), baseUrl);
                novo.getArticle().addAll(a.getArticle());
            }

            return novo;
        }

        else
            doc = getFromFile("");

        if(doc != null){
            n = parseNews(doc.select(linkSelector), baseUrl);
            if(n != null)
                return n;
            else
                return null;
        }

        return null;
    }




    long parseDate(String date){
        String parts[] = date.split(" ", 7);

        String parseableText = "";
        try {
            parseableText = String.format("%s %s %s %s", parts[0], parts[1], parts[2], parts[5]);

        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            return dateParser.parse(parseableText).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

       return 0;
    }

    private String getVideo(String url){
        Document doc = null;

        try {
            doc = Jsoup.connect(url).userAgent("Chrome").header("Accept-Language", "en-US").get();
            return doc.select("[bitrate=768x432_1300k_mp4]").text();

        } catch (SocketTimeoutException e) {
            System.out.println("Read timeout");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Article parseArticle(String url){

        Article a = new Article();

        Document doc = getFromURL(url);
        if(doc != null){
            Elements story = doc.select(".cnn_storyarea");

            if(story.select(".cnn_strytmstmp").text().isEmpty())
                return null;

            a.setTitle(story.select("h1").text());

            Elements autores = story.select(".cnnByline strong");
            for(Element e : autores)
                a.getAuthor().add(e.text());

            a.setTimestamp(BigInteger.valueOf(parseDate(story.select(".cnn_strytmstmp").text())));
            a.setCorpus(story.select(".cnn_storypgraphtxt").text());
            a.setImage(doc.select("head meta[itemprop=thumbnailUrl]").attr("content"));
            a.setUrl(doc.select("head meta[property=vr:canonical]").attr("content"));
            a.setSection(doc.select("#intl-menu [class=nav-on]").text());
            //a.setSection("U.S.");

            String video = doc.select(".OUTBRAIN").attr("data-src");
            if(video != null && video != ""){
                String v = "http://ht.cdn.turner.com/cnn/big" + getVideo(video + "?xml=true");
            }

            return a;
        }

        return null;
    }
}

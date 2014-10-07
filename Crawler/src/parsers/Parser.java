package parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import classes.Article;
import classes.NewsList;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;

/**
 * Created by André on 21-09-2014.
 */
public abstract class Parser {

    protected String baseUrl;
    protected String linkSelector;
    protected SimpleDateFormat dateParser;

    protected static Document getFromFile(String filename) {
        return null;
    }

    protected static Document getFromURL(String url) {
        Document doc = null;

        try {
            doc = Jsoup.connect(url).userAgent("Chrome").header("Accept-Language", "en-US").get();
            return doc;

        } catch (SocketTimeoutException e) {
            System.out.println("Read timeout");

        } catch (IOException e) {
            System.out.println("HTTP get error");
            //e.printStackTrace();
        }
        return null;
    }

    protected NewsList parseNews(Elements elements, String url){
        NewsList n = new NewsList();
        n.getArticle();

        for (Element e : elements){
            Article a = parseArticle(url + e.attr("href"));
            if(a != null)
                n.getArticle().add(a);
        }

        return n;
    }

    public abstract Article parseArticle(String url);
    abstract long parseDate(String date);
    public abstract NewsList getNewsList(int section);
}



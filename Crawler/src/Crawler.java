import classes.NewsList;
import parsers.*;

import java.util.HashMap;

/**
 * Created by André on 21-09-2014.
 */

public class Crawler {

    private HashMap<String, Parser> parsers;

    public Crawler() {
        parsers = new HashMap<String, Parser>();
        parsers.put("CNN", new CNN());
        
    }

    public NewsList crawl(String site, int section){
        NewsList n = new NewsList();

        Parser p = parsers.get(site);

        if(p != null)
            n = p.getNewsList(section);


        return n;
    }

}

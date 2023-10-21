package run.runnable.commontool.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.Iterator;

/**
 * @author Asher
 * on 2023/10/2
 */
public class CommonUtil {


    /**
     * get html content by xpath
     * @param resourceHtml
     * @param xpath
     * @return {@link String}
     */
    public static String getHtmlContent(String resourceHtml, String xpath) {
        Document doc = Jsoup.parse(resourceHtml, "utf8");
        if (StringUtils.hasLength(xpath)){
            final Elements elements = doc.selectXpath(xpath);
            return elements.html();
        }else {
            return doc.html();
        }
    }
}

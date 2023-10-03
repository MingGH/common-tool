package run.runnable.commontool.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Asher
 * on 2023/1/10
 */
public class RegexUtil {

    public static List<String> getRegexString(String str, String reg){
        Matcher matcher = Pattern.compile(reg).matcher(str);
        List<String> matchStrs = new ArrayList<>();
        while (matcher.find()) {
            matchStrs.add(matcher.group());
        }
        return matchStrs;
    }
}

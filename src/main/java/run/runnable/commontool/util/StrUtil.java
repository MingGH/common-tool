package run.runnable.commontool.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Asher
 * on 2023/10/14
 */
public class StrUtil {

    /**
     * 是否中文
     *
     * @param text text
     * @return boolean
     */
    public static boolean isChinese(String text) {
        // 定义中文字符的正则表达式
        String regex = "^[\\u4e00-\\u9fa5]+$";
        // 创建Pattern对象
        Pattern pattern = Pattern.compile(regex);
        // 检查是否匹配
        return pattern.matcher(text).matches();
    }

    /**
     * 按中文划分字符
     *
     * @param mixedChinese 混合中文
     * @return {@link List}<{@link String}>
     */
    public static List<String> splitByChinese(String mixedChinese){
        Set<Character.UnicodeBlock> chineseUnicodeBlocks = Set.of(Character.UnicodeBlock.CJK_COMPATIBILITY,
            Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS,
            Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS,
            Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT,
            Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT,
            Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
            Character.UnicodeBlock.KANGXI_RADICALS,
            Character.UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS
        );

        int chFlag = 0;
        List<List<Character>> data = new ArrayList<>();
        List<Character> characters = null;
        for (char c : mixedChinese.toCharArray()) {
            if (chineseUnicodeBlocks.contains(Character.UnicodeBlock.of(c))) {
                if (characters != null) {
                    data.add(characters);
                    characters = null;
                }else {
                    chFlag ++;
                }
            } else {
                if (characters == null) {
                    characters = new ArrayList<>();
                }
                characters.add(c);
            }
        }
        if (characters != null){
            data.add(characters);
        }
        return data.stream()
                .map(it -> it.stream().map(String::valueOf).reduce((acc, item) -> acc + item).get())
                .collect(Collectors.toList());
    }
}

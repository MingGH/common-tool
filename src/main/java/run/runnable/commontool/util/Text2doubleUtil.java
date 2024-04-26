package run.runnable.commontool.util;

import java.util.HashMap;

public interface Text2doubleUtil {

    static double text2double(String text) {
        String[] units = new String[] { "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

        String[] scales = new String[] { "十", "百", "千", "万", "亿" };

        HashMap<String, ScaleIncrementPair> numWord = new HashMap<>();

        for (int i = 0; i < units.length; i++) {
            numWord.put(units[i], new ScaleIncrementPair(1, i));
        }

        numWord.put("零", new ScaleIncrementPair(1, 0));
        numWord.put("两", new ScaleIncrementPair(1, 2));

        for (int i = 0; i < scales.length; i++) {
            numWord.put(scales[i], new ScaleIncrementPair(Math.pow(10, (i + 1)), 0));
        }

        double current = 0;
        double result = 0;

        for (char character : text.toCharArray()) {
            ScaleIncrementPair scaleIncrement = numWord.get(String.valueOf(character));
            current = current * scaleIncrement.scale + scaleIncrement.increment;
            if (scaleIncrement.scale > 10) {
                result += current;
                current = 0;
            }
        }

        return result + current;
    }

    record ScaleIncrementPair(double scale, int increment) {

    }
}

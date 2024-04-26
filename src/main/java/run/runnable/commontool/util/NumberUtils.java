package run.runnable.commontool.util;

public class NumberUtils {

    private static final char[] cnArr = new char[] {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};
    private static final char[] chArr = new char[] {'十', '百', '千', '万', '亿'};

    /**
     * 将汉字中的数字转换为阿拉伯数字
     * 最大支持单位（亿）
     * @param chineseNum
     * @return
     */
    public static int chineseNumToArabicNum(String chineseNum) {
        int temp = 1;// 单位大小
        int result = 0;// 结果
        int count = 1;// 当前单位上的值，例如当前九十，temp = 10， count = 9
        boolean firstIsUnits = false;// 判断首位是否为单位，处理十六、十六万、十六亿这种情况
        for (int i = 0; i < chineseNum.length(); i++){
            boolean isUnits = true;// 是否为单位
            char nowChar = chineseNum.charAt(i);
            // 如果是数字
            for (int j = 0; j < cnArr.length; j++) {
                if (nowChar == cnArr[j]) {
                    // 如果首位是单位，则不合并至结果
                    if (firstIsUnits) {
                        count += j;
                        firstIsUnits = false;
                        break;
                    }
                    // 非首位数字则将前边数值合并至结果
                    if (i > 0) {
                        result += count;
                        // 重置单位
                        temp = 1;
                    }
                    count = j;
                    isUnits = false;
                }
            }
            // 如果是单位
            if (isUnits) {
                temp = switch (nowChar) {
                    case '十' -> 10;
                    case '百' -> 100;
                    case '千' -> 1000;
                    case '万' -> 10000;
                    case '亿' -> 100000000;
                    default -> 1;
                };
                // 百万、千万
                if (temp == 10000 && i > 2) {
                    result = (result / 100000000) * 100000000 + result % 100000000 * 10000;
                }
                // 百亿、千亿
                if (temp == 100000000 && i > 2) {
                    result *= 100000000;
                }
                // 首位是单位
                if (i == 0) {
                    firstIsUnits = true;
                }
                count *= temp;
            }
            // 如果是最后一位则将当前值合并至结果
            if (i == chineseNum.length() - 1) {
                result += count;
            }
        }
        return result;
    }

    /**
     * 中文数字转阿拉伯数字
     * 最大支持单位（亿）
     * @param chineseNum
     * @return
     */
    public static int chineseNumToArabicNum2(String chineseNum) {
        chineseNum = chineseNum.replaceAll("零", "");
        int endIndex = 0;
        int result = 0;
        if (chineseNum.contains("亿")) {
            endIndex = chineseNum.indexOf("亿");
            result = chineseNumToArabicNum2(chineseNum.substring(0, endIndex)) * 100000000;
            chineseNum = chineseNum.substring(endIndex + 1);
        }
        if (chineseNum.contains("万")) {
            endIndex = chineseNum.indexOf("万");
            result += chineseNumToArabicNum2(chineseNum.substring(0, endIndex)) * 10000;
            chineseNum = chineseNum.substring(endIndex + 1);
        }
        if (chineseNum.contains("千")) {
            char num = chineseNum.charAt(0);
            for (int i = 0; i < cnArr.length; i++) {
                if (num == cnArr[i]) {
                    result += i * 1000;
                    chineseNum = chineseNum.substring(2);
                    break;
                }
            }
        }
        if (chineseNum.contains("百")) {
            char num = chineseNum.charAt(0);
            for (int i = 0; i < cnArr.length; i++) {
                if (num == cnArr[i]) {
                    result += i * 100;
                    chineseNum = chineseNum.substring(2);
                    break;
                }
            }
        }
        if (chineseNum.contains("十")) {
            char num = chineseNum.charAt(0);
            for (int i = 0; i < cnArr.length; i++) {
                if (num == '十') {
                    result += 10;
                    chineseNum = chineseNum.substring(1);
                    break;
                } else if (num == cnArr[i]) {
                    result += i * 10;
                    chineseNum = chineseNum.substring(2);
                    break;
                }
            }
        }
        if (chineseNum.length() > 0) {
            char num = chineseNum.charAt(0);
            for (int i = 0; i < cnArr.length; i++) {
                if (num == cnArr[i]) {
                    result += i;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 将数字转换为中文数字
     * 最大支持单位（亿）
     * @param intInput
     * @return
     */
    public static String arabicNumToChineseNum(int intInput) {
        String result = "";
        int temp = 1000;
        int count = 0;
        int index = 2;
        if (intInput < 10000) {
            while (temp >= 1) {
                if ((intInput / temp) > 0) {
                    count = intInput / temp;
                    result += cnArr[count];
                    if (temp >= 10) {
                        result += chArr[index];
                    }
                    intInput -= count * temp;
                }else if (result.length() > 1 && ! result.substring(result.length() - 1).equals("零")) {
                    result += "零";
                }
                temp = temp / 10;
                index --;
            }
        } else if (intInput < 100000000) {
            int high = intInput / 10000;
            int low = intInput - high * 10000;
            String highResult = arabicNumToChineseNum(high) + "万";
            String lowResult = arabicNumToChineseNum(low);
            result = highResult + (lowResult.contains("千") ? lowResult : "零" + lowResult);
        } else if (intInput > 100000000) {
            int high = intInput / 100000000;
            int mid = (intInput - high * 100000000) / 10000;
            int low = intInput - (intInput / 10000) * 10000;
            String highResult = arabicNumToChineseNum(high) + "亿";
            String midResult = arabicNumToChineseNum(mid) + "万";
            String lowResult = arabicNumToChineseNum(low);
            result = highResult +
                    (midResult.contains("千") ? midResult : "零" + midResult) +
                    (lowResult.contains("千") ? lowResult : "零" + lowResult);
        }

        if (result.length() > 2 && result.substring(result.length() - 1).equals("零")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

}

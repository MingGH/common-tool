package run.runnable.commontool.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public interface DateUtil {

    /**
     * 转香港时间
     *
     * @param inputTime 输入时间
     * @return {@link String }
     * @author asher
     * @date 2023/10/19
     */
    public static String convertToHongKongTime(String inputTime) {
        ZonedDateTime hongKongTime = Instant.parse(inputTime).atZone(ZoneId.of("Asia/Hong_Kong"));
        return hongKongTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
    }
}

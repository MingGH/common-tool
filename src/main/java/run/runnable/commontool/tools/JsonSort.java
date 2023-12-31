package run.runnable.commontool.tools;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import run.runnable.commontool.util.ClipboardUtil;

import static com.alibaba.fastjson2.JSONWriter.Feature.*;

/**
 * JSON sort
 *
 * @author asher
 * @date 2023/10/21
 */
public class JsonSort {

    public static String execute() {
        String content = ClipboardUtil.getClipboardStringData();
        JSONObject originJson = JSON.parseObject(content);
        String result = originJson.toString(PrettyFormat, MapSortField, WriteMapNullValue);
        ClipboardUtil.saveClipboardStringData(result);
        return result;
    }

}

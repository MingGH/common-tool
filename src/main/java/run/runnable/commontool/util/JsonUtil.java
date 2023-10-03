package run.runnable.commontool.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author Asher
 * on 2023/10/2
 */
public class JsonUtil {

    public static JsonNode string2JsonNode(String str){
        try {
            return new ObjectMapper().readTree(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T jsonNode2Entity(TreeNode n, Class<T> valueType){
        try {
            return new ObjectMapper().treeToValue(n, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode xml2JsonNode(String xml){
        try {
            return new XmlMapper().readTree(xml);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



}

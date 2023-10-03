package run.runnable.commontool.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Asher
 * on 2023/10/2
 */
public class JsonUtil {

    public static JsonNode String2JsonNode(String str){
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


}

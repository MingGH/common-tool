package run.runnable.commontool.util;


import ch.qos.logback.core.testUtil.FileTestUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Asher
 * on 2023/10/3
 */
class CommonUtilTest {

    void getIterableFromIterator() {
        System.out.println("");
    }

    @Test
    void testGetIterableFromIterator() {
    }


    @Test
    void getHtmlContent() throws IOException {
        getResourceContent("commonutil/htmlContent.html");

    }

    public static String getResourceContent(String path) throws IOException {
        ClassLoader classLoader = CommonUtilTest.class.getClassLoader();
        File file = new File(classLoader.getResource(path).getFile());

        return FileUtils.readFileToString(file);
    }

    public static File getResourceFile(String path){
        ClassLoader classLoader = CommonUtilTest.class.getClassLoader();
        return new File(classLoader.getResource(path).getFile());
    }
}
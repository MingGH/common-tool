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
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("commonutil/htmlContent.html").getFile());

        final String htmlContent = FileUtils.readFileToString(file);
        System.out.println(CommonUtil.getHtmlContent(htmlContent, null));

    }
}
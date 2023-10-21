package run.runnable.commontool.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Asher
 * on 2023/10/3
 */
public class ImageUtil {

    public static boolean isBadImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (null == image) {
                return true;
            }
            return image.getWidth() <= 0 || image.getHeight() <= 0;
        } catch (Exception e) {
            return true;
        }
    }
}

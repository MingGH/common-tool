package run.runnable.commontool.util;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Asher
 * on 2023/10/3
 */
public class FileUtil {

    public static Mono<Boolean> asyncDownloadPic(String url, String savePath){
        return Mono.fromCallable(() -> {
            return downloadPic(url, savePath);
        });
    }

    public static boolean downloadPic(String url, String savePath){
        try(InputStream in = new URL(url).openStream()){
            Files.copy(in, Paths.get(savePath));
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package run.runnable.commontool.util;

import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Asher
 * on 2023/10/3
 */
public interface FileUtil {

    /**
     * 异步下载 图片
     *
     * @param url      网址
     * @param savePath 保存路径
     * @return {@link Mono}<{@link Boolean}>
     */
    static Mono<Boolean> asyncDownloadPic(String url, String savePath){
        return Mono.fromCallable(() -> downloadPic(url, savePath));
    }

    /**
     * 下载 图片
     *
     * @param url      网址
     * @param savePath 保存路径
     * @return boolean
     */
    static boolean downloadPic(String url, String savePath){
        try(InputStream in = new URL(url).openStream()){
            Files.copy(in, Paths.get(savePath));
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字节写入文件
     *
     * @param bytes    字节
     * @param filePath 文件路径
     * @throws IOException ioexception
     */
    static void writeBytesToFile(byte[] bytes, String filePath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);){
            fileOutputStream.write(bytes);
        }
    }

    /**
     * 使用 Deflater 进行数据压缩
     *
     * @param content 内容
     * @return {@link byte[]}
     */
    static byte[] compressedByDeflater(byte[] content){
        Deflater deflater = new Deflater();
        deflater.setInput(content);
        deflater.finish();

        byte[] compressedData = new byte[content.length];
        deflater.deflate(compressedData);
        deflater.end();
        return compressedData;
    }

    /**
     * 解压缩 by deflater
     *
     * @param content 内容
     * @return {@link byte[]}
     */
    @SneakyThrows
    static byte[] unCompressedByDeflater(byte[] content) {
        // 创建 Inflater 对象
        Inflater inflater = new Inflater();
        inflater.setInput(content);

        // 创建一个足够大的缓冲区来存储解压缩后的数据
        byte[] buffer = new byte[1024];

        // 解压缩数据
        int decompressedDataLength = inflater.inflate(buffer);

        // 获取解压缩后的数据
        byte[] decompressedData = new byte[decompressedDataLength];
        System.arraycopy(buffer, 0, decompressedData, 0, decompressedDataLength);
        return decompressedData;
    }

}

package run.runnable.commontool.util;

import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.runnable.commontool.entity.ChunkFileInfo;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Predicate;
import java.util.stream.BaseStream;
import java.util.stream.Stream;
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

    /**
     * Read the contents of a file line by line, supporting backpressure
     *
     * @param path 路径
     * @return {@link Flux}<{@link String}>
     */
    static Flux<String> readLines(String path){
        return Flux.using(
                () -> Files.lines(Path.of(path)),
                Flux::fromStream,
                BaseStream::close
        );
    }


    /**
     * Append content to a file
     *
     * @param filePath filePath
     * @param content  content
     * @return {@link Mono}<{@link Void}>
     */
    @SneakyThrows
    static Mono<Void> appendToFile(String filePath, String content) {
        return Mono.fromRunnable(() -> {
            try {
                if (!Files.exists(Path.of(filePath))) {
                    Files.createFile(Path.of(filePath));
                }
                Files.write(Path.of(filePath), (content + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException("Failed to append to file", e);
            }
        }).onErrorResume(e -> Mono.error(new RuntimeException("Failed to append to file", e))).then();
    }

    @SneakyThrows
    static RandomAccessFile newRandomAccessFile(String path) {
        return new RandomAccessFile(path, "r");
    }


    /**
     * Split a file into multiple files of specified size
     *
     * @param file      file
     * @param chunkSize chunkSize
     * @return {@link Flux}<{@link ChunkFileInfo}>
     */
    static Flux<ChunkFileInfo> split2ChunkedFiles(RandomAccessFile file, int chunkSize) {
        return Flux.create(emitter -> {
            try {
                FileChannel channel = file.getChannel();
                long fileSize = channel.size();
                long currentPosition = 0;

                while (currentPosition < fileSize ){

                    while (emitter.requestedFromDownstream() == 0 && !emitter.isCancelled()) {
                        //waiting request
                    }

                    long remainingSize = fileSize - currentPosition;
                    int readSize = (int) Math.min(remainingSize, chunkSize);

                    ByteBuffer buffer = ByteBuffer.allocate(readSize);
                    channel.read(buffer, currentPosition);

                    byte[] byteArray = new byte[readSize];
                    buffer.flip();
                    buffer.get(byteArray);
                    emitter.next(new ChunkFileInfo(currentPosition, currentPosition + readSize, readSize, byteArray));
                    currentPosition += readSize;
                }
                emitter.complete();
            } catch (IOException e) {
                emitter.error(e);
            }
        });
    }

    /**
     * 获取目录中所有文件，以响应式编程的方式
     *
     * @param directory 目录
     * @param filter    filter
     * @return {@link Flux}<{@link Path}>
     */
    private static Flux<Path> listFilesInDirectory(Path directory, Predicate<Path> filter) {
        Mono<Stream<Path>> currentFileMono = Mono.fromCallable(() -> Files.list(directory));
        return currentFileMono
                .subscribeOn(Schedulers.boundedElastic())
                .flux()
                .flatMap(it -> Flux.fromStream(it).filter(filter))
                .flatMap(it -> {
                    if (it.toFile().isDirectory()){
                        return listFilesInDirectory(it, filter);
                    }else {
                        return Flux.just(it);
                    }
                })
                .filter(Files::isRegularFile)
                .subscribeOn(Schedulers.boundedElastic());
    }

}

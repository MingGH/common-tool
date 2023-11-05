package run.runnable.commontool.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.stream.ChunkedFile;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.zip.Deflater;


/**
 * @author Asher
 * on 2023/11/5
 */
@Slf4j
class CipherUtilTest {

    @Test
    void encryptByECC() throws IOException {
        String password = "p@ssW0rd";
        final byte[] bytes = CipherUtil.encryptByECC(password.getBytes(),
                "secp256k1",
                "ECIES",
                "PrivateKey.pem",
                "PublicKeyPath.pem");
        FileUtil.writeBytesToFile(bytes, "password.enc");
        Assertions.assertNotNull(bytes);
    }


    @Test
    void encryptByECC_publicKey() throws IOException {
        String password = "p@ssW0rd";
        final File publicKeyFile = CommonUtilTest.getResourceFile("cipher/PublicKeyPath.pem");
        final byte[] bytes = CipherUtil.encryptByECC(password.getBytes(),
                "secp256k1",
                "ECIES",
                publicKeyFile,
                "EC");


        final File privateKeyFile = CommonUtilTest.getResourceFile("cipher/PrivateKey.pem");
        final byte[] encryptBytes = FileUtils.readFileToByteArray(CommonUtilTest.getResourceFile("cipher/password.enc"));
        final byte[] decryptContent = CipherUtil.decryptByEllipticCurveCrypt(encryptBytes, privateKeyFile, "EC", "ECIES");
        final String decryptStr = new String(decryptContent);
        System.out.println(decryptStr);
        Assertions.assertEquals("p@ssW0rd", decryptStr);
    }

    public static void main(String[] args) {

    }


    @Test
    void encryptBigFile() throws IOException {
        Mono.just("/Users/asher/Desktop/temp/test.mp4")
                .flatMap(it -> {
                    RandomAccessFile randomAccessFile = null;
                    try {
                        randomAccessFile = new RandomAccessFile(it, "r");
                    } catch (FileNotFoundException e) {
                        return Mono.error(new RuntimeException());
                    }
                    return Mono.just(randomAccessFile);
                })
                .flux()
                .flatMap(file -> {
                    final long fileLength;
                    try {
                        fileLength = file.length();
                    } catch (IOException e) {
                        return Flux.error(new RuntimeException(e));
                    }

                    int chunkSize = 1024 * 1024 * 3;
                    long currentPosition = 0;


                    List<ChunkedFile> chunkedFiles = new ArrayList<>();
                    while (currentPosition < fileLength) {
                        long remaining = fileLength - currentPosition;
                        long bytesToRead = Math.min(remaining, chunkSize);

                        try {
                            ChunkedFile chunkedFile = new ChunkedFile(file, currentPosition, bytesToRead, chunkSize);
                            chunkedFiles.add(chunkedFile);
                        } catch (IOException e) {
                            return Flux.error(new RuntimeException(e));
                        }
                        currentPosition += bytesToRead;
                    }
                    return Flux.fromIterable(chunkedFiles).limitRate(10);
                })
                .doOnNext(it -> {
                    log.info("startOffset:{}  endOffset:{}", it.startOffset(), it.endOffset());
                })
                .flatMap(chunkedFile -> {
                    ByteBufAllocator byteBufAllocator = new PooledByteBufAllocator();
                    try {
                        // 创建一个 ByteBuf 用于存储读取的数据
                        ByteBuf byteBuf = Unpooled.buffer();

                        // 读取 ChunkedFile 中的数据块
                        while (!chunkedFile.isEndOfInput()) {
                            chunkedFile.readChunk(byteBufAllocator);
                        }

                        // 将 ByteBuf 转换为字节数组
                        byte[] data = new byte[byteBuf.readableBytes()];
                        byteBuf.readBytes(data);
                        System.out.println("123");
                        return Flux.just(data)
                            .map(chunkByte -> {
                                final File publicKeyFile = CommonUtilTest.getResourceFile("cipher/PublicKeyPath.pem");
                                try {
                                    final byte[] bytes = CipherUtil.encryptByECC(
                                            FileUtils.readFileToByteArray(new File("/Users/asher/Desktop/temp/test2.mp4")),
                                            "secp256k1",
                                            "ECIES",
                                            publicKeyFile,
                                            "EC");
                                    return Base64.getEncoder().encodeToString(bytes);
                                } catch (IOException e) {
                                    return Flux.error(new RuntimeException(e));
                                }
                            })
                                .cast(String.class)
                                .doOnNext(log::info);
                    } catch (Exception e) {
                        return Flux.error(new RuntimeException(e));
                    }
                })
                .doOnNext(str -> {
                    log.info(str);
                })
//                .cast(String.class)
//                .map(it -> {
//                    FileUtil
//                })
                .subscribe();



//        final long startTime = System.currentTimeMillis();
//        final File publicKeyFile = CommonUtilTest.getResourceFile("cipher/PublicKeyPath.pem");
//        final byte[] bytes = CipherUtil.encryptByECC(
//                FileUtils.readFileToByteArray(new File("/Users/asher/Desktop/temp/test2.mp4")),
//                "secp256k1",
//                "ECIES",
//                publicKeyFile,
//                "EC");
//
//
//        // 使用 Base64 编码压缩后的数据
//        String base64Encoded = Base64.getEncoder().encodeToString(bytes);
//        FileUtils.writeStringToFile(new File("/Users/asher/Desktop/temp/encrypted_test2.mp4.base64"), base64Encoded);
//        final long endTime = System.currentTimeMillis();
//        System.out.println("time:" + (endTime - startTime));

//        String filePath = "path/to/your/file"; // 替换为实际的文件路径
//        long start = 0; // 文件起始位置
//        long end = 1024; // 文件结束位置
//
//        try (RandomAccessFile raf = new RandomAccessFile(new File(filePath), "r")) {
//            FileChannel channel = raf.getChannel();
//            ByteBuffer buffer = ByteBuffer.allocate((int) (end - start));
//
//            channel.position(start);
//            channel.read(buffer);
//            buffer.flip();
//
//            byte[] data = new byte[buffer.remaining()];
//            buffer.get(data);
//
//            // 处理读取的数据
//            System.out.println("Read data: " + new String(data));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//        final File privateKeyFile = CommonUtilTest.getResourceFile("cipher/PrivateKey.pem");
//        final byte[] encryptBytes = FileUtils.readFileToByteArray(CommonUtilTest.getResourceFile("cipher/password.enc"));
//        final byte[] decryptContent = CipherUtil.decryptByEllipticCurveCrypt(encryptBytes, privateKeyFile, "EC", "ECIES");
//        final String decryptStr = new String(decryptContent);
//        System.out.println(decryptStr);
//        Assertions.assertEquals("p@ssW0rd", decryptStr);
    }

    @Test
    void decryptByEllipticCurveCrypt() throws IOException {
        final File resourceFile = CommonUtilTest.getResourceFile("cipher/PrivateKey.pem");
        final byte[] encryptBytes = FileUtils.readFileToByteArray(CommonUtilTest.getResourceFile("cipher/password.enc"));
        final byte[] decryptContent = CipherUtil.decryptByEllipticCurveCrypt(encryptBytes, resourceFile, "EC", "ECIES");
        final String decryptStr = new String(decryptContent);
        System.out.println(decryptStr);
        Assertions.assertEquals("p@ssW0rd", decryptStr);
    }
}
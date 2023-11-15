package run.runnable.commontool.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.runnable.commontool.entity.ChunkFileInfo;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static run.runnable.commontool.util.FileUtil.appendToFile;
import static run.runnable.commontool.util.FileUtil.split2ChunkedFiles;
import static run.runnable.commontool.util.KeyFileUtil.savePrivateKey;
import static run.runnable.commontool.util.KeyFileUtil.savePublicKey;

/**
 * @author Asher
 * on 2023/11/5
 */
public interface CipherUtil {

    Logger log = LoggerFactory.getLogger(CipherUtil.class);

    byte[] derivation = Hex.decode("202122232425263738393a3b3c3d3e3f");
    byte[] encoding   = Hex.decode("303132333435362728292a2b2c2d2e2f");


    /**
     * Encrypt by Elliptic Curve Crypt
     *
     * @param encryptContent     加密内容
     * @param curveName          曲线名称 例如：
     *                           secp256k1：这是比特币和以太坊等加密货币中广泛使用的椭圆曲线参数。它具有 256 位的长度，并提供了良好的安全性和性能。
     *                           secp256r1/prime256v1：这是一种常见的椭圆曲线参数，也被称为 NIST P-256。它被广泛用于许多安全协议和应用中，提供了适当的安全性和性能。
     *                           secp384r1：也称为 NIST P-384，它是一种具有 384 位长度的椭圆曲线参数，提供了比 secp256k1 和 secp256r1 更高的安全性，但可能会稍微降低性能。
     *                           secp521r1：也称为 NIST P-521，它是一种具有 521 位长度的椭圆曲线参数，提供了最高级别的安全性，但可能会牺牲一些性能。
     * @param transformation     transformation : ECIES（Elliptic Curve Integrated Encryption Scheme）加密方案
     * @param publicFile  公钥路径
     * @return {@link byte[]}
     */
    @SneakyThrows
    static byte[] encryptByECC(byte[] encryptContent,
                               String curveName,
                               String transformation,
                               File publicFile,
                               String algorithm) {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec(curveName));

        final PublicKey publicKey = loadPublicKey(publicFile, algorithm);

        // 使用公钥进行加密
        IESParameterSpec params = new IESParameterSpec(derivation, encoding, 128, 128, null);
        Cipher encryptCipher = Cipher.getInstance(transformation, "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey, params);

        return encryptCipher.doFinal(encryptContent);
    }

    /**
     * Encrypt by Elliptic Curve Crypt
     *
     * @param encryptContent     加密内容
     * @param curveName          曲线名称 例如：
     *                           secp256k1：这是比特币和以太坊等加密货币中广泛使用的椭圆曲线参数。它具有 256 位的长度，并提供了良好的安全性和性能。
     *                           secp256r1/prime256v1：这是一种常见的椭圆曲线参数，也被称为 NIST P-256。它被广泛用于许多安全协议和应用中，提供了适当的安全性和性能。
     *                           secp384r1：也称为 NIST P-384，它是一种具有 384 位长度的椭圆曲线参数，提供了比 secp256k1 和 secp256r1 更高的安全性，但可能会稍微降低性能。
     *                           secp521r1：也称为 NIST P-521，它是一种具有 521 位长度的椭圆曲线参数，提供了最高级别的安全性，但可能会牺牲一些性能。
     * @param transformation     transformation : ECIES（Elliptic Curve Integrated Encryption Scheme）加密方案
     * @param savePrivateKeyPath 保存私钥路径
     * @param savePublicKeyPath  保存公钥路径
     * @return {@link byte[]}
     */
    @SneakyThrows
    static byte[] encryptByECC(byte[] encryptContent,
                               String curveName,
                               String transformation,
                               String savePrivateKeyPath,
                               String savePublicKeyPath) {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec(curveName));

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        final PrivateKey privateKey = keyPair.getPrivate();
        final PublicKey publicKey = keyPair.getPublic();
        savePrivateKey(privateKey, savePrivateKeyPath);
        savePublicKey(publicKey, savePublicKeyPath);


        // 使用公钥进行加密
        IESParameterSpec params = new IESParameterSpec(derivation, encoding, 128, 128, null);
        Cipher encryptCipher = Cipher.getInstance(transformation, "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey, params);

        return encryptCipher.doFinal(encryptContent);
    }

    /**
     * 通过 Elliptic Curve Crypt 解密
     *
     * @param decryptContent 需要解密内容
     * @param privateKey     私钥
     * @param transformation 转型
     * @return {@link byte[]}
     */
    @SneakyThrows
    static byte[] decryptByEllipticCurveCrypt(byte[] decryptContent,
                                      PrivateKey privateKey,
                                      String transformation){
        // 使用私钥进行解密
        Cipher decryptCipher = Cipher.getInstance(transformation, "BC");
        IESParameterSpec params = new IESParameterSpec(derivation, encoding, 128, 128, null);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey, params);

        return decryptCipher.doFinal(decryptContent);
    }

    /**
     * Decrypt  Elliptic Curve Crypt
     *
     * @param decryptContent 解密内容
     * @param privateKeyPath 私钥路径
     * @param algorithm      算法
     * @param transformation 转型
     * @return {@link byte[]}
     */
    @SneakyThrows
    static byte[] decryptByEllipticCurveCrypt(byte[] decryptContent,
                                      String privateKeyPath,
                                      String algorithm,
                                      String transformation){
        return decryptByEllipticCurveCrypt(decryptContent, loadPrivateKey(privateKeyPath, algorithm), transformation);
    }

    @SneakyThrows
    static byte[] decryptByEllipticCurveCrypt(byte[] decryptContent,
                                              File privateFile,
                                              String algorithm,
                                              String transformation){
        return decryptByEllipticCurveCrypt(decryptContent, loadPrivateKey(privateFile, algorithm), transformation);
    }

    /**
     * 加载私钥
     *
     * @param filePath  文件路径
     * @param algorithm 算法
     * @return {@link PrivateKey}
     */
    @SneakyThrows
    static PrivateKey loadPrivateKey(String filePath, String algorithm) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)){
            byte[] privateKeyBytes = fileInputStream.readAllBytes();
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePrivate(privateKeySpec);
        }
    }

    /**
     * 加载私钥
     *
     * @param file  文件路径
     * @param algorithm 算法
     * @return {@link PrivateKey}
     */
    @SneakyThrows
    static PrivateKey loadPrivateKey(File file, String algorithm) {
        Security.addProvider(new BouncyCastleProvider());
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            byte[] privateKeyBytes = fileInputStream.readAllBytes();
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
            return keyFactory.generatePrivate(privateKeySpec);
        }
    }

    /**
     * 加载私钥
     *
     * @param file  文件路径
     * @param algorithm 算法
     * @return {@link PrivateKey}
     */
    @SneakyThrows
    static PublicKey loadPublicKey(File file, String algorithm) {
        Security.addProvider(new BouncyCastleProvider());
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            X509EncodedKeySpec privateKeySpec = new X509EncodedKeySpec(fileInputStream.readAllBytes());
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
            return keyFactory.generatePublic(privateKeySpec);
        }
    }

    /**
     * Encrypt large files in parallel, split them into multiple byte arrays for encryption, and finally convert to base64 encoding.
     *
     * @param filePath       Files that need to be encrypted
     * @param targetFilePath The file name of the encrypted file
     * @param chunkSize      File split size
     * @param publicKey     public key
     * @return {@link Flux}<{@link Void}>
     */
    static Flux<Void> encryptBigFile(String filePath, String targetFilePath, int chunkSize, File publicKey){
        return Mono.just(filePath)
                .doFirst(deleteFile(targetFilePath))
                .map(FileUtil::newRandomAccessFile).flux()
                .flatMap(file -> split2ChunkedFiles(file, chunkSize).limitRate(8))
                .doOnNext(it -> log.info("startOffset:{} endOffset:{}", it.getStartOffset(), it.getEndOffset()))
                .flatMapSequential(chunkedFile -> {
                    long startOffset = chunkedFile.getStartOffset();
                    long endOffset = chunkedFile.getEndOffset();
                    return Mono.just(chunkedFile.getBytes())
                            .publishOn(Schedulers.boundedElastic())
                            .flux()
                            .doOnNext(it -> log.info("starting encrypt chunk file"))
                            .map(chunkByte -> encryptByECC(chunkByte, "secp256k1", "ECIES", publicKey, "EC"))
                            .map(encryptBytes -> Base64.getEncoder().encodeToString(encryptBytes))
                            .map(base64 -> startOffset + ":" + endOffset + ":" +  base64)
                            ;
                })
                .concatMap(content -> appendToFile(targetFilePath, content));
    }

    /**
     * Decrypt the file encrypted by the encryptBigFile method and restore it to the same file
     *
     * @param encryptFilePath encryptFilePath
     * @param targetFilePath  targetFilePath
     * @param privateKey  privateKey
     * @return {@link Flux}<{@link Void}>
     */
    static Flux<Void> decryptBigFile(String encryptFilePath, String targetFilePath, File privateKey){
        return Mono.just(encryptFilePath)
                .flux()
                .doFirst(deleteFile(targetFilePath))
                .flatMap(it -> FileUtil.readLines(it).limitRate(8))
                .buffer(4)
                .flatMapSequential(lines ->
                        Flux.fromIterable(lines)
                                .publishOn(Schedulers.boundedElastic())
                                .map(line -> decrypt2ChunkFileInfo(privateKey, line))
                )
                .doOnNext(it -> log.info("startOffset:{} endOffset:{}", it.getStartOffset(), it.getEndOffset()))
                .publishOn(Schedulers.single())
                .concatMap(chunkFileInfo -> {
                    return Mono.just(chunkFileInfo)
                            .doOnNext(it -> mergeChunkFile(targetFilePath, it))
                            .then();
                });
    }

    @SneakyThrows
    private static void mergeChunkFile(String decryptFilePath, ChunkFileInfo chunkFileInfo) {
        log.info("starting writeChunkFile");
        // Open file for append using "rw"
        try (RandomAccessFile mergedFile = new RandomAccessFile(decryptFilePath, "rw")){
            // Move the file pointer to the starting position
            mergedFile.seek(chunkFileInfo.getStartOffset());
            mergedFile.write(chunkFileInfo.getBytes(), 0, chunkFileInfo.getChunkSize());
        }
    }

    private static ChunkFileInfo decrypt2ChunkFileInfo(File privateKeyFile, String line) {
        String[] split = line.split(":");
        long startOffset = Long.parseLong(split[0]);
        long endOffset = Long.parseLong(split[1]);
        String base64Str = split[2];
        log.info("starting decode");
        byte[] decode = Base64.getDecoder().decode(base64Str);
        byte[] decryptByte = CipherUtil.decryptByEllipticCurveCrypt(decode, privateKeyFile, "EC", "ECIES");

        return new ChunkFileInfo(startOffset, endOffset, (int)(endOffset - startOffset), decryptByte);
    }

    private static Runnable deleteFile(String filePath) {
        return () -> {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    FileUtils.forceDelete(file);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

}

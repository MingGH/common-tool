package run.runnable.commontool.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;



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

    public static final int CHUNK_SIZE = 1024 * 1024 * 4;

    @Test
    void encryptBigFile() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        String filePath = "/Users/asher/Desktop/temp/temp.zip";
        String encryptFilePath = "/Users/asher/Desktop/temp/temp.zip.base64";

        CipherUtil.encryptBigFile(filePath, encryptFilePath, CHUNK_SIZE,
                        CommonUtilTest.getResourceFile("cipher/PublicKeyPath.pem"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe();
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("===============================>" + (endTime-startTime));
        Assertions.assertTrue(true);
    }


    @Test
    void decryptBigFile() throws InterruptedException {
        String encryptFilePath = "/Users/asher/Desktop/temp/temp.zip.base64";
        String decryptFilePath = "/Users/asher/Desktop/temp/temp.zip";

        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //decrypt
        CipherUtil.decryptBigFile(encryptFilePath, decryptFilePath, CommonUtilTest.getResourceFile("cipher/PrivateKey.pem"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe();
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("===============================>" + (endTime-startTime));
        Assertions.assertTrue(true);
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
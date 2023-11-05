package run.runnable.commontool.util;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


/**
 * @author Asher
 * on 2023/11/5
 */
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
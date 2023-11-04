package run.runnable.commontool.util;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Asher
 * on 2023/11/5
 */
public class KeyFileUtil {

    /**
     * 保存公钥
     *
     * @param publicKey 公钥
     * @param filePath
     */
    public static void savePublicKey(PublicKey publicKey, String filePath) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        saveToFile(filePath, x509EncodedKeySpec.getEncoded());
    }

    /**
     * 保存私钥
     *
     * @param privateKey
     * @param filePath   文件路径
     */
    public static void savePrivateKey(PrivateKey privateKey, String filePath) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        saveToFile(filePath, pkcs8EncodedKeySpec.getEncoded());
    }

    @SneakyThrows
    private static void saveToFile(String filePath, byte[] keyBytes) {
        File file = new File(filePath);
        try (FileOutputStream outputStream = new FileOutputStream(file)){
            outputStream.write(keyBytes);
        }
    }
}

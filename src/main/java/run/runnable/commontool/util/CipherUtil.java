package run.runnable.commontool.util;

import lombok.SneakyThrows;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

import static run.runnable.commontool.util.KeyFileUtil.savePrivateKey;
import static run.runnable.commontool.util.KeyFileUtil.savePublicKey;

/**
 * @author Asher
 * on 2023/11/5
 */
public interface CipherUtil {

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
     * @param savePrivateKeyPath 保存私钥路径
     * @param savePublicKeyPath  保存公钥路径
     * @return {@link byte[]}
     */
    @SneakyThrows
    static byte[] encryptByEllipticCurveCrypt(byte[] encryptContent,
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

}

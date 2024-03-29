package com.example.imageproject.config;

import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Configuration
public class AESEncryption {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER_MODE = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "1234567890123456";

    public byte[] encrypt(byte[] data) throws Exception {
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CIPHER_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] encryptedData) throws Exception {
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_CIPHER_MODE);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    public String encryptToBase64(byte[] data) throws Exception {
        byte[] encryptedData = encrypt(data);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public byte[] decryptFromBase64(String base64Data) throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(base64Data);
        return decrypt(encryptedData);
    }

}

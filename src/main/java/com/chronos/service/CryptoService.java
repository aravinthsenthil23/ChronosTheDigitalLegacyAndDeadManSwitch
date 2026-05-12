package com.chronos.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

@Service
public class CryptoService {

    private static SecretKeySpec secretKey;
    private static byte[] key;

    // ==========================================
    // 1. KEY PREPARATION (The "Fixer")
    // ==========================================
    // Turns any string (e.g., "password123") into a valid 16-byte AES Key
    public static void prepareSecreteKey(String myKey) {
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);

            // Hash the key using SHA-1 to get a fixed length
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);

            // AES needs exactly 16 bytes (128 bit)
            key = Arrays.copyOf(key, 16);

            secretKey = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // 2. ENCRYPT DATA
    // ==========================================
    public byte[] encrypt(byte[] contentToEncrypt, String secret) {
        try {
            prepareSecreteKey(secret);

            // Configure Cipher for AES
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Perform Encryption
            return cipher.doFinal(contentToEncrypt);

        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    // ==========================================
    // 3. DECRYPT DATA
    // ==========================================
    public byte[] decrypt(byte[] contentToDecrypt, String secret) {
        try {
            prepareSecreteKey(secret);

            // Configure Cipher for Decryption
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Perform Decryption
            return cipher.doFinal(contentToDecrypt);

        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
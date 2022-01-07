package com.michelkapoko.copper.bitmex.integration.adapter;

import com.michelkapoko.copper.bitmex.integration.exceptions.SecretSignatureException;
import org.springframework.http.HttpMethod;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class  which main functionality is to compute an api signature based on an api secret
 * and using the HmacSHA256 alagorithm.
 */
public class BitmexIntegrationRequestUtil {

    private static final String HMAC_SHA_256 = "HmacSHA256";
    private BitmexIntegrationRequestUtil(){}

    public static String generateSignature(
            String secretKey,
            HttpMethod requestMethod,
            String apiPath,
            Long expiringTime,
            String requestBody)  {

        StringBuilder builder = new StringBuilder();
        builder.append(requestMethod.name());
        builder.append(apiPath);
        builder.append(expiringTime);
        builder.append(requestBody);

        String signature = null;
        try {
            signature = hashAndGetHexStringValue(secretKey, builder.toString(), HMAC_SHA_256);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SecretSignatureException(e.getMessage());
        }
        return signature;
    }

    private static String hashAndGetHexStringValue(String key, String message, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] hash = hash(key, message, algorithm);
        StringBuilder hashVal = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hashVal.append('0');
            }
            hashVal.append(hex);
        }
        return hashVal.toString();
    }

    public static byte[] hash(String key, String message, String algorithm) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, algorithm);

        Mac hmac = Mac.getInstance(algorithm);
        hmac.init(secretKey);
        return hmac.doFinal(messageBytes);
    }

}

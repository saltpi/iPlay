package top.ourfor.app.iplayx.util;

import android.os.Build;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAUtil {
    public static String rsaEncode(byte[] origData, String jRsakey, boolean hex) {
        try {
            String publicKeyPEM = jRsakey
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] publicKeyBytes = new byte[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
            }
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(origData);

            String res = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                res = Base64.getEncoder().encodeToString(encryptedData);
            }

            if (hex) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return bytesToHex(Base64.getDecoder().decode(res));
                }
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
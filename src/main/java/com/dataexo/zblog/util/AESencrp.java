package com.dataexo.zblog.util;

/**
 * Created by wangyi on 9/23/2017.
 */

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

import sun.misc.*;

public class AESencrp {

    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',
                    'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };

    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new Base64().encode(encVal).toString();
        return encryptedValue;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new Base64().decode(encryptedData.getBytes());
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);

        return key;
    }

}

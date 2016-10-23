package pl.osik.autismemotion.helpers;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by User on 2016-10-16.
 */
public class CipherHelper {
    private static CipherHelper instance;
    private static SecretKeySpec key;
    private static IvParameterSpec vector;
    private static Cipher cipher;

    private CipherHelper() {
        try {
            final String keyString = "obtbGPKAcxzMF36I";        //TODO FINALLY zmienić klucz i zrobić GIT Ignore
            final String vectorString = "olP0TsRMFKX1bi0E";
            byte[] keyBytes = keyString.getBytes("UTF-8");
            byte[] ivBytes = vectorString.getBytes("UTF-8");
            key = new SecretKeySpec(keyBytes, "AES");
            vector = new IvParameterSpec(ivBytes);
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            instance = this;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Log.d("CipherHelper", ex.getMessage());
        }
    }

    public static CipherHelper getInstance() {
        if(instance == null) return new CipherHelper();
        return instance;
    }

    public String encrypt(String text) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, vector);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            byte[] encoded = Base64.encode(encrypted, Base64.DEFAULT);
            return new String(encoded);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            Log.d("CipherHelper", ex.getMessage());
        }
        return null;
    }

    public String decrypt(String text) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, vector);
            byte[] decoded = Base64.decode(text, Base64.DEFAULT);
            byte[] out = cipher.doFinal(decoded);
            return new String(out);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            Log.d("CipherHelper", ex.getMessage());
        }
        return null;
    }
}

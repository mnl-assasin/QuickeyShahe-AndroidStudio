package com.bulsu.quickeyshare;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.EZSharedPrefences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by mykelneds on 27/03/2017.
 */

public class EncryptionHelper {

    static String TAG = EncryptionHelper.class.getSimpleName();

    private static String algorithm = "AES";

    public static boolean generateSecretKey(Context ctx) throws NoSuchAlgorithmException {
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();

        if (secretKey != null) {
            String stringKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
            Log.d(TAG, "string key: '" + stringKey + "'");
            EZSharedPrefences.setSecret(ctx, stringKey);
            return true;
        } else
            return false;
    }

    private static SecretKey getSecretKey(Context ctx) {
        String stringKey = EZSharedPrefences.getSecret(ctx);
        Log.d(TAG, "string key: '" + stringKey + "'");
        byte[] encodedKey = Base64.decode(stringKey, Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    public static boolean encryptFile(Context ctx, String encryptedFileName, String origPath) {
        try {

            File fDirectory = new File(Const.DEFAULT_VAULT_PATH);
            if (!fDirectory.exists())
                fDirectory.mkdirs();

            File file = new File(Const.DEFAULT_VAULT_PATH + File.separator + encryptedFileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] encryptedBytes = encryptFile(getSecretKey(ctx), fileToByte(origPath));
            bos.write(encryptedBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static byte[] encryptFile(SecretKey secretKey, byte[] fileData) throws Exception {
        byte[] encrypted = null;
        byte[] data = secretKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
    }

    public static boolean decryptFile(Context ctx, String newFileName, String origPath) {
        try {
            File fDirectory = new File(Const.DEFAULT_VAULT_UNLOCKED_PATH);
            if (!fDirectory.exists())
                fDirectory.mkdirs();

            File file = new File(Const.DEFAULT_VAULT_UNLOCKED_PATH + File.separator + newFileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] encryptedBytes = decodeFile(ctx, origPath);
            bos.write(encryptedBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static byte[] fileToByte(String filePath) {
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {

            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static byte[] decodeFile(Context ctx, String path) {
        return decodeFile(getSecretKey(ctx), fileToByte(path));
    }

    private static byte[] decodeFile(SecretKey secretKey, byte[] fileData) {
        byte[] decrypted = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            decrypted = cipher.doFinal(fileData);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decrypted;
    }


}

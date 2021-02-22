package com.huliganbear.androidbridge.ciphers;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.huliganbear.androidbridge.CryptographyConstants.AES_CIPHER_METHOD;

public class AESCipher {

    public static JSONObject encrypt(byte[] key, String plainText, byte[] iv) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, JSONException, InvalidAlgorithmParameterException {

        JSONObject jsonObject = new JSONObject();
        byte[] cipherText = encrypt(key, plainText.getBytes(), iv);
        jsonObject.put("data", Base64.encodeToString(cipherText, Base64.DEFAULT));
        jsonObject.put("iv", Base64.encodeToString(iv, Base64.DEFAULT));
        return jsonObject;
    }

    public static byte[] encrypt (byte[] key, byte[] plainData, byte[] iv) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_METHOD);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, KeyProperties.KEY_ALGORITHM_AES);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(plainData);
    }

    public static String decrypt(byte[] key, JSONObject data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, JSONException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        byte[] iv = Base64.decode(data.getString("iv"), Base64.DEFAULT);
        byte[] encryptedData = Base64.decode(data.getString("data"), Base64.DEFAULT);
        return decrypt(key, encryptedData, iv);

    }

    public static String decrypt(byte[] key, byte[] data, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_METHOD);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, KeyProperties.KEY_ALGORITHM_AES);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] plainData = cipher.doFinal(data);
        return new String(plainData);
    }
}

package com.huliganbear.androidbridge;

import android.util.Base64;
import android.util.Log;

import com.huliganbear.androidbridge.ciphers.AESCipher;
import com.huliganbear.androidbridge.ciphers.RSACipher;
import com.huliganbear.androidbridge.keygenerators.AESKeyGenerator;
import com.huliganbear.androidbridge.keygenerators.RSAKeyGenerator;

import org.json.JSONObject;

import java.security.KeyPair;

public class Cryptography {
    public String encrypt(String plainText) {
        try {
            byte[] key = AESKeyGenerator.generateKey();
            byte[] iv = AESKeyGenerator.generateIV();
            JSONObject jsonObject = AESCipher.encrypt(key, plainText, iv);
            KeyPair keyPair = RSAKeyGenerator.retrieveKeyPair();
            byte[] encryptedKey = RSACipher.encrypt(keyPair.getPublic(), key);
            jsonObject.put("key", Base64.encodeToString(encryptedKey, Base64.DEFAULT));
            return jsonObject.toString();
        } catch (Exception e) {
            Log.e("TEST123", "encryption failed", e);
            return "";
        }
    }

    public String decrypt(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            KeyPair keyPair = RSAKeyGenerator.retrieveKeyPair();
            byte[] encryptedKey = Base64.decode(jsonObject.getString("key"), Base64.DEFAULT);
            byte[] key = RSACipher.decrypt(keyPair.getPrivate(), encryptedKey);
            return AESCipher.decrypt(key, jsonObject);
        } catch (Exception e) {
            Log.e("TEST123", "decryption failed", e);
            return "";
        }
    }
}

package com.huliganbear.androidbridge.keygenerators;

import android.security.keystore.KeyProperties;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

public class AESKeyGenerator {

    public static byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES);
        keygen.init(128);
        return keygen.generateKey().getEncoded();
    }

    public static byte[] generateIV() {
        final byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
}

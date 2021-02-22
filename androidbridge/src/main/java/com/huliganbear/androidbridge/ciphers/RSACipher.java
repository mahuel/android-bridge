package com.huliganbear.androidbridge.ciphers;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import static com.huliganbear.androidbridge.CryptographyConstants.RSA_CIPHER_METHOD;

public class RSACipher {
    public static byte[] encrypt(PublicKey key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(RSA_CIPHER_METHOD);

        // This parameter spec needs to be added
        OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.ENCRYPT_MODE, key, oaepParameterSpec);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(PrivateKey key, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(RSA_CIPHER_METHOD);

        // This parameter spec needs to be added
        OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.DECRYPT_MODE, key, oaepParameterSpec);
        return cipher.doFinal(encrypted);
    }
}

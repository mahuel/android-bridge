package com.huliganbear.androidbridge.callbacks;

public interface CryptographyCallback {

    void encryptionSuccessful(String encryptedData);
    void encryptionUnsuccessful(String plainText);

    void decryptionSuccessful(String plainText);
    void decryptionUnsuccessful();
}

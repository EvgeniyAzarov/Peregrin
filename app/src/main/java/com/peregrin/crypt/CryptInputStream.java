package com.peregrin.crypt;

import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CryptInputStream extends ObjectInputStream {
    public CryptInputStream(InputStream in) throws IOException {
        super(in);
    }

    public String readString() throws IOException,
            ClassNotFoundException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        String encrypted = (String) super.readObject();

        final byte[] keyBytes = "ZZHHYYTTUUHHGGRR".getBytes();
        final byte[] ivBytes = "AAACCCDDDYYUURRS".getBytes();

        final byte[] encryptedBytes = Base64.decode(encrypted, Base64.DEFAULT);

        //Инициализация и задание параметров расшифровки

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

        //Расшифровка

        final byte[] resultBytes = cipher.doFinal(encryptedBytes);

        return new String(resultBytes);
    }
}

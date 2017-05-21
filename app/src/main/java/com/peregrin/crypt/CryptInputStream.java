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
//        super(in);
    }

    public String readString() throws IOException,
            ClassNotFoundException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        //String encrypted = (String) super.readObject();
        String encrypted =
                "p+oJjsGEULNSptP5Sj1BM5w65hMjkqzahORd8ybIkqyJD0V/608c1tYuKIvDLUIa\n" +
                "RQ9jQ6+EwbyMFjlMa6xuEnxOx4sez001hd3NsLO7p00XoTqAvi9zwUBII+\n" +
                "nPphP6Zr0P4icvODpmhlmRILgSBsUf1H/3VN1lNXjo4LTa\n" +
                "GxLqW3VSg9iV9yFq4VMWqsRF";

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

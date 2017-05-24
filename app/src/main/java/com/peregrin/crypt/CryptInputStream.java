package com.peregrin.crypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;


public class CryptInputStream extends ObjectInputStream {
    public CryptInputStream(InputStream in) throws IOException {
        super(in);
    }

    public String readString() throws
            IOException,
            ClassNotFoundException {

        String encrypted = (String) super.readObject();


        return new String("0");
    }
}

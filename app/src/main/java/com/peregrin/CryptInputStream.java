package com.peregrin;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;


class CryptInputStream extends ObjectInputStream {
    public CryptInputStream(InputStream in) throws IOException {
        super(in);
    }

    public String readString() throws IOException, ClassNotFoundException {
        String buf = (String) super.readObject();

        //TODO decrypt string

        return buf;
    }
}

package com.peregrin;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


class CryptOutputStream extends ObjectOutputStream {
    public CryptOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    public void writeString(String str) throws IOException {
        //TODO encrypt string

        super.writeObject(str);
    }
}

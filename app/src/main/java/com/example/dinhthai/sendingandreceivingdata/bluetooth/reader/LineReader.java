package com.example.dinhthai.sendingandreceivingdata.bluetooth.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LineReader extends com.example.dinhthai.sendingandreceivingdata.bluetooth.reader.SocketReader {
    private BufferedReader reader;

    public LineReader(InputStream inputStream) {
        super(inputStream);
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public byte[] read() throws IOException {
        return reader.readLine().getBytes();
    }
}
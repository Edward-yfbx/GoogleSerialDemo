package com.google.serial.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.serial.R;
import com.google.serial.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    private void test() throws IOException {
        SerialPort serialPort = new SerialPort(new File("/dev/ttyGS0"), 9600, 0);
        InputStream inputStream = serialPort.getInputStream();
        byte[] buffer = new byte[1024];
        int read = inputStream.read(buffer);
        inputStream.close();

        OutputStream outputStream = serialPort.getOutputStream();
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close();

        serialPort.close();

    }
}

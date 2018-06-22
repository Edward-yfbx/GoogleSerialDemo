package com.google.serial.mydemo;

import android.os.Bundle;
import android.serialport.SerialPort;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.serial.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView retTxt;
    SerialPort serialPort;
    OutputStream outputStream;
    InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSerialPort();
        editText = findViewById(R.id.edit_txt);
        retTxt = findViewById(R.id.ret_txt);
        retTxt.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execute(editText.getText().toString());
            }
        });
    }

    /**
     * 初始化
     */
    private void initSerialPort() {
        try {
            serialPort = new SerialPort(new File("/dev/ttyS4"), 115200, 0);
            outputStream = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行请求
     */
    public void execute(final String msg) {

        new Thread() {

            @Override
            public void run() {
                try {
                    test(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void test(String msg) throws IOException, InterruptedException {
        msg = "1502000100082B18";
        byte[] data = HexUtils.hexStr2BinArr(msg);
        outputStream.write(data);

        byte[] buffer = new byte[64];
        int len = inputStream.read(buffer);
        String ret = HexUtils.bin2HexStr(buffer, len);
        setText(ret);
        Log.i("返回结果", "test: " + ret);

    }

    private void setText(final String ret) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                retTxt.append(ret);
                retTxt.append("\r\n");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        serialPort.close();
    }
}

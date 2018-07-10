package cn.yfbx.demo;

import android.app.Activity;
import android.os.Bundle;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.serial.R;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements View.OnKeyListener, View.OnClickListener {

    private static final String TAG = "串口测试";
    Spinner portSpinner;
    Spinner baudSpinner;
    EditText editText;
    CheckBox hexBtn;
    TextView retTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSpinner();
    }

    private void initView() {
        portSpinner = findViewById(R.id.spinner1);
        baudSpinner = findViewById(R.id.spinner2);
        editText = findViewById(R.id.edit_txt);
        hexBtn = findViewById(R.id.hexBtn);
        retTxt = findViewById(R.id.ret_txt);
        findViewById(R.id.send_btn).setOnClickListener(this);
        findViewById(R.id.clear_btn).setOnClickListener(this);
        editText.setOnKeyListener(this);
        retTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initSpinner() {
        SerialPortFinder finder = new SerialPortFinder();
        String[] path = finder.getAllDevicesPath();
        List<String> list = Arrays.asList(path);
        String[] array = getResources().getStringArray(R.array.baudrates_value);
        List<String> baudList = Arrays.asList(array);

        portSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));
        baudSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, baudList));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                sendMsg(editText.getText().toString());
                break;
            case R.id.clear_btn:
                retTxt.setText("");
                break;
        }
    }


    private void sendMsg(final String msg) {
        new Thread() {
            @Override
            public void run() {
                String dev = portSpinner.getSelectedItem().toString();
                int baudRate = Integer.parseInt(baudSpinner.getSelectedItem().toString());
                Log.i(TAG, "串口：" + dev + "，波特率：" + baudRate);
                byte[] data = hexBtn.isChecked() ? HexUtils.hexToByte(msg) : msg.getBytes();
                byte[] bytes = serialIO(dev, baudRate, data);
                String ret = hexBtn.isChecked() ? HexUtils.byteToHex(bytes) : new String(bytes);
                onReadResult(ret);
            }
        }.start();
    }

    private void onReadResult(final String result) {
        if (result == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                retTxt.append(result);
                retTxt.append("\n");
            }
        });
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            sendMsg(editText.getText().toString());
            return true;
        }
        return false;
    }


    /**
     * 串口通信
     */
    private byte[] serialIO(String dev, int baudRate, byte[] data) {
        try {
            SerialPort serialPort = new SerialPort(new File(dev), baudRate, 0);
            serialPort.getOutputStream().write(data);
            byte[] buffer = new byte[512];
            int len = serialPort.getInputStream().read(buffer);
            serialPort.close();
            return Arrays.copyOf(buffer, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

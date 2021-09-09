package com.dongnao.serialprotdongnao;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ProtDataInterface, SerialPortManager.OnOpenFailed {

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final int REQUEST_PERMISSION_CODE = 1;
    EditText edit_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_content = findViewById(R.id.edit_content);

    }
    public void requestPermision() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                return;
            }
            else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                return;
            }
        }
                SerialPortManager.getInstance().openSerialPort("/dev/ttySAC2", 115200);
//        SerialPortManager.getInstance().openSerialPort("/dev/ttyHS0", 115200);
//        SerialPortManager.getInstance().openSerialPort("/dev/tty", 115200);
//        SerialPortManager.getInstance().openSerialPort("/dev/urandom", 115200);
//        SerialPortManager.getInstance().openSerialPort("/dev/zero", 115200);
//        SerialPortManager.getInstance().openSerialPort("/dev/stderr", 115200);
//        SerialPortManager.getInstance().openSerialPort("/dev/ttyS0", 115200);
//        SerialPortManager.getInstance().openSerialPort("/dev/ttyS1", 115200);
        SerialPortManager.getInstance().regist(this);
        SerialPortManager.getInstance().setOnOpenFailed(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean granted=true;
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" +
                        grantResults[i]);
                if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                    granted=false;
                    break;
                }
            }
            if(granted){
                open(edit_content);
            }
        }
    }
//dev/ttsy
    public void open(View view) {
        requestPermision();
    }

    public void send(View view) {

        String command = edit_content.getText().toString().trim();

        if (TextUtils.isEmpty(command)) {
            return;
        }
        byte[] sendContentBytes = command.getBytes();
        SerialPortManager.getInstance().putCommand(sendContentBytes);
    }

    @Override
    public void onDataReceived(byte[] bytes) {
        Log.i("david", "   收到信息   " + new String(bytes));
    }

    @Override
    public void failed() {
        Toast.makeText(this,"打开串口失败",Toast.LENGTH_SHORT).show();
    }
}

package com.dongnao.serialprotdongnao;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class SerialPortManager {

    static {
        System.loadLibrary("native-lib");
    }

    public List<ProtDataInterface> observable;

    private LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    //    读取流   从底层读取数据
    private FileInputStream mFileInputStream;
    Executor executor = Executors.newSingleThreadExecutor();

//    写入流  传递信息
    private FileOutputStream mFileOutputStream;
//
//    public void write (byte[] command) {
////        写入
//        try {
//            mFileOutputStream.write(command);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//    核心  用户的操作 开始  -----》底层 指令码  发送出去
//array查询    linke   插入
    public void putCommand(byte[] command) {
        try {
            queue.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //让这些过程运作起来
    private Runnable taskCenterRunnable=new Runnable() {
        @Override
        public void run() {
            while(true){

                byte[] content=null;
                try {
                    content=queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(content!=null){
                    try {
                        mFileOutputStream.write(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };



    private static final SerialPortManager ourInstance = new SerialPortManager();

    public static SerialPortManager getInstance() {
        return ourInstance;
    }




    private SerialPortManager() {
        observable = new ArrayList<>();

    }

//    一定避免并发写   00  02  01    开门    00  03 03  关门
//    00  00  02  01 03 03







//观察者模式----------------------------------------->
    public void regist(ProtDataInterface protDataInterface) {
        if (protDataInterface != null) {
            observable.add(protDataInterface);
        }
    }
    public void unregist (ProtDataInterface protDataInterface) {
        if (protDataInterface != null) {
            observable.remove(protDataInterface);
        }
    }

    public void update(byte[] content) {
        for (ProtDataInterface protDataInterface : observable) {
            protDataInterface.onDataReceived(content);
        }
    }

    public void openSerialPort(String path, int baudRate) {

        File file = new File(path);
//        获取root权限
        Utils.chmod777(file);

        FileDescriptor mFd=open(path, baudRate);
        if(mFd==null){
            if(onOpenFailed!=null){
                onOpenFailed.failed();
            }
            return;
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);


        if (mFd != null) {
            startReadThread();
            executor.execute(taskCenterRunnable);
        }

    }
    OnOpenFailed onOpenFailed;

    public void setOnOpenFailed(OnOpenFailed onOpenFailed) {
        this.onOpenFailed = onOpenFailed;
    }

    interface OnOpenFailed{
        void failed();
    }

    private void startReadThread() {
        SerialPortReadThread serialPortReadThread = new SerialPortReadThread(mFileInputStream) {
            @Override
            public void onDataReceived(byte[] readBytes) {
//              协议     最重要  不断会得到数据
//                帧   h264关键帧的第一
//                线程进行处理
//                业务的分发
                switch (ActivityStackState.getActivityType()) {
                    case 1:

                        break;
                }
                Log.i("david", "onDataReceived: "+bytesToHex(readBytes));
                update(readBytes);

//                if(activityType){
////                        解析数据（16进制） ------javabean  ----接口<T>回调出去
//
//
//
//
//                }



//                处理  ----》 业务逻辑     协议

            }
        };
        serialPortReadThread.start();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    public native FileDescriptor open(String path, int baudRate);
}

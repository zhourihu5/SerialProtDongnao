package com.dongnao.serialprotdongnao;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public abstract class SerialPortReadThread extends Thread {
    private InputStream mInputStream;

    private byte[] mReadBuffer;

    public SerialPortReadThread(InputStream mInputStream) {
        this.mInputStream = mInputStream;
        this.mReadBuffer = new byte[1024];
    }

    public boolean isInterrupted=false;

    public void setInterrupted(boolean interrupted) {
        isInterrupted = interrupted;
    }

    @Override
    public void run() {

        while (!isInterrupted) {
            try {
                //  redids


                int size = mInputStream.read(mReadBuffer);

                if (-1 == size || 0 >= size) {
                    return;
                }
                byte[] readBytes = new byte[size];
                System.arraycopy(mReadBuffer, 0, readBytes, 0, size);
                onDataReceived(readBytes);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }



    }



    public abstract void onDataReceived(byte[] readBytes) ;
}

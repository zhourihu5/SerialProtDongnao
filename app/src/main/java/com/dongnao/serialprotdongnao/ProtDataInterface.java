package com.dongnao.serialprotdongnao;

public interface ProtDataInterface {
    /**
     * 数据接收
     *
     * @param bytes 接收到的数据
     */
    void onDataReceived(byte[] bytes);
}

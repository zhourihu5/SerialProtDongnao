package com.dongnao.serialprotdongnao;

import java.io.File;
import java.io.IOException;

public class Utils {

   public static boolean chmod777(File file) {
        if (null == file || !file.exists()) {
            // 文件不存在
            return false;
        }
        try {
            // 获取ROOT权限
            Process su = Runtime.getRuntime().exec("/system/bin/su");
            // 修改文件属性为 [可读 可写 可执行]
            String cmd = "chmod 777 " + file.getAbsolutePath() + "\n" + "exit\n";
            su.getOutputStream().write(cmd.getBytes());
            if (0 == su.waitFor() && file.canRead() && file.canWrite() && file.canExecute()) {
                return true;
            }
        } catch (IOException | InterruptedException e) {
            // 没有ROOT权限
            e.printStackTrace();
        }
        return false;
    }
}

package edu.wkd.towave.memorycleaner.tools;

import android.app.ActivityManager;
import android.content.Context;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import android.app.ActivityManager.MemoryInfo;
/**
 * Created by towave on 2016/4/8.
 */
public class MemoryUsedMessage {
    private static long sum;
    private static long available;
    private static float percent;

    public MemoryUsedMessage() {
        // TODO Auto-generated constructor stub
    }

    // 获取可用运存大小
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        // return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        available = mi.availMem / (1024 * 1024);
        return available;
    }

    // 获取总运存大小
    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
        }
        // return Formatter.formatFileSize(context, initial_memory);//
        // Byte转换为KB或者MB，内存大小规格化
        sum = initial_memory / (1024 * 1024);
        return sum;
    }

    public static float getPercent(Context context) {
        if (sum == 0)
            sum = MemoryUsedMessage.getTotalMemory();
        available = MemoryUsedMessage.getAvailMemory(context);
        float tmp_double = (float) (1.0 * (sum - available) / sum * 100);
        BigDecimal bigDecimal = new BigDecimal(tmp_double);
        percent = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return percent;
    }
}

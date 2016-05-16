package edu.wkd.towave.memorycleaner.tools;

import android.app.ActivityManager;
import android.content.Context;
import java.io.BufferedReader;
import java.io.FileReader;
import android.app.ActivityManager.MemoryInfo;
import java.math.BigDecimal;

/**
 * Created by towave on 2016/4/8.
 */
public class AppUtils {

    /**
     * 描述：获取可用内存.
     */
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager activityManager
                = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 当前系统可用内存 ,将获得的内存大小规格化
        return memoryInfo.availMem;
    }


    /**
     * 描述：总内存.
     */
    public static long getTotalMemory() {
        // 系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader,
                    8192);
            // 读取meminfo第一行，系统内存大小
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            // 获得系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Byte转位KB或MB
        return memory * 1024;
    }


    public static float getPercent(Context context) {
        long l = getAvailMemory(context);
        long y = getTotalMemory();
        final double x = (((y - l) / (double) y) * 100);
        return new BigDecimal(x).setScale(2, BigDecimal.ROUND_HALF_UP)
                                .floatValue();
    }
}

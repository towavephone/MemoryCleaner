package edu.wkd.towave.memorycleaner.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;
import edu.wkd.towave.memorycleaner.R;
import edu.wkd.towave.memorycleaner.model.AppProcessInfo;
import edu.wkd.towave.memorycleaner.model.Ignore;
import edu.wkd.towave.memorycleaner.tools.L;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalDb;

public class CoreService extends Service {

    public static final String ACTION_CLEAN_AND_EXIT
            = "edu.wkd.towave.service.cleaner.CLEAN_AND_EXIT";

    private static final String TAG = "CleanerService";

    private OnProcessActionListener mOnActionListener;
    private boolean mIsScanning = false;
    private boolean mIsCleaning = false;
    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    PackageManager packageManager = null;
    Context mContext;
    //private FinalDb mFinalDb;

    public interface OnProcessActionListener {
        void onScanStarted(Context context);

        void onScanProgressUpdated(Context context, int current, int max, long memory, String processName);

        void onScanCompleted(Context context, List<AppProcessInfo> apps);

        void onCleanStarted(Context context);

        void onCleanCompleted(Context context, long cacheSize);
    }

    public class ProcessServiceBinder extends Binder {

        public CoreService getService() {
            return CoreService.this;
        }
    }

    private ProcessServiceBinder mBinder = new ProcessServiceBinder();


    @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override public void onCreate() {
        mContext = getApplicationContext();

        try {
            activityManager = (ActivityManager) getSystemService(
                    Context.ACTIVITY_SERVICE);
            packageManager = mContext.getPackageManager();
        } catch (Exception e) {

        }
    }


    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action != null) {
            if (action.equals(ACTION_CLEAN_AND_EXIT)) {
                setOnActionListener(new OnProcessActionListener() {
                    @Override public void onScanStarted(Context context) {

                    }


                    @Override
                    public void onScanProgressUpdated(Context context, int current, int max, long memory, String processName) {

                    }


                    @Override
                    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {
                        //   if (getCacheSize() > 0) {
                        //     cleanCache();
                        // }
                    }


                    @Override public void onCleanStarted(Context context) {

                    }


                    @Override
                    public void onCleanCompleted(Context context, long cacheSize) {
                        String msg = getString(R.string.cleaned,
                                Formatter.formatShortFileSize(CoreService.this,
                                        cacheSize));

                        Log.d(TAG, msg);

                        Toast.makeText(CoreService.this, msg, Toast.LENGTH_LONG)
                             .show();

                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                stopSelf();
                            }
                        }, 5000);
                    }
                });

                scanRunProcess();
            }
        }

        return START_NOT_STICKY;
    }


    private class TaskScan
            extends AsyncTask<Void, Object, List<AppProcessInfo>> {

        private int mAppCount = 0;

        private long mAppMemory = 0;

        private FinalDb mFinalDb = FinalDb.create(mContext);
        //public TaskScan(FinalDb finalDb) {
        //    this.mFinalDb = finalDb;
        //}


        @Override protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onScanStarted(CoreService.this);
            }
        }


        @Override
        protected List<AppProcessInfo> doInBackground(Void... params) {
            list = new ArrayList<>();
            ApplicationInfo appInfo = null;
            AppProcessInfo abAppProcessInfo = null;
            //得到所有正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> appProcessList
                    = activityManager.getRunningAppProcesses();
            publishProgress(0, appProcessList.size(), 0, "开始扫描");

            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
                abAppProcessInfo = new AppProcessInfo(
                        appProcessInfo.processName, appProcessInfo.pid,
                        appProcessInfo.uid);
                String packName = appProcessInfo.processName;
                try {
                    appInfo = packageManager.getApplicationInfo(
                            appProcessInfo.processName, 0);

                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        abAppProcessInfo.isSystem = true;
                    }
                    else {
                        abAppProcessInfo.isSystem = false;
                    }
                    Drawable icon = appInfo.loadIcon(packageManager);
                    String appName = appInfo.loadLabel(packageManager)
                                            .toString();
                    abAppProcessInfo.icon = icon;
                    abAppProcessInfo.appName = appName;
                    //abAppProcessInfo.packName = packName;
                } catch (PackageManager.NameNotFoundException e) {
                    abAppProcessInfo.icon = mContext.getResources()
                                                    .getDrawable(
                                                            R.mipmap.ic_launcher);
                    //String packName = appProcessInfo.processName;
                    appInfo = getApplicationInfo(
                            appProcessInfo.processName.split(":")[0]);
                    if (appInfo != null) {
                        Drawable icon = appInfo.loadIcon(packageManager);
                        abAppProcessInfo.icon = icon;
                        packName = appProcessInfo.processName.split(":")[0];
                    }
                    abAppProcessInfo.isSystem = true;
                    abAppProcessInfo.appName = appProcessInfo.processName;
                    //abAppProcessInfo.packName = packName;
                }
                abAppProcessInfo.packName = packName;
                long memory = activityManager.getProcessMemoryInfo(new int[] {
                        appProcessInfo.pid })[0].getTotalPrivateDirty() * 1024;
                abAppProcessInfo.memory = memory;

                List<Ignore> ignores = mFinalDb.findAllByWhere(Ignore.class,
                        "packName='" + abAppProcessInfo.packName + "'");
                // List<Ignore> ignores = mFinalDb.findAll(Ignore.class);
                if (ignores.size() == 0) {
                    list.add(abAppProcessInfo);
                    mAppMemory += memory;
                    publishProgress(++mAppCount, appProcessList.size(),
                            mAppMemory, abAppProcessInfo.processName);
                }
            }

            return list;
        }


        @Override protected void onProgressUpdate(Object... values) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanProgressUpdated(CoreService.this,
                        Integer.parseInt(values[0] + ""),
                        Integer.parseInt(values[1] + ""),
                        Long.parseLong(values[2] + ""), values[3] + "");
            }
        }


        @Override protected void onPostExecute(List<AppProcessInfo> result) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanCompleted(CoreService.this, result);
            }

            mIsScanning = false;
        }
    }


    public void scanRunProcess() {
        // mIsScanning = true;
        //mFinalDb = finalDb;
        new TaskScan().execute();
    }


    public void killBackgroundProcesses(String processName) {
        // mIsScanning = true;

        String packageName = null;
        try {
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            }
            else {
                packageName = processName.split(":")[0];
            }

            activityManager.killBackgroundProcesses(packageName);

            //app使用FORCE_STOP_PACKAGES权限，app必须和这个权限的声明者的签名保持一致！
            Method forceStopPackage = activityManager.getClass()
                                                     .getDeclaredMethod(
                                                             "forceStopPackage",
                                                             String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class TaskClean extends AsyncTask<Void, Void, Long> {

        private FinalDb mFinalDb = FinalDb.create(mContext);


        @Override protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanStarted(CoreService.this);
            }
        }


        @Override protected Long doInBackground(Void... params) {
            long beforeMemory = 0;
            long endMemory = 0;
            ActivityManager.MemoryInfo memoryInfo
                    = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            beforeMemory = memoryInfo.availMem;
            List<ActivityManager.RunningAppProcessInfo> appProcessList
                    = activityManager.getRunningAppProcesses();
            ApplicationInfo appInfo = null;
            for (ActivityManager.RunningAppProcessInfo info : appProcessList) {
                String packName = info.processName;
                try {
                    packageManager.getApplicationInfo(info.processName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    appInfo = getApplicationInfo(
                            info.processName.split(":")[0]);
                    if (appInfo != null) {
                        packName = info.processName.split(":")[0];
                    }
                }
                List<Ignore> ignores = mFinalDb.findAllByWhere(Ignore.class,
                        "packName='" + packName + "'");
                if (ignores.size() == 0) {
                    L.e(info.processName);
                    killBackgroundProcesses(info.processName);
                }
            }
            activityManager.getMemoryInfo(memoryInfo);
            endMemory = memoryInfo.availMem;
            return endMemory - beforeMemory;
        }


        @Override protected void onPostExecute(Long result) {

            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(CoreService.this, result);
            }
        }
    }


    public long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager.MemoryInfo memoryInfo
                = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 当前系统可用内存 ,将获得的内存大小规格化

        return memoryInfo.availMem;
    }


    public void cleanAllProcess() {
        //  mIsCleaning = true;

        new TaskClean().execute();
    }


    public void setOnActionListener(OnProcessActionListener listener) {
        mOnActionListener = listener;
    }


    public ApplicationInfo getApplicationInfo(String processName) {
        if (processName == null) {
            return null;
        }
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }


    public boolean isScanning() {
        return mIsScanning;
    }


    public boolean isCleaning() {
        return mIsCleaning;
    }
}

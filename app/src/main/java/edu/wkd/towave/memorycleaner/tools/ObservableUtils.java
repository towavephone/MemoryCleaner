package edu.wkd.towave.memorycleaner.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import edu.wkd.towave.memorycleaner.model.AppInfo;
import edu.wkd.towave.memorycleaner.model.AutoStartInfo;
import edu.wkd.towave.memorycleaner.model.Ignore;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;
import net.tsz.afinal.FinalDb;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by towave on 2016/5/14.
 */
public class ObservableUtils {
    @Inject public ObservableUtils() {}


    public Observable<Boolean> disableApps(Context context, ArrayList<AutoStartInfo> autoStartInfos) {
        return create(new disableAppsFun(context, autoStartInfos));
    }


    public Observable<Boolean> enableApp(Context context, AutoStartInfo autoStartInfo) {
        return create(new enableAppFun(context, autoStartInfo));
    }


    public Observable<List<Ignore>> getIgnoreApps(FinalDb finalDb) {
        return create(new getIgnoreAppsFun(finalDb));
    }


    private <T> Observable<T> create(Fun<T> fun) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(Subscriber<? super T> subscriber) {
                try {
                    T t = fun.call();
                    subscriber.onNext(t);
                    //subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }


    private class disableAppsFun implements Fun<Boolean> {

        Context mContext;
        ArrayList<AutoStartInfo> mAutoStartInfos;


        public disableAppsFun(Context context, ArrayList<AutoStartInfo> autoStartInfos) {
            this.mContext = context;
            this.mAutoStartInfos = autoStartInfos;
        }


        @Override public Boolean call() throws Exception {
            RootUtil.preparezlsu(mContext);
            List<String> mSring = new ArrayList<>();
            for (AutoStartInfo auto : mAutoStartInfos) {
                if (auto.isEnable()) {
                    String packageReceiverList[] = auto.getPackageReceiver()
                                                       .toString()
                                                       .split(";");
                    for (int j = 0; j < packageReceiverList.length; j++) {
                        String cmd = "pm disable " + packageReceiverList[j];
                        //部分receiver包含$符号，需要做进一步处理，用"$"替换掉$
                        cmd = cmd.replace("$", "\"" + "$" + "\"");
                        //执行命令
                        mSring.add(cmd);
                    }
                }
            }
            ShellUtils.CommandResult mCommandResult = ShellUtils.execCommand(
                    mSring, true, true);
            return mCommandResult.result == 0;
        }
    }

    private class enableAppFun implements Fun<Boolean> {

        Context mContext;
        AutoStartInfo mAutoStartInfo;


        public enableAppFun(Context context, AutoStartInfo autoStartInfo) {
            this.mContext = context;
            this.mAutoStartInfo = autoStartInfo;
        }


        @Override public Boolean call() throws Exception {
            RootUtil.preparezlsu(mContext);
            List<String> mSring = new ArrayList<>();

            String enable = !mAutoStartInfo.isEnable() ? "enable" : "disable";
            String packageReceiverList[] = mAutoStartInfo.getPackageReceiver()
                                                         .toString()
                                                         .split(";");
            for (int j = 0; j < packageReceiverList.length; j++) {
                String cmd = "pm " + enable + " " + packageReceiverList[j];
                //部分receiver包含$符号，需要做进一步处理，用"$"替换掉$
                cmd = cmd.replace("$", "\"" + "$" + "\"");
                //执行命令
                mSring.add(cmd);
            }

            ShellUtils.CommandResult mCommandResult = ShellUtils.execCommand(
                    mSring, true, true);
            return mCommandResult.result == 0;
        }
    }

    private class getIgnoreAppsFun implements Fun<List<Ignore>> {

        FinalDb mFinalDb;


        public getIgnoreAppsFun(FinalDb finalDb) {
            this.mFinalDb = finalDb;
        }


        @Override public List<Ignore> call() throws Exception {
            return mFinalDb.findAll(Ignore.class);
        }
    }

    public interface Fun<T> {
        T call() throws Exception;
    }
}

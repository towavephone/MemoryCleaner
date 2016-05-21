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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by towave on 2016/5/14.
 */
public class ObservableUtils {
    @Inject public ObservableUtils() {}


    public Observable<Map<String, Object>> getAllApps(Context context) {
        return create(new getAllAppsFun(context));
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


    private class getAllAppsFun implements Fun<Map<String, Object>> {

        Context mContext;

        public getAllAppsFun(Context context) {
            this.mContext = context;
        }


        @Override public Map<String, Object> call() throws Exception {
            return null;
        }
    }


    public interface Fun<T> {
        T call() throws Exception;
    }
}

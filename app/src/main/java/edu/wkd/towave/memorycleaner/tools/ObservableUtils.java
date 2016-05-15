package edu.wkd.towave.memorycleaner.tools;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by towave on 2016/5/14.
 */
public class ObservableUtils {
    @Inject public ObservableUtils() {}


    public Observable<Map<String,Object>> getMemoryStatus(Context context) {
        return create(new getMemoryStatusFun(context));
    }


    private <T> Observable<T> create(Fun<T> fun) {
        return Observable.create(new Observable
                .OnSubscribe<T>
                () {
            @Override public void call(Subscriber<? super T> subscriber) {
                try {
                    T t = fun.call();
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }


    private class getMemoryStatusFun implements Fun<Map<String, Object>> {

        Context mContext;


        public getMemoryStatusFun(Context context) {
            this.mContext = context;
        }


        @Override public Map<String, Object> call() throws Exception {
            HashMap map = new HashMap();
            map.put("percent", MemoryUsedMessage.getPercent(mContext));
            map.put("available", MemoryUsedMessage.getAvailMemory(mContext));
            map.put("sum", MemoryUsedMessage.getTotalMemory());
            return map;
        }
    }

    public interface Fun<T> {
        T call() throws Exception;
    }
}

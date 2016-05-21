package edu.wkd.towave.memorycleaner;

import android.app.Application;
import android.content.Context;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import edu.wkd.towave.memorycleaner.injector.component.AppComponent;
import edu.wkd.towave.memorycleaner.injector.component.DaggerAppComponent;
import edu.wkd.towave.memorycleaner.injector.module.AppModule;

/**
 * Created by Administrator on 2016/5/4.
 */
public class App extends Application {

    private AppComponent mAppComponent;


    @Override public void onCreate() {
        super.onCreate();
        initializeInjector();
        initializeStetho();
        initializeLeakCanary();
    }


    private RefWatcher refWatcher;


    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }


    @Override public void onTerminate() {
        super.onTerminate();
    }


    @Override public void onLowMemory() {
        super.onLowMemory();
    }


    public void initializeLeakCanary() {
        refWatcher = LeakCanary.install(this);
    }


    private void initializeInjector() {
        mAppComponent = DaggerAppComponent.builder()
                                          .appModule(new AppModule(this))
                                          .build();
    }


    private void initializeStetho() {
        Stetho.initializeWithDefaults(this);
    }


    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}

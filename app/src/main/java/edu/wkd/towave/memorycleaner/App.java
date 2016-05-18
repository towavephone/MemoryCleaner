package edu.wkd.towave.memorycleaner;

import android.app.Application;
import com.facebook.stetho.Stetho;
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
    }


    @Override public void onTerminate() {
        super.onTerminate();
    }


    @Override public void onLowMemory() {
        super.onLowMemory();
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

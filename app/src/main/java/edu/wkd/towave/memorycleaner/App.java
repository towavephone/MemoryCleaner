package edu.wkd.towave.memorycleaner;

import android.app.Application;
import edu.wkd.towave.memorycleaner.injector.component.AppComponent;
import edu.wkd.towave.memorycleaner.injector.component.DaggerAppComponent;
import edu.wkd.towave.memorycleaner.injector.module.AppModule;

/**
 * Created by Administrator on 2016/5/4.
 */
public class App extends Application{

    private AppComponent mAppComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private void initializeInjector() {
        mAppComponent = DaggerAppComponent.builder()
                                          .appModule(new AppModule(this))
                                          .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}

package edu.wkd.towave.memorycleaner.injector.component;

import android.content.Context;
import dagger.Component;
import edu.wkd.towave.memorycleaner.injector.Activity;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.ui.activity.AppManage;
import edu.wkd.towave.memorycleaner.ui.activity.AutoStartManage;
import edu.wkd.towave.memorycleaner.ui.activity.MainActivity;
import edu.wkd.towave.memorycleaner.ui.activity.MemoryClean;
import edu.wkd.towave.memorycleaner.ui.activity.RubbishClean;
import edu.wkd.towave.memorycleaner.ui.activity.SettingActivity;

@Activity @Component(dependencies = AppComponent.class,
                     modules = { ActivityModule.class })
public interface ActivityComponent {
    void inject(MainActivity activity);

    void inject(SettingActivity activity);

    void inject(AppManage activity);

    void inject(MemoryClean memoryClean);

    void inject(RubbishClean rubbishClean);

    void inject(AutoStartManage autoStartManage);

    android.app.Activity activity();

    @ContextLifeCycle("Activity") Context activityContext();

    @ContextLifeCycle("App") Context appContext();
}

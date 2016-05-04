package edu.wkd.towave.memorycleaner.injector.component;

import android.content.Context;
import dagger.Subcomponent;
import edu.wkd.towave.memorycleaner.injector.Activity;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.injector.module.ActivityModule;
import edu.wkd.towave.memorycleaner.ui.activity.MainActivity;
import edu.wkd.towave.memorycleaner.ui.activity.AppManage;

@Activity @Subcomponent(modules = { ActivityModule.class })
public interface ActivityComponent {
    void inject(MainActivity activity);

    void inject(AppManage activity);

    android.app.Activity activity();

    @ContextLifeCycle("Activity") Context activityContext();

    @ContextLifeCycle("App") Context appContext();
}

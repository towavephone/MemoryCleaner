package edu.wkd.towave.memorycleaner.injector.module;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import edu.wkd.towave.memorycleaner.injector.Activity;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;

@Module public class ActivityModule {
    private final android.app.Activity activity;


    public ActivityModule(android.app.Activity activity) {
        this.activity = activity;
    }


    @Provides @Activity android.app.Activity provideActivity() {
        return activity;
    }


    @Provides @Activity @ContextLifeCycle("Activity") Context provideContext() {
        return activity;
    }
}

package edu.wkd.towave.memorycleaner.injector.module;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import javax.inject.Singleton;

/**
 * Created by Administrator on 2016/5/4.
 */
@Module public class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides @Singleton
    App provideApplication() {
        return app;
    }

    @Provides @Singleton @ContextLifeCycle("App")
    Context provideActivityContext() {
        return app.getApplicationContext();
    }

}

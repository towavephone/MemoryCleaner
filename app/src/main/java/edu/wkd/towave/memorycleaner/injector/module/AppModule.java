package edu.wkd.towave.memorycleaner.injector.module;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.BuildConfig;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import javax.inject.Singleton;
import net.tsz.afinal.FinalDb;

/**
 * Created by Administrator on 2016/5/4.
 */
@Module public class AppModule {
    private final App app;


    public AppModule(App app) {
        this.app = app;
    }


    @Provides @Singleton App provideApplication() {
        return app;
    }


    @Provides @Singleton @ContextLifeCycle("App")
    Context provideActivityContext() {
        return app.getApplicationContext();
    }

    @Provides @Singleton
    FinalDb.DaoConfig provideDaoConfig(@ContextLifeCycle("App") Context context) {
        FinalDb.DaoConfig config = new FinalDb.DaoConfig();
        config.setDbName("ignore.db");
        config.setDbVersion(2);
        config.setDebug(BuildConfig.DEBUG);
        config.setContext(context);
        config.setDbUpdateListener((db, oldVersion, newVersion) -> {
            //if (newVersion == 2 && oldVersion == 1) {
            //    db.execSQL("ALTER TABLE '" + "ignore" + "' ADD COLUMN " +
            //            "appName" + " TEXT;");
            //    db.execSQL("ALTER TABLE '" + "ignore" + "' ADD COLUMN " +
            //            "appIcon" + " BLOB;");
            //}
        });
        return config;
    }

    @Provides @Singleton
    FinalDb provideFinalDb(FinalDb.DaoConfig config) {
        return FinalDb.create(config);
    }
}

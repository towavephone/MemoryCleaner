package edu.wkd.towave.memorycleaner.injector.component;

import android.content.Context;
import dagger.Component;
import edu.wkd.towave.memorycleaner.App;
import edu.wkd.towave.memorycleaner.injector.ContextLifeCycle;
import edu.wkd.towave.memorycleaner.injector.module.AppModule;
import javax.inject.Singleton;
import net.tsz.afinal.FinalDb;

/**
 * Created by Administrator on 2016/5/4.
 */
//Singleton即是Application scope
@Singleton @Component(modules = AppModule.class) public interface AppComponent {
    App app();

    @ContextLifeCycle("App") Context context();

    FinalDb finalDb();

    FinalDb.DaoConfig daoConfig();
    //void inject(BaseActivity baseActivity);
}

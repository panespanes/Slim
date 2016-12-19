package panes.slim.app;

import android.app.Application;
import android.content.Context;

import panes.slim.core.Inject;

/**
 * Created by panes.
 */
public class MyApplication extends Application {
    public static Application application;
    @Override
    public void onCreate() {
        super.onCreate();
//        Slim.initSmall(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Inject.injectTK(this, base);
        application = this;
    }
}

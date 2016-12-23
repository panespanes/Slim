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
//        ComponentName cName = new ComponentName("com.android.launcher",
//                "com.android.launcher2.Launcher");
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setComponent(cName);
//        startActivity(intent);

//        Inject.injectTK(this, base);
        Inject.injectDirWithResourcesHooked(this, base);

        application = this;
    }
}

package panes.slim.app;

import android.app.Application;
import android.content.Context;

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
//        Inject.initDynamic(this, base);
        application = this;
    }

    //    private void dynamicLoad() {
//        SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
//        SlimConfig.addResourcesBundle(slimBundle);
//        Slim.init(getApplicationContext(), new SlimListener() {
//            @Override
//            public void onSuccess() {
//                Log.i(MainActivity.TAG, "initResource onSuccess.");
//
//            }
//
//            @Override
//            public void onError(String msg) {
//                Log.i(MainActivity.TAG, "onError: " + msg);
//            }
//        });
//    }
}

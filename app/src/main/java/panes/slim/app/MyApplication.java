package panes.slim.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import panes.slim.Slim;
import panes.slim.SlimBundle;
import panes.slim.SlimConfig;
import panes.slim.SlimListener;

/**
 * Created by panes.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        dynamicLoad();
    }

    private void dynamicLoad() {
        SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
        SlimConfig.addResourcesBundle(slimBundle);
        Slim.init(getApplicationContext(), new SlimListener() {
            @Override
            public void onSuccess() {
                Log.i(MainActivity.TAG, "initResource onSuccess.");

            }

            @Override
            public void onError(String msg) {
                Log.i(MainActivity.TAG, "onError: " + msg);
            }
        });
    }
}

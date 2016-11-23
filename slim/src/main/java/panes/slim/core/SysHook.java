package panes.slim.core;

import android.app.Instrumentation;
import android.content.res.AssetManager;

import java.lang.reflect.Method;

/**
 * Created by panes on 2016/11/22.
 */
public class SysHook {
    public static int mErrorCode;
    public static AssetManager new_AssetManager() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            // cannot new AssetManager api24: AssetManager() {throw new RuntimeException("Stub!");}
        return assetManager;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Instrumentation new_instrumentation(){
        return null;
    }

    public static Method method_AssetManager_addAssetPath() {
        try {
            return AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}

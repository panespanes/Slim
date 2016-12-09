package panes.slim.core;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import panes.slim.SlimBundle;
import panes.slim.SlimConfig;

/**
 * Created by panes.
 */
public class Inject {
    private static Object _mLoadedApk;
    private static Object _sActivityThread;

    static class ActivityThreadGetter implements Runnable {
        ActivityThreadGetter() {

        }

        public void run() {
            try {
                _sActivityThread = Runtime.ActivityThread_currentActivityThread.invoke(Runtime.ActivityThread.getmClass(), new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (Runtime.ActivityThread_currentActivityThread) {
                Runtime.ActivityThread_currentActivityThread.notify();
            }
        }
    }

    static {
        _sActivityThread = null;
        _mLoadedApk = null;

        try {
            Runtime.ActivityThread = QuickReflection.into("android.app.ActivityThread");
            Runtime.ActivityThread_currentActivityThread = Runtime.ActivityThread.method("currentActivityThread", new Class[0]);
            Runtime.ActivityThread_mPackages = Runtime.ActivityThread.field("mPackages");
            Runtime.LoadedApk = QuickReflection.into("android.app.LoadedApk");
            Runtime.LoadedApk_mResources = Runtime.LoadedApk.field("mResources");
            Runtime.ContextImpl = QuickReflection.into("android.app.ContextImpl");
            Runtime.ContextImpl_mResources = Runtime.ContextImpl.field("mResources");
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
        }

    }



    public static Object getActivityThread() throws Exception {
        if (_sActivityThread == null) {
            if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
                _sActivityThread = Runtime.ActivityThread_currentActivityThread.invoke(null, new Object[0]);
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                synchronized (Runtime.ActivityThread_currentActivityThread) {
                    handler.post(new ActivityThreadGetter());
                    Runtime.ActivityThread_currentActivityThread.wait();
                }
            }
        }
        return _sActivityThread;
    }

    public static Object getLoadedApk(Object obj, String str) throws Exception {
        if (_mLoadedApk == null) {
            WeakReference weakReference = (WeakReference) ((Map) Runtime.ActivityThread_mPackages.get(obj)).get(str);
            if (weakReference != null) {
                _mLoadedApk = weakReference.get();
            }
        }
        return _mLoadedApk;
    }

    public static void initDynamic(Application application, Context context){
        try {
            SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
            SlimConfig.addResourcesBundle(slimBundle);
            AssetManager assetManager = SysHook.new_AssetManager();
            Method mth = SysHook.method_AssetManager_addAssetPath();
            SlimBundle[] bundles = SlimConfig.getResourcesBundles();
            // need to add application.getResourcesPath
            mth.invoke(assetManager, bundles[0].getPath());
            mth.invoke(assetManager, application.getPackageResourcePath());
            Log.i(SlimConfig.TAG, "invoke");
            QuickReflection.QrClass<Object> ContextImpl = QuickReflection.into("android.app.ContextImpl");
            QuickReflection.QrField<Object, Resources> ContextImpl_mResources = ContextImpl.field("mResources");
            Resources resources = new Resources(assetManager, application.getResources().getDisplayMetrics(), application.getResources().getConfiguration());
            ContextImpl_mResources.set(context, resources);
            Object loadedApk = getLoadedApk(getActivityThread(), application.getPackageName());
            QuickReflection.QrClass<Object> LoadedApk = QuickReflection.into("android.app.LoadedApk");
            QuickReflection.QrField<Object, Resources> LoadedApk_mResources = LoadedApk.field("mResources");
            LoadedApk_mResources.set(loadedApk, resources);
            Log.i(SlimConfig.TAG, "set resources");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void injectResources(Application application, Resources resources, AssetManager assetManager) throws Exception {
        Object activityThread = getActivityThread();
        if (activityThread == null) {
            throw new Exception("Failed to get ActivityThread.sCurrentActivityThread");
        }
        Object loadedApk = getLoadedApk(activityThread, application.getPackageName());
        if (loadedApk == null) {
            throw new Exception("Failed to get ActivityThread.mLoadedApk");
        }
        try {
            QuickReflection.QrField<Object, Resources> LoadedApk_mResources = Runtime.LoadedApk.field("mResources");
            QuickReflection.QrClass<Object> ContextImpl = QuickReflection.into("android.app.ContextImpl");
            QuickReflection.QrField<Object, Resources> ContextImpl_mResources = ContextImpl.field("mResources");
            LoadedApk_mResources.set(loadedApk, resources);
            ContextImpl_mResources.set(application.getBaseContext(), resources);

            Method mEnsureStringBlocks = AssetManager.class.getDeclaredMethod("ensureStringBlocks", new Class[0]);
            mEnsureStringBlocks.setAccessible(true);
            mEnsureStringBlocks.invoke(assetManager, new Object[0]);
            QuickReflection.QrClass<Object> QrResourcesManager = QuickReflection.into("android.app.ResourcesManager");
            QuickReflection.QrField<Object, ArrayMap<?, WeakReference<Resources>>> mActiveResources = QrResourcesManager.field("mActiveResources");
            QuickReflection.QrMethod getInstance = QrResourcesManager.staticMethod("getInstance");
            Object ResourcesManager = getInstance.invoke(null);
            ArrayMap<?, WeakReference<Resources>> weakReferenceArrayMap = mActiveResources.get(ResourcesManager);
            Collection<WeakReference<Resources>> references2 = weakReferenceArrayMap.values();
            Log.i(SlimConfig.TAG, "number = " + references2.size());
            for (WeakReference<Resources> reference : references2) {
                Log.i(SlimConfig.TAG, "enter reference");
                Resources resource = reference.get();
                if (resource == null) {
                    Log.i(SlimConfig.TAG, "resource not null");
                } else {
                    Log.i(SlimConfig.TAG, "resource is null");
                }
                try {
                    Field mAssets = resource.getClass().getDeclaredField("mAssets");
                    mAssets.setAccessible(true);
                    mAssets.set(resource, assetManager);
                    Log.i(SlimConfig.TAG, "hook mAssets");
                } catch (NoSuchFieldException e) {
                    // MIUI ?!
                    Field mResourcesImpl = resource.getClass().getDeclaredField("mResourcesImpl");
                    mResourcesImpl.setAccessible(true);
                    Object resourcesImpl = mResourcesImpl.get(resource);
                    Field mAssets1 = resourcesImpl.getClass().getDeclaredField("mAssets");
                    mAssets1.set(resource, assetManager);
                }
                resource.updateConfiguration(resource.getConfiguration(), resource.getDisplayMetrics());

            }
            Log.i(SlimConfig.TAG, "inject resources");
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
        }
//        Runtime.ContextImpl_mTheme.set(application.getBaseContext(), null);
    }


}

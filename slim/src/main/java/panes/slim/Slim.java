package panes.slim;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import panes.slim.core.Inject;
import panes.slim.core.QuickReflection;
import panes.slim.core.Runtime;
import panes.slim.core.SysHook;
import panes.slim.inflater.resources.drawable.DrawableInflater;

/**
 * Slim Interface
 *
 * @author panes
 */
public class Slim {
    /**
     * call when application created in case of long time waiting when activity starts.
     */
    public static void init(Application application) {
        try {
            SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
            SlimConfig.addResourcesBundle(slimBundle);
            AssetManager assetManager = SysHook.new_AssetManager();
            Method mth = SysHook.method_AssetManager_addAssetPath();
            SlimBundle[] bundles = SlimConfig.getResourcesBundles();
            mth.invoke(assetManager,application.getPackageResourcePath(), bundles[0].getPath());
            Resources resources = new Resources(assetManager, application.getResources().getDisplayMetrics(), application.getResources().getConfiguration());

            Inject.injectResources(application, resources, assetManager);

        }  catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(SlimConfig.TAG, "invoke");
//        Runtime.Resources = QuickReflection.into(Resources.class);
    }

    public static void initDynamic(Application application) {
        try {
            SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
            SlimConfig.addResourcesBundle(slimBundle);
            AssetManager assetManager = SysHook.new_AssetManager();
            Method mth = SysHook.method_AssetManager_addAssetPath();
            SlimBundle[] bundles = SlimConfig.getResourcesBundles();
            // need to add application.getResourcesPath
            mth.invoke(assetManager, bundles[0].getPath());
            Object result3 = mth.invoke(assetManager, application.getPackageResourcePath());
            Log.i(SlimConfig.TAG, "invoke");
            QuickReflection.QrClass<Object> ContextImpl = QuickReflection.into("android.app.ContextImpl");
            QuickReflection.QrField<Object, Resources> mResources = ContextImpl.field("mResources");
            Resources resources = new Resources(assetManager, application.getResources().getDisplayMetrics(), application.getResources().getConfiguration());
            mResources.set(application.getApplicationContext(), resources);

            Log.i(SlimConfig.TAG, "set resources");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
        }
    }

    public static Resources getDynamic(Application application) {
        try {
            SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
            SlimConfig.addResourcesBundle(slimBundle);
            AssetManager assetManager = SysHook.new_AssetManager();
            Method mth = SysHook.method_AssetManager_addAssetPath();
            Method mthSingle = SysHook.method_AssetManager_addAssetPath();
            SlimBundle[] bundles = SlimConfig.getResourcesBundles();
            mth.invoke(assetManager,application.getPackageResourcePath());
            mth.invoke(assetManager,bundles[0].getPath());
            Log.i(SlimConfig.TAG, "invoke");
//            QuickReflection.QrClass<Context> Context = QuickReflection.into(Context.class);
//            QuickReflection.QrField<Context, Resources> mResources = Context.field("mResources");
            Log.i(SlimConfig.TAG, "get resources");
            return new Resources(assetManager, application.getResources().getDisplayMetrics(), application.getResources().getConfiguration());
//            mResources.set(context, resources);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NewApi")
    public static void initSmall(Application application) {
        try {
            SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
//            SlimBundle slimBundle2 = new SlimBundle("panes.slim.bundle2", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle2.apk", SlimBundle.TYPE_RESOURCES);
            SlimConfig.addResourcesBundle(slimBundle);
            AssetManager assetManager = SysHook.new_AssetManager();
            Method mth = SysHook.method_AssetManager_addAssetPath();
            SlimBundle[] bundles = SlimConfig.getResourcesBundles();
            // 0 on failure
            Object result = mth.invoke(assetManager, bundles[0].getPath());
            Log.i(SlimConfig.TAG, "addAssetPath result1 = "+(Integer)result);
            Object result3 = mth.invoke(assetManager, application.getPackageResourcePath());
            Log.i(SlimConfig.TAG, "addAssetPath result3 = "+(Integer)result3);
            Runtime.resources = new Resources(assetManager, application.getResources().getDisplayMetrics(), application.getResources().getConfiguration());

            Method mEnsureStringBlocks = AssetManager.class.getDeclaredMethod("ensureStringBlocks", new Class[0]);
            mEnsureStringBlocks.setAccessible(true);
            mEnsureStringBlocks.invoke(assetManager, new Object[0]);

            QuickReflection.QrClass<Object> QrResourcesManager = QuickReflection.into("android.app.ResourcesManager");
            QuickReflection.QrField<Object, ArrayMap<?, WeakReference<Resources>>> mActiveResources = QrResourcesManager.field("mActiveResources");
            QuickReflection.QrMethod getInstance = QrResourcesManager.staticMethod("getInstance");
            Object ResourcesManager = getInstance.invoke(null);
            ArrayMap<?, WeakReference<Resources>> weakReferenceArrayMap = mActiveResources.get(ResourcesManager);
            Collection<WeakReference<Resources>> references = weakReferenceArrayMap.values();
            Log.i(SlimConfig.TAG, "resources size = " + references.size());
            for (WeakReference<Resources> reference : references) {
                Log.i(SlimConfig.TAG, "enter reference");
                Resources resource = reference.get();
                if (resource == null) {
                    Log.i(SlimConfig.TAG, "resource is null");
                } else {
                    Log.i(SlimConfig.TAG, "resource not null");
                }
                try {
                    Field mAssets = resource.getClass().getDeclaredField("mAssets");
                    mAssets.setAccessible(true);
                    mAssets.set(resource, assetManager);
                    Log.i(SlimConfig.TAG, "hook mAssets");
                } catch (NoSuchFieldException e) {
                    // MiUIResources ?!
                    Field mResourcesImpl = resource.getClass().getDeclaredField("mResourcesImpl");
                    mResourcesImpl.setAccessible(true);
                    Object resourcesImpl = mResourcesImpl.get(resource);
                    Field mAssets1 = resourcesImpl.getClass().getDeclaredField("mAssets");
                    mAssets1.set(resource, assetManager);
                }

                if (Build.VERSION.SDK_INT >= 21) {
                    for (WeakReference<Resources> wr : references) {
                        Resources resources = wr.get();
                        if (resources == null) continue;

                        // android.util.Pools$SynchronizedPool<TypedArray>
                        Field mTypedArrayPool = Resources.class.getDeclaredField("mTypedArrayPool");
                        mTypedArrayPool.setAccessible(true);
                        Object typedArrayPool = mTypedArrayPool.get(resources);
                        // Clear all the pools
                        Method acquire = typedArrayPool.getClass().getMethod("acquire");
                        acquire.setAccessible(true);
                        while (acquire.invoke(typedArrayPool) != null) ;
                    }
                }
                resource.updateConfiguration(resource.getConfiguration(), resource.getDisplayMetrics());

            }
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
//        catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * cannot match the specified resource . should be Resources.getDrawable
     *
     * @param resName
     * @return
     */
    @Deprecated
    private static int getDrawableId(String resName) {
        return DrawableInflater.instance().inflateId(resName); //not specified image
    }


    /**
     * get a Drawable instance from resourceId
     *
     * @param bundleResId id MUST come from bundle's R.java !!!
     * @return
     */
    public static Drawable getDrawable(int bundleResId) {
        return DrawableInflater.instance().inflateById(bundleResId);
    }

    /**
     * get a Drawable instance by resourceName in bundle's R.java
     *
     * @param resName not include resource type, eg.:R.drawable.ic_launch than 'ic_launch' is the resourceName
     * @return
     */
    public static Drawable getDrawable(String resName) {
        return DrawableInflater.instance().inflateByName(resName);
    }
}

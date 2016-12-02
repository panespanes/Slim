package panes.slim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

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
     * call when application created in case of long time starting activity.
     */
    public static void init(Context context, final SlimListener slimListener) {
//        new ResourcesManager().initResources(context, new SlimListener() {
//            @Override
//            public void onSuccess() {
//                ActivityThread = Hack.into("android.app.ActivityThread");
        Runtime.Resources = QuickReflection.into(Resources.class);
//                Application = Hack.into(Application.class);
//                AssetManager = Hack.into(AssetManager.class);
//                IPackageManager = Hack.into("android.content.pm.IPackageManager");
//                Service = Hack.into(Service.class);
        try {

            /**
             * Context->ContextImpl.getAssets()/ContextImpl.getResources()
             */

            Runtime.ContextImpl = QuickReflection.into("android.app.ContextImpl");
            Runtime.ContextThemeWrapper = QuickReflection.into(ContextThemeWrapper.class);
            Runtime.ContextWrapper = QuickReflection.into("android.content.ContextWrapper");

            Runtime.ContextImpl_resources = Runtime.ContextImpl.field("mResources");
            Runtime.ContextImpl_theme = Runtime.ContextImpl.field("mTheme");
//                    ContextThemeWrapper_resources;
//                    Runtime.ContextThemeWrapper_context = Runtime.ContextThemeWrapper.field("mBase");
//                    public static QuickReflection.QrClass<Resources> Resources;
//                    public static QuickReflection.QrClass<Object> ActivityThread;

            slimListener.onSuccess();
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
            slimListener.onError(e.toString());
        }

//                slimListener.onSuccess();
//            }
//
//            @Override
//            public void onError(String msg) {
//                slimListener.onError(msg);
//            }
//        }); //get prepared for ResourcesBundle.
    }

    @SuppressLint("NewApi")
    public static void initSmall() {
        try {
            SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
            SlimConfig.addResourcesBundle(slimBundle);
            AssetManager assetManager = SysHook.new_AssetManager();
            Method mth = SysHook.method_AssetManager_addAssetPath();
            SlimBundle[] bundles = SlimConfig.getResourcesBundles();
            mth.invoke(assetManager, bundles[0].getPath());

            Method mEnsureStringBlocks = AssetManager.class.getDeclaredMethod("ensureStringBlocks", new Class[0]);
            mEnsureStringBlocks.setAccessible(true);
            mEnsureStringBlocks.invoke(assetManager, new Object[0]);

//            Collection<WeakReference<Resources>> references = null;

//            Class<?> resourcesManagerClass = Class.forName("android.app.ResourcesManager");
//            Method mGetInstance = resourcesManagerClass.getDeclaredMethod("getInstance", new Class[0]);
//            mGetInstance.setAccessible(true);
//            Object resourcesManager = mGetInstance.invoke(null, new Object[0]);
//            try {
//                Field fMActiveResources = resourcesManagerClass.getDeclaredField("mActiveResources");
//                fMActiveResources.setAccessible(true);
//
//                ArrayMap<?, WeakReference<Resources>> arrayMap = (ArrayMap) fMActiveResources.get(resourcesManager);
//
//                references = arrayMap.values();
//            } catch (NoSuchFieldException ignore) {
//            }
//            Log.i(SlimConfig.TAG, "size = " + references.size());


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
                if (resource == null){
                    Log.i(SlimConfig.TAG, "resource not null");
                } else {
                    Log.i(SlimConfig.TAG, "resource is null");
                }
                try {
                    Field mAssets = resource.getClass().getDeclaredField("mAssets");
                    mAssets.setAccessible(true);
                    mAssets.set(resource, assetManager);
                    Log.i(SlimConfig.TAG, "hook mAssets");
                } catch (NoSuchFieldException e){
                    // MIUI ?!
                    Field mResourcesImpl = resource.getClass().getDeclaredField("mResourcesImpl");
                    mResourcesImpl.setAccessible(true);
                    Object resourcesImpl = mResourcesImpl.get(resource);
                    Field mAssets1 = resourcesImpl.getClass().getDeclaredField("mAssets");
                    mAssets1.set(resource, assetManager);
                }
                resource.updateConfiguration(resource.getConfiguration(), resource.getDisplayMetrics());

            }
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
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

package panes.slim;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import panes.slim.core.QuickReflection;
import panes.slim.core.Runtime;
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
//        new ResourcesManger().initResources(context, new SlimListener() {
//            @Override
//            public void onSuccess() {
//                ActivityThread = Hack.into("android.app.ActivityThread");
        Runtime.Resources = QuickReflection.into(Resources.class);
//                Application = Hack.into(Application.class);
//                AssetManager = Hack.into(AssetManager.class);
//                IPackageManager = Hack.into("android.content.pm.IPackageManager");
//                Service = Hack.into(Service.class);
        try {
            Runtime.ContextImpl = QuickReflection.into("android.app.ContextImpl");
            Runtime.ContextThemeWrapper = QuickReflection.into(ContextThemeWrapper.class);
            Runtime.ContextWrapper = QuickReflection.into("android.content.ContextWrapper");

            Runtime.ContextImpl_resources = Runtime.ContextImpl.field("mResources");
            Runtime.ContextImpl_theme = Runtime.ContextImpl.field("mTheme");
//                    ContextThemeWrapper_resources;
//                    Runtime.ContextThemeWrapper_context = Runtime.ContextThemeWrapper.field("mBase");
            Method mth = Slim.class.getMethod("getDrawable", String.class);
            Object avatar = mth.invoke(new Slim(), "avatar");
            Runtime.Slim = QuickReflection.into(Slim.class);
//                    public static QuickReflection.QrClass<Resources> Resources;
//                    public static QuickReflection.QrClass<Object> ActivityThread;
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
            slimListener.onError(e.toString());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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


    public static int getDrawableId(String resName) {
        return DrawableInflater.instance().inflateId(resName);
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

package panes.slim.core;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import panes.slim.SlimBundle;
import panes.slim.SlimConfig;
import panes.slim.SlimException;
import panes.slim.SlimListener;
import panes.slim.util.LogUtil;

/**
 * Created by panes.
 */
public class ResourcesManger {
    private static AssetManager mAssetManager;
    public static Context applicationContext;
    /**
     * return instance of Resources that is loaded by specific AssetManager
     * @return
     */
    public Resources getResources() {
        Resources resources = new Resources(mAssetManager, applicationContext.getResources().getDisplayMetrics(), applicationContext.getResources().getConfiguration());
        if (resources == null)
            LogUtil.i("warning: resources = null");

        return resources;
    }

    public void initResources(Context context, SlimListener slimListener){
        if (slimListener == null){
            throw new NullPointerException("SlimListener is null");
        }
        try {
            new ResourcesManger().load(context, slimListener);
        } catch (SlimException e) {
            slimListener.onError(e.getMsg());
        }
    }

    public void load (Context context, SlimListener listener) throws SlimException{
        // TODO. async op
        applicationContext = context;
        AssetManager assetManager = SysHook.new_AssetManager();
        assetManager = addAssets(assetManager);
        if (assetManager == null){
            if (listener != null){
                listener.onError("异常详见日志");
            }
        } else {
            mAssetManager = assetManager;
            listener.onSuccess();
        }
    }

    private AssetManager addAssets(AssetManager assetManager) throws SlimException {
        Method mth = SysHook.method_AssetManager_addAssetPath();
        SlimBundle[] bundles = SlimConfig.getResourcesBundles();
        if (bundles == null || bundles.length < 1) {
            throw new SlimException(-1);
        }
        try {
            for (SlimBundle bundle : bundles) {
                File bundleFile = new File(bundle.getPath());
                if (bundleFile.isDirectory() || bundleFile.getTotalSpace()<=0){
                    throw new SlimException(4);
                }
                mth.invoke(assetManager, bundle.getPath());
                return assetManager;// TODO. only support a bundle once, more bundles will cause id conflict.
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new SlimException(3);
    }
}

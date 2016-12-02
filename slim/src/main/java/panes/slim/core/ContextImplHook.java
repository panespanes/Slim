package panes.slim.core;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;

import panes.slim.util.LogUtil;

/**
 * Created by panes.
 */
public class ContextImplHook extends ContextWrapper {
    public ContextImplHook(Context context) {
        super(context);

    }
    @Override
    public Resources getResources() {
        LogUtil.i("hook getResources");
        return ResourcesManager.mResources;
    }

    @Override
    public AssetManager getAssets() {
        LogUtil.i("hook getAssets");
        return ResourcesManager.mResources.getAssets();
    }

}
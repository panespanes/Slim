package panes.slim.inflater.resources;

import android.content.res.Resources;
import android.text.TextUtils;

import panes.slim.SlimConfig;
import panes.slim.SlimException;
import panes.slim.core.ResourcesManger;
import panes.slim.util.LogUtil;

/**
 * Created by panes.
 */
public class ResourcesInflater {
    protected static Resources mResources;
    protected static String TYPE_DRAWABLE = "drawable";
    protected static String TYPE_LAYOUT = "layout";
    protected void initResources(){
        if (mResources == null){
            try {
                mResources = new ResourcesManger().getResources();
            } catch (SlimException e) {
                e.printStackTrace();
            }
        }
    }

    public int inflateId(String type, String resName) {
        initResources();
        String packageName =  SlimConfig.getResourcesBundles()[0].getPackageName();
        if (TextUtils.isEmpty(packageName)){
            LogUtil.i("package name is null. check SlimConfig.resourcesBundles");// TODO. use PMS. path->packageName as default.
            return -2;
        }
        int id = mResources.getIdentifier(resName, type, packageName);
        return id == 0x00 ? -1: id;
    }
}

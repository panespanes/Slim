package panes.slim;

import android.content.Context;
import android.graphics.drawable.Drawable;

import panes.slim.core.ResourcesManger;
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
    public static void init(Context context, SlimListener slimListener) {
        new ResourcesManger().initResources(context, slimListener); //get prepared for ResourcesBundle.
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

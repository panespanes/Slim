package panes.slim.inflater.resources.drawable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import panes.slim.SlimConfig;
import panes.slim.core.ResourcesManager;
import panes.slim.inflater.resources.ResourcesInflater;

/**
 * Created by panes.
 */
public class DrawableInflater extends ResourcesInflater implements IDrawableInflater {
    private static DrawableInflater mInstance;
    private DrawableInflater (Context context){
    }
    public static DrawableInflater instance(){
        if (mInstance == null){
            mInstance = new DrawableInflater(ResourcesManager.applicationContext); // TODO. should always use app context?
        }
        return mInstance;
    }
    @Override
    public Drawable inflateByName(String resName) {
        initResources();

        return mResources.getDrawable(inflateId(resName));
    }

    @Override
    public Drawable inflateById(int resId) {
        initResources();
        return mResources.getDrawable(resId);
    }

    @Override
    public int inflateId(String resName) {
        int id = super.inflateId(TYPE_DRAWABLE, resName);
        Log.i(SlimConfig.TAG, "id = " + id);
        return id; // does not match.
    }

}

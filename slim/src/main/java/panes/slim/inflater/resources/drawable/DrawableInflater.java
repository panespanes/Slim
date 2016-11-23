package panes.slim.inflater.resources.drawable;

import android.content.Context;
import android.graphics.drawable.Drawable;

import panes.slim.core.ResourcesManger;
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
            mInstance = new DrawableInflater(ResourcesManger.applicationContext); // TODO. should always use app context?
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
        return super.inflateId(TYPE_DRAWABLE, resName);
    }

}

package panes.slim.inflater.resources.drawable;

import android.graphics.drawable.Drawable;

import panes.slim.inflater.IInflater;

/**
 * Created by panes.
 */
public interface IDrawableInflater extends IInflater{
    Drawable inflateByName(String resName);
    Drawable inflateById(int resId);
    int inflateId(String resName);
}

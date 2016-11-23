package panes.slim.inflater.resources.layout;

import android.content.Context;
import android.view.View;

import panes.slim.inflater.resources.ResourcesInflater;

/**
 * Created by panes.
 */
public class LayoutInflater extends ResourcesInflater implements ILayoutInflater{
    private Context mContext;
    public LayoutInflater (Context context) {
        mContext = context;
    }
    @Override
    public View inflateByName(String resName) {
        return View.inflate(getContext(mContext), inflateId(resName), null);
    }

    @Override
    public int inflateId(String resName) {
        return inflateId(TYPE_LAYOUT, resName);
    }

    public Context getContext(Context context){
        return context;
    }
}

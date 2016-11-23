package panes.slim.inflater.resources.layout;

import android.view.View;

import panes.slim.inflater.IInflater;

/**
 * Created by panes.
 */
public interface ILayoutInflater extends IInflater {
    @Override
    View inflateByName(String resName);

    int inflateId(String resName);
}

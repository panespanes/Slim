package panes.slim.inflater;

/**
 * Created by panes.
 */
public interface IInflater {
    /**
     * return the production of inflater.
     * @return
     */
    Object inflateByName(String resName);
}

package panes.slim;

/**
 * Created by panes.
 */
public class SlimConfig {
    private SlimConfig (){}
    public static String TAG = "Slim";
    public static boolean isDebug = true;
    /**
     * className to redirect in case of startup failure. e.g."panes.slim.app.MainActivity"
     */
    public static String BackUpActivity;


    /**
     * bundles which include R.drawable, R.string and R.layout.
     */
    private static SlimBundle[] resourcesBundles;
    /**
     * include .so files
     */
    private static SlimBundle[] soBundles;
    /**
     * include .jar files
     */
    private static SlimBundle[] jarBundles;

    public static void addResourcesBundle(SlimBundle bundle){
        resourcesBundles = new SlimBundle[]{bundle};
    }
    public static void addSoBundle(SlimBundle bundle){
        soBundles = new SlimBundle[]{bundle};
    }

    public static SlimBundle[] getResourcesBundles() {
        return resourcesBundles;
    }
}

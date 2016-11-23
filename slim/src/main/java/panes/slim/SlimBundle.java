package panes.slim;

/**
 * describe an apk's file name and where to store it.
 * Created by panes.
 */
public class SlimBundle {
    public static final int TYPE_RESOURCES = 0;
    public static final int TYPE_SO = 1;
    public static final int TYPE_JAR = 2;
    /**
     * packageName of MainApplication if null.
     */
    private String packageName;
    /**
     * full path on SD card. eg.: /storage/emulated/0/panes.slim/bundle.apk
     */
    private String path;
    /**
     * TYPE_RESOURCES as default
     */
    private int type;
    @Deprecated
    public SlimBundle (String path, int type){
        this.path = path;
        this.type = type;
    }
    public SlimBundle (String packageName, String path, int type){
        this.packageName = packageName;
        this.path = path;
        this.type = type;
    }


    public String getPath() {
        return path;
    }

    public int getType() {
        return type;
    }
    public String getPackageName() {
        return packageName;
    }

}

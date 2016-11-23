package panes.slim;

import java.util.HashMap;

/**
 * Created by panes.
 */
public class SlimException extends RuntimeException {
    private int mErrorCode;
    private static HashMap<Integer, String> map = new HashMap<>();
    static {
        map.put(-1, "请先配置SlimConfig.resourcesBundles/soBundles/jarBundles");
        map.put(3, "系统异常. AssetManager::addAssetPath()");
        map.put(4, "检查SD中Bundle文件有效性或读取SD卡权限是否打开");
    }
    public SlimException(int errorCode) {
        super();
        mErrorCode = errorCode;
    }

    public String getMsg() {
        return map.get(mErrorCode);
    }
}

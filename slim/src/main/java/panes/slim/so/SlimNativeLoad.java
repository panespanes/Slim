package panes.slim.so;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import panes.slim.SlimConfig;

/**
 * Created by panes.
 */
public class SlimNativeLoad {
    public static final String[] ABIs = new String[]{"armeabi", "armeabi-v7a", "arm64-v8a", "mips", "mips64", "x86", "x86_64"};
    public static boolean loadNative (Context context, String path){
        if (!copy(context, path)){
            Log.e(SlimConfig.TAG, "复制so库失败");
            return false;
        }
        System.load(genNativePath(context, path));
        return true;
    }

    private static boolean copy(Context context, String path){
        boolean result = false;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = new FileInputStream(path);
            String nativePath = genNativePath(context, path);
            File file = new File(nativePath);
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String genNativePath (Context context, String filePath){
        File jniDir = context.getDir("jniLibs", Activity.MODE_PRIVATE);
        return jniDir.getAbsolutePath() + File.separator + new File(filePath).getName();
    }
}

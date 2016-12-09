package panes.slim.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import panes.slim.Slim;
import panes.slim.SlimBundle;
import panes.slim.SlimConfig;
import panes.slim.core.Inject;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Slim";
    TextView tv;
    ImageView iv;
    Resources resources;
    Context mBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources resourcesOnCreate = getResources();
        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            tv.setText("show resource in bundle.");

//            dynamicLoad();
//            new ResourcesManager().initResources(getApplicationContext(), new SlimListener() {
//                @Override
//                public void onSuccess() {
//                    Log.i(SlimConfig.TAG, "init success");
////                    Slim.initSmall(ResourcesManager.getAssetManager());
//                    iv.setImageResource(getId());
//
//                }
//
//                @Override
//                public void onError(String msg) {
//                    Log.i(SlimConfig.TAG, msg);
//                }
//            });
        }

    }

    public int getId() {
        int id = getResources().getIdentifier("logob", "drawable", SlimConfig.getResourcesBundles()[0].getPackageName());
        Log.i(SlimConfig.TAG, String.valueOf("id =" + id));
        return id;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Log.i(TAG, "start slim");
//                dynamicLoad();
            } else {
                tv.setText("未开启读取SD卡权限.");
            }
        }
    }

    private void dynamicLoad() {
        SlimBundle slimBundle = new SlimBundle("panes.slim.bundle", Environment.getExternalStorageDirectory() + File.separator + "slim.bundle.apk", SlimBundle.TYPE_RESOURCES);
        SlimConfig.addResourcesBundle(slimBundle);
        Slim.init(getApplication());
    }

//    private void test() {
//        Slim.initSmall();
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
//        try {
//            resources = Slim.getDynamic(newBase);
//            Field field = newBase.getClass().getDeclaredField("mResources");
//            field.setAccessible(true);
//            field.set(newBase, resources);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
        Inject.initDynamic(MyApplication.application, newBase);

        super.attachBaseContext(newBase);
    }

//    @Override
//    public Resources getResources() {
////        return super.getResources();
//        return Slim.getDynamic(getApplication());
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        Slim.init(getApplication());
//        Resources resourcesOnResume = getResources();

//        iv.setImageDrawable(resources.getDrawable(id));

//        Resources res = Slim.getDynamic(getApplication());
        Resources superRes = getResources();
//        int id = superRes.getIdentifier("logob", "drawable", "panes.slim.bundle");
        int superId = superRes.getIdentifier("logoy", "drawable", "panes.slim.app");
//        int hookedId = Runtime.resources.getIdentifier("logob", "drawable", "panes.slim.app");
        Log.i(SlimConfig.TAG, "id = " + Integer.toHexString(superId));
//        Log.i(SlimConfig.TAG, "id = " + Integer.toHexString(hookedId));
        iv.setImageDrawable(superRes.getDrawable(superId));



//        iv.setImageResource(R.drawable.logob);
    }

    private int getBundleId(){
        return 0x7f020001;
    }

}

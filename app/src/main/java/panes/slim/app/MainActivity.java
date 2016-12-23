package panes.slim.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import panes.slim.SlimConfig;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Slim";
    TextView tv;
    ImageView iv;
    Resources resources;
    Context mBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Inject.injectTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources resourcesOnCreate = getResources();
        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
//            tv.setText("show resource in bundle.");

        }

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

    @Override
    protected void onResume() {
        super.onResume();
        int id = getResources().getIdentifier("logoppp", "drawable", "panes.slim.app");
        int bundleId = getResources().getIdentifier("logob", "drawable", "panes.slim.app");
        Log.i(SlimConfig.TAG, "id1 = " + Integer.toHexString(id));
        Log.i(SlimConfig.TAG, "id = " + Integer.toHexString(bundleId));
//        iv.setImageDrawable(getResources().getDrawable(bundleId));

// #0x7f020013
//        iv.setImageResource(hookedId);
        iv.setImageResource(R.drawable.logob);
    }

    private int getBundleId(){
        return 0x7f020001;
    }

}

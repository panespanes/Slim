package panes.slim.app;

import android.app.Application;
import android.content.Context;

import panes.slim.core.Inject;

/**
 * Created by panes.
 */
public class MyApplication extends Application {
    public static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
//        Slim.initSmall(this);
//        PushAgent mPushAgent = PushAgent.getInstance(this);
////注册推送服务，每次调用register方法都会回调该接口
//        mPushAgent.register(new IUmengRegisterCallback() {
//
//            @Override
//            public void onSuccess(String deviceToken) {
//                //注册成功会返回device token
//                Log.i(SlimConfig.TAG, "deviceToken = " + deviceToken);
//                MyApplication.deviceToken = deviceToken;
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//
//            }
//        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Inject.injectDirWithResourcesHooked(this, base);

        application = this;
    }
}

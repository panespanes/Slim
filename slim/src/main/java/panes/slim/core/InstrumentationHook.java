package panes.slim.core;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import panes.slim.SlimConfig;
import panes.slim.util.LogUtil;

/**
 * Created by panes.
 */
public class InstrumentationHook extends Instrumentation {
    private Context mContext;
    private Instrumentation mInstrumentation;

    private static interface ExecStartActivityCallback {
        ActivityResult execStartActivity();
    }

    public InstrumentationHook(Instrumentation instrumentation, Context context) {
        mInstrumentation = instrumentation;
        mContext = context;
    }

    public ActivityResult execStartActivity(final Context context, final IBinder iBinder, final IBinder iBinder2, final Activity activity, final Intent intent, final int i) {
        String packageName;
        if (intent.getComponent() != null) {
            packageName = intent.getComponent().getPackageName();
        } else {
            ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, 0);
            if (resolveActivity == null || resolveActivity.activityInfo == null) {
                packageName = null; //neither intent.getComponent nor activityInfo.packageName.
            } else {
                packageName = resolveActivity.activityInfo.packageName;
            }
        }
        try {
            // set hooked Instrumentation
            return (ActivityResult) SysHook.Instrumentation.method("execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class)
                    .invoke(mInstrumentation, context, iBinder, iBinder2, activity, intent, i);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (QuickReflection.QrException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Activity newActivity(Class<?> cls, Context context, IBinder iBinder, Application application, Intent intent, ActivityInfo activityInfo, CharSequence charSequence, Activity activity, String str, Object obj) throws InstantiationException, IllegalAccessException {
        Activity newActivity = mInstrumentation.newActivity(cls, context, iBinder, application, intent, activityInfo, charSequence, activity, str, obj);
        if (Runtime.ContextThemeWrapper_resources != null) {
            // set replaced Resources
            Runtime.ContextThemeWrapper_resources.set(newActivity, ResourcesManager.mResources);
        }
        return newActivity;
    }

    public Activity newActivity(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Activity newActivity;
        try {
            newActivity = mInstrumentation.newActivity(classLoader, str, intent);
            if (Runtime.ContextThemeWrapper_resources != null) {
                // set replaced Resources
                Runtime.ContextThemeWrapper_resources.set(newActivity, ResourcesManager.mResources);
            }
        } catch (ClassNotFoundException e) {
            String redirect = SlimConfig.BackUpActivity;
            if (TextUtils.isEmpty(redirect)) {
                LogUtil.i(str + " not found, config SlimConfig.BackUpActivity to redirect.");
                throw e;
            } else {
                List runningTasks = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
                if (runningTasks != null && runningTasks.size() > 0 && ((ActivityManager.RunningTaskInfo) runningTasks.get(0)).numActivities > 1) {
                    if (intent.getComponent() == null) {
                        intent.setClassName(mContext, str);
                    }
                }
                LogUtil.i("could not find activity class: " + str);
                LogUtil.i("redirect to: " + SlimConfig.BackUpActivity);
                newActivity = mInstrumentation.newActivity(classLoader, SlimConfig.BackUpActivity, intent);
            }
        }
        return newActivity;
    }

    public void callActivityOnCreate(Activity activity, Bundle bundle) {
        ContextImplHook contextImplHook = new ContextImplHook(activity.getBaseContext());
        // set hooked ContextImpl
        Runtime.ContextThemeWrapper_context.set(activity, contextImplHook);
        mInstrumentation.callActivityOnCreate(activity, bundle);
    }

}

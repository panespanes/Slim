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

import java.util.List;
import java.util.logging.Logger;

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
                packageName = null;
            } else {
                packageName = resolveActivity.activityInfo.packageName;
            }
        }
        return (ActivityResult) SysHacks.Instrumentation.method("execStartActivity", Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class, int.class)
                .invoke(mBase, context, iBinder, iBinder2, activity, intent, i);
    }
    public Activity newActivity(Class<?> cls, Context context, IBinder iBinder, Application application, Intent intent, ActivityInfo activityInfo, CharSequence charSequence, Activity activity, String str, Object obj) throws InstantiationException, IllegalAccessException {
        Activity newActivity = this.mBase.newActivity(cls, context, iBinder, application, intent, activityInfo, charSequence, activity, str, obj);
        if (RuntimeArgs.androidApplication.getPackageName().equals(activityInfo.packageName) && SysHacks.ContextThemeWrapper_mResources != null) {
            SysHacks.ContextThemeWrapper_mResources.set(newActivity, RuntimeArgs.delegateResources);
        }
        return newActivity;
    }

    public Activity newActivity(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Activity newActivity;
        try {
            newActivity = this.mBase.newActivity(classLoader, str, intent);
            if (SysHacks.ContextThemeWrapper_mResources != null) {
                SysHacks.ContextThemeWrapper_mResources.set(newActivity, RuntimeArgs.delegateResources);
            }
        } catch (ClassNotFoundException e) {
            String property = Framework.getProperty("ctrip.android.bundle.welcome", "ctrip.android.view.home.CtripSplashActivity");
            if (StringUtil.isEmpty(property)) {
                throw e;
            } else {
                List runningTasks = ((ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
                if (runningTasks != null && runningTasks.size() > 0 && ((ActivityManager.RunningTaskInfo) runningTasks.get(0)).numActivities > 1) {
                    if (intent.getComponent() == null) {
                        intent.setClassName(this.context, str);
                    }
                }
                log.log("Could not find activity class: " + str, Logger.LogLevel.WARN);
                log.log("Redirect to welcome activity: " + property, Logger.LogLevel.WARN);
                newActivity = this.mBase.newActivity(classLoader, property, intent);
            }
        }
        return newActivity;
    }

    public void callActivityOnCreate(Activity activity, Bundle bundle) {
        if (RuntimeArgs.androidApplication.getPackageName().equals(activity.getPackageName())) {
            ContextImplHook contextImplHook = new ContextImplHook(activity.getBaseContext());
            if (!(SysHacks.ContextThemeWrapper_mBase == null || SysHacks.ContextThemeWrapper_mBase.getField() == null)) {
                SysHacks.ContextThemeWrapper_mBase.set(activity, contextImplHook);
            }
            SysHacks.ContextWrapper_mBase.set(activity, contextImplHook);
        }
        this.mBase.callActivityOnCreate(activity, bundle);
    }

}

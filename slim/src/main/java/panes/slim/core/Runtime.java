package panes.slim.core;

import android.content.Context;
import android.content.res.Resources;

import java.util.Map;

import panes.slim.Slim;

/**
 * Created by panes.
 */
public class Runtime {
    public static QuickReflection.QrField<Object, Resources> ContextImpl_mResources;
    public static QuickReflection.QrField<Object, Resources.Theme> ContextImpl_theme;
    public static QuickReflection.QrClass<android.view.ContextThemeWrapper> ContextThemeWrapper;
    public static QuickReflection.QrField<android.view.ContextThemeWrapper, Resources> ContextThemeWrapper_resources;
    public static QuickReflection.QrField<android.view.ContextThemeWrapper, Context> ContextThemeWrapper_context;
    public static QuickReflection.QrClass<Object> ContextImpl;
    public static QuickReflection.QrClass<Resources> Resources;
    public static QuickReflection.QrClass<android.content.ContextWrapper> ContextWrapper;
    public static QuickReflection.QrClass<Object> ActivityThread;
    public static QuickReflection.QrClass<Slim> Slim;
    public static QuickReflection.QrMethod  getAvatar;

    public static QuickReflection.QrClass<Object> LoadedApk;
    public static QuickReflection.QrMethod ActivityThread_currentActivityThread;
    public static QuickReflection.QrField<Object, Map<String, Object>> ActivityThread_mPackages;
    public static QuickReflection.QrField<Object, Resources> LoadedApk_mResources;

    public static Resources resources;
}

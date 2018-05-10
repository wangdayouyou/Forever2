package com.example.doctorwang.forever2.modules.avchat;

import android.content.Context;
import android.widget.Toast;

import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * Created by wangda on 2018/4/5.
 */
public class DemoCache {

    private static Context context;

    private static String account;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    private static boolean mainTaskLaunching;

    public static void setAccount(String account) {
        DemoCache.account = account;
        //NimUIKit.setAccount(account);
        //AVChatKit.setAccount(account);
        //RTSKit.setAccount(account);
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        DemoCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DemoCache.context = context.getApplicationContext();

        //AVChatKit.setContext(context);
        //RTSKit.setContext(context);
    }

    public static void setMainTaskLaunching(boolean mainTaskLaunching) {
        DemoCache.mainTaskLaunching = mainTaskLaunching;

        //AVChatKit.setMainTaskLaunching(mainTaskLaunching);
    }

    public static boolean isMainTaskLaunching() {
        return mainTaskLaunching;
    }
}

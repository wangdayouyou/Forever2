package com.example.doctorwang.forever2.modules.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.doctorwang.forever2.R;
import com.example.doctorwang.forever2.config.Preferences;
import com.example.doctorwang.forever2.modules.avchat.AVChatActivity;
import com.example.doctorwang.forever2.modules.avchat.DemoCache;
import com.example.doctorwang.forever2.modules.login.LoginActivity;
import com.example.doctorwang.forever2.modules.model.Extras;
import com.example.doctorwang.forever2.utils.SysInfoUtil;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

/**
 * 欢迎/导航页（app启动Activity）
 * <p/>
 */
public class WelcomeActivity extends UI {

    private static final String TAG = "WelcomeActivity";

    private boolean customSplash = false;

    private static boolean firstEnter = true; // 是否首次进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_welcome);

        // 表示启动界面在显示中
        DemoCache.setMainTaskLaunching(true);
        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }

        // 如果是首次进入应用
        if (!firstEnter) {
            onIntent();
        } else {
            showSplashView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firstEnter) {
            firstEnter = false;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    customSplash = false;
                    if (canAutoLogin()) {
                        onIntent();
                    } else {
                        LoginActivity.start(WelcomeActivity.this);
                        finish();
                    }
                }
            };
            if (customSplash) {
                new Handler().postDelayed(runnable, 1000);
            } else {
                runnable.run();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /**
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         * 当调用到onNewIntent(intent)的时候，
         * 需要在onNewIntent() 中使用setIntent(intent)赋值给Activity的Intent.否则，后续的getIntent()都是得到老的Intent。
         */
        setIntent(intent);
        if(!customSplash){
            onIntent();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DemoCache.setMainTaskLaunching(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState );
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        LogUtil.i(TAG, "onIntent...");

        if (TextUtils.isEmpty(DemoCache.getAccount())) { // 如果未登录
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) { // 如果没有正在运行才去开启登录界面

                LoginActivity.start(this);
            }
            finish();
        } else { // 如果登录过
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
            if (intent != null) {
                // 如果是从通知栏进入的
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent);
                    return;
                } else if (intent.hasExtra( Extras.EXTRA_JUMP_P2P) || intent.hasExtra( AVChatActivity.INTENT_ACTION_AVCHAT)) {
                    parseNormalIntent(intent);
                }
            }
            if (!firstEnter && intent == null) {
                finish();
            } else {
                showMainActivity();
            }
        }
    }

    /**
     * 已经登陆过，自动登陆
     */
    private boolean canAutoLogin() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        Log.i(TAG, "get local sdk token =" + token);
        return !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
    }

    private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {
            showMainActivity(null);
        } else {
            showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
        }
    }

    private void parseNormalIntent(Intent intent) {
        showMainActivity(intent);
    }

    /**
     * 首次进入，打开欢迎界面
     */
    private void showSplashView() {
        getWindow().setBackgroundDrawableResource(R.drawable.splash_bg);
        customSplash = true;
    }

    private void showMainActivity() {
        showMainActivity(null);
    }

    private void showMainActivity(Intent intent) {
        MainActivity.start(WelcomeActivity.this, intent);
        finish();
    }

}
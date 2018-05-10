package com.example.doctorwang.forever2;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.example.doctorwang.forever2.config.ExtraOptions;
import com.example.doctorwang.forever2.config.Preferences;
import com.example.doctorwang.forever2.config.UserPreferences;
import com.example.doctorwang.forever2.modules.avchat.AVChatActivity;
import com.example.doctorwang.forever2.modules.avchat.AVChatProfile;
import com.example.doctorwang.forever2.modules.avchat.DemoCache;
import com.example.doctorwang.forever2.modules.avchat.PhoneCallStateObserver;
import com.example.doctorwang.forever2.modules.contact.ContactHelper;
import com.example.doctorwang.forever2.modules.event.DemoOnlineStateContentProvider;
import com.example.doctorwang.forever2.modules.event.OnlineStateEventManager;
import com.example.doctorwang.forever2.modules.main.WelcomeActivity;
import com.example.doctorwang.forever2.modules.session.SessionHelper;
import com.example.doctorwang.forever2.utils.AppCrashHandler;
import com.example.doctorwang.forever2.utils.SystemUtil;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.contact.core.query.PinYin;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.netease.nim.uikit.impl.provider.DefaultUserInfoProvider;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimStrings;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.ServerAddresses;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.mixpush.NIMPushClient;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;

import java.util.Map;

public class BaseApplication extends Application {

    private static final String TAG = "NimApplication";

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);

    }

    public void onCreate() {
        super.onCreate();

        // 全局缓存一个Context对象
        // 原本可以以MyApplication代替Context的，但是为了代码移植的完整性，以一个全局缓存类代替
        DemoCache.setContext(this);

        // 注册小米推送appID 、appKey 以及在云信管理后台添加的小米推送证书名称，该逻辑放在 NIMClient init 之前
        NIMPushClient.registerMiPush(this, "DEMO_MI_PUSH", "2882303761517502883", "5671750254883");
        // 注册华为推送
        NIMPushClient.registerHWPush(this, "DEMO_HW_PUSH");

        // 注册自定义小米推送消息处理，这个是可选项
        //NIMPushClient.registerMixPushMessageHandler(new DemoMixPushMessageHandler());
        // 网易云信SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        // 登录网易信：loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        //  监听登录网易信： loginRequest.setCallback(new RequestCallback<LoginInfo>() {...}
        // 说明：在自动登录过程中，如果没有网络或者网络断开或者与网易云通信服务器建立连接失败，
        // 会上报在线状态 NET_BROKEN，表示当前网络不可用，当网络恢复的时候，会触发断网自动重连；
        // 如果连接建立成功但登录超时，会上报在线状态 UNLOGIN，并触发自动重连，无需上层手动调用登录接口。
        // 特别提醒: 在自动登录成功前，调用服务器相关请求接口（由于与网易云通信服务器连接尚未建立成功，会导致发包超时）会报408错误。
        // 但可以调用本地数据库相关接口获取本地数据（自动登录的情况下会自动打开相关账号的数据库）。自动登录过程中也会有用户在线状态回调。
        NIMClient.init(this, getLoginInfo(), getOptions());

        // 空的？
        ExtraOptions.provide();

        // crash handler 未捕捉异常处理类
        AppCrashHandler.getInstance(this);

        // 在主进程中初始化UI组件
        // 注意：除了 NIMClient.init 接口外，其他 SDK 暴露的接口都只能在 UI 进程调用。
        // 如果 APP 包含远程 service，该 APP 的 Application 的 onCreate 会多次调用。
        // 因此，如果需要在 onCreate 中调用除 init 接口外的其他接口，应先判断当前所属进程，
        // 并只有在当前是 UI 进程时才调用。
        if (inMainProcess()) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用

            // init pinyin  什么用？
            PinYin.init(this);
            PinYin.validate();

            // 初始化UIKit模块
            // SDK 由于需要保持后台运行，典型场景下会在独立进程中运行，
            // 第一类接口基本上都是从主进程发起调用，然后在后台进程执行，
            // 最后再将结果返回给主进程。因此，如无特殊说明，所有接口均为异步调用，
            // 开发者无需担心调用 SDK 接口阻塞 UI 的问题。
            initUIKit();

            // 注册通知消息过滤器
            registerIMMessageFilter();

            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

            // 注册网络通话来电
            registerAVChatIncomingCallObserver(true);

            // 注册白板会话
            //registerRTSIncomingObserver(true);

            // 注册语言变化监听
            registerLocaleReceiver(true);

            OnlineStateEventManager.init();
        }
    }

    /**
     * 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
     * @return
     */
    private LoginInfo getLoginInfo() {
        // 从本地读取上次登录成功时保存的用户登录信息
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            DemoCache.setAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    /**
     * 如果返回值为 null，则全部使用默认参数。
     * @return
     */
    private SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();
        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        initStatusBarNotificationConfig(options);

        // 配置保存图片，文件，log等数据的目录
        // 如果不设置，则默认为“/{外卡根目录}/{app_package_name}/nim/”
        // 其中外卡根目录获取方式为 Environment.getExternalStorageDirectory().getPath()
        // 如果你的 APP 需要清除缓存功能，可扫描该目录下的文件，按照你们的规则清理即可。
        // 在 SDK 初始化完成后可以通过 NimClient#getSdkStorageDirPath 获取 SDK 数据缓存目录。
        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

        // 用户信息提供者
        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        //options.userInfoProvider = new DefaultUserInfoProvider(this);

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        // 在线多端同步未读数
        options.sessionReadAck = true;

        // 云信私有化配置项
        configServerAddress(options);

        return options;
    }

    private void configServerAddress(final SDKOptions options) {
        String appKey = PrivatizationConfig.getAppKey();
        if (!TextUtils.isEmpty(appKey)) {
            options.appKey = appKey;
        }

        ServerAddresses serverConfig = PrivatizationConfig.getServerAddresses();
        if (serverConfig != null) {
            options.serverConfig = serverConfig;
        }
    }

    /**
     * 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
     * @param options
     */
    private void initStatusBarNotificationConfig(SDKOptions options) {
        // load 应用的状态栏配置
        StatusBarNotificationConfig config = loadStatusBarNotificationConfig();

        // load 用户的 StatusBarNotificationConfig 设置项
        StatusBarNotificationConfig userConfig = UserPreferences.getStatusConfig();
        if (userConfig == null) {
            userConfig = config;
        } else {
            // 新增的 UserPreferences 存储项更新，兼容 3.4 及以前版本
            // 新增 notificationColor 存储，兼容3.6以前版本
            // APP默认 StatusBarNotificationConfig 配置修改后，使其生效
            userConfig.notificationEntrance = config.notificationEntrance;
            userConfig.notificationFolded = config.notificationFolded;
            userConfig.notificationColor = getResources().getColor(R.color.color_blue_3a9efb);
        }
        // 持久化生效
        UserPreferences.setStatusConfig(userConfig);
        // SDK statusBarNotificationConfig 生效
        options.statusBarNotificationConfig = userConfig;
    }

    /**
     * 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
     */
    private StatusBarNotificationConfig loadStatusBarNotificationConfig() {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = WelcomeActivity.class;
        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
        config.notificationColor = getResources().getColor(R.color.color_blue_3a9efb);
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;

        // save cache，留做切换账号备用
        DemoCache.setNotificationConfig(config);
        return config;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }

    /**
     * 通知消息过滤器（如果过滤则该消息不存储不上报）
     * 消息过滤

     支持单聊和群聊的通知类型消息过滤，支持音视频类型消息过滤。

     通知消息是指 IMMessage#getMsgType 为 MsgTypeEnum#notification。
     SDK 在 2.4.0 版本后支持上层指定过滤器决定是否要将通知消息，音视频消息存入 SDK 数据库（并通知上层收到该消息）。
     请注意，注册过滤器的时机，建议放在 Application 的 onCreate 中， SDK 初始化之后。

     示例：SDK 过滤群头像变更通知和过滤音视频类型消息。
     */
    private void registerIMMessageFilter() {
        // 在 Application启动时注册，保证漫游、离线消息也能够回调此过滤器进行过滤。注意，过滤器的实现不要有耗时操作。
        NIMClient.getService(MsgService.class).registerIMMessageFilter(new IMMessageFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                if (UserPreferences.getMsgIgnore() && message.getAttachment() != null) {
                    if (message.getAttachment() instanceof UpdateTeamAttachment) {
                        UpdateTeamAttachment attachment = (UpdateTeamAttachment) message.getAttachment();
                        for (Map.Entry<TeamFieldEnum, Object> field : attachment.getUpdatedFields().entrySet()) {
                            if (field.getKey() == TeamFieldEnum.ICON) {
                                return true; // 过滤
                            }
                        }
                    } else if (message.getAttachment() instanceof AVChatAttachment) {
                        return true; // 过滤
                    }
                }
                return false; // 不过滤
            }
        });
    }

    private void registerAVChatIncomingCallObserver(boolean register) {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>() {
            @Override
            public void onEvent(AVChatData data) {
                String extra = data.getExtra();
                Log.e("Extra", "Extra Message->" + extra);
                if (PhoneCallStateObserver.getInstance().getPhoneCallState() != PhoneCallStateObserver.PhoneCallStateEnum.IDLE
                        || AVChatProfile.getInstance().isAVChatting()
                        //|| TeamAVChatHelper.sharedInstance().isTeamAVChatting()
                        || AVChatManager.getInstance().getCurrentChatId() != 0) {
                    LogUtil.i(TAG, "reject incoming call data =" + data.toString() + " as local phone is not idle");
                    AVChatManager.getInstance().sendControlCommand(data.getChatId(), AVChatControlCommand.BUSY, null);
                    return;
                }
                // 有网络来电打开AVChatActivity
                AVChatProfile.getInstance().setAVChatting(true);
                AVChatProfile.getInstance().launchActivity(data, AVChatActivity.FROM_BROADCASTRECEIVER);
            }
        }, register);
    }

/*    private void registerRTSIncomingObserver(boolean register) {
        RTSManager.getInstance().observeIncomingSession(new Observer<RTSData>() {
            @Override
            public void onEvent(RTSData rtsData) {
                RTSActivity.incomingSession(DemoCache.getContext(), rtsData, RTSActivity.FROM_BROADCAST_RECEIVER);
            }
        }, register);
    }*/

    private void registerLocaleReceiver(boolean register) {
        if (register) {
            updateLocale();
            IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
            registerReceiver(localeReceiver, filter);
        } else {
            unregisterReceiver(localeReceiver);
        }
    }

    private BroadcastReceiver localeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                updateLocale();
            }
        }
    };

    private void updateLocale() {
        NimStrings strings = new NimStrings();
        strings.status_bar_multi_messages_incoming = getString(R.string.nim_status_bar_multi_messages_incoming);
        strings.status_bar_image_message = getString(R.string.nim_status_bar_image_message);
        strings.status_bar_audio_message = getString(R.string.nim_status_bar_audio_message);
        strings.status_bar_custom_message = getString(R.string.nim_status_bar_custom_message);
        strings.status_bar_file_message = getString(R.string.nim_status_bar_file_message);
        strings.status_bar_location_message = getString(R.string.nim_status_bar_location_message);
        strings.status_bar_notification_message = getString(R.string.nim_status_bar_notification_message);
        strings.status_bar_ticker_text = getString(R.string.nim_status_bar_ticker_text);
        strings.status_bar_unsupported_message = getString(R.string.nim_status_bar_unsupported_message);
        strings.status_bar_video_message = getString(R.string.nim_status_bar_video_message);
        strings.status_bar_hidden_message_content = getString(R.string.nim_status_bar_hidden_msg_content);
        NIMClient.updateStrings(strings);
    }

    /**
     * 在主进程中初始化UI组件
     * UIKit 中用到的 Activity 已经在 UIKit 工程的 AndroidManifest.xml 文件中注册好，
     * 上层 APP 无需再去添加注册。除观看视频的 WatchVideoActivity 需要用到黑色主题，因此单独定义 style 外，其他 Activity 均使用项目默认主题。
     * 开发者初始化 UIKit 之后，就可以在适当的时机调用登陆方法连接云信服务器，云信建议开发者首选自动登录，即在 SDK 初始化的时候传入登陆信息。
     * 但需要注意的是，对于非多端在线系统，用户第一次登陆或者用户登录状态被其他端踢掉之后，必须进行手动登陆才能成功。
     * 怎么知道自己被踢了？
     */
    private void initUIKit() {
        // 初始化，使用 uikit 默认的用户信息提供者
        // 除了 NimUIKit.init(this) 是必须的以外，其他均为可选配置项。
        NimUIKit.init(this);

        // 可选定制项
        // 注册定位信息提供者类（可选）,如果需要发送地理位置消息，必须提供。
        // demo中使用高德地图实现了该提供者，开发者可以根据自身需求，选用高德，百度，google等任意第三方地图和定位SDK
        //NimUIKit.setLocationProvider(new NimDemoLocationProvider());

        // 会话窗口的定制: 示例代码可详见demo源码中的SessionHelper类。
        // 1.注册自定义消息附件解析器（可选）
        // 2.注册各种扩展消息类型的显示ViewHolder（可选）
        // 3.设置会话中点击事件响应处理（一般需要）
        SessionHelper.init();

        // 通讯录列表定制：示例代码可详见demo源码中的ContactHelper类。
        // 1.定制通讯录列表中点击事响应处理（一般需要，UIKit 提供默认实现为点击进入聊天界面)
        ContactHelper.init();

        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
        // NimUIKit.CustomPushContentProvider(new DemoPushContentProvider());

        NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());

        //NimUIKit.registerMsgItemViewHolder();
    }

    /**
     * （ SDK 1.8.0 及以上版本支持）本地定制的通知栏提醒文案，目前支持配置Ticker文案（通知栏弹框条显示内容）和通知内容文案（下拉通知栏显示的通知内容），
     * SDK 会在收到消息时回调 MessageNotifierCustomization 接口， 开发者可以根据昵称和收到的消息（消息类型、会话类型、发送者、消息扩展字段等）来决定要显示的通知内容。
     * 如果上述两点都不定制(返回null)，将显示默认提醒内容：

     文本消息：文本消息内容。

     文件消息：{说话者}发来一条文件消息

     图片消息：{说话者}发来一条图片消息

     语音消息：{说话者}发来一条语音消息

     视频消息：{说话者}发来一条视频消息

     位置消息：{说话者}分享了一个地理位置

     通知消息：{说话者}: 通知消息

     提醒消息：{说话者}: 提醒消息

     自定义消息：{说话者}: 自定义消息

     除文本消息外，开发者可以通过 NimStrings 类修改这些默认提醒内容。
     */
    private MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeRevokeMsgTip(String s, IMMessage imMessage) {
            return null;
        }
    };


}

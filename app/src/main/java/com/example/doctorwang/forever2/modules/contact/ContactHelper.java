package com.example.doctorwang.forever2.modules.contact;

import android.content.Context;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactEventListener;

/**
 * 定制通讯录列表
 * UIKit联系人列表定制展示类
 * <p/>
 * Created by huangjun on 2015/9/11.
 */
public class ContactHelper {

    public static void init() {
        setContactEventListener();
    }

    /**
     *
     * 设置通讯录列表点击事件响应处理 —— 在Application初始化中设置
     * 通讯录列表提供点击事件的响应处理函数，见 ContactEventListener ：
     */
    private static void setContactEventListener() {
        NimUIKit.setContactEventListener( new ContactEventListener() {
            /**
             * 通讯录联系人项点击事件处理
             */
            @Override
            public void onItemClick(Context context, String account) {
                // 显示用户名片
                UserProfileActivity.start(context, account);
            }

            /**
             * 通讯录联系人项长按事件处理，一般弹出菜单：移除好友、添加到星标好友等
             * @param context
             * @param account 点击的联系人帐号
             */
            @Override
            public void onItemLongClick(Context context, String account) {

            }

            /**
             * 联系人头像点击相应
             * @param context
             * @param account 点击的联系人帐号
             */
            @Override
            public void onAvatarClick(Context context, String account) {
                // 显示用户名片
                UserProfileActivity.start(context, account);
            }
        });
    }

}

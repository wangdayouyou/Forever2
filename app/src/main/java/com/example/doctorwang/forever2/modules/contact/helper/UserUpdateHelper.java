package com.example.doctorwang.forever2.modules.contact.helper;

import android.widget.Toast;

import com.example.doctorwang.forever2.R;
import com.example.doctorwang.forever2.modules.avchat.DemoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzxuwen on 2015/9/17.
 */
public class UserUpdateHelper {
    private static final String TAG = UserUpdateHelper.class.getSimpleName();

    /**
     * 更新用户资料
     */
    public static void update(final UserInfoFieldEnum field, final Object value, RequestCallbackWrapper<Void> callback) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(field, value);
        update(fields, callback);
    }

    /**
     *
     * 编辑用户资料

     更新用户本人资料
     传入参数 Map<UserInfoFieldEnum, Object> 更新用户本人资料，key 为字段，value 为对应的值。
     具体字段见 UserInfoFieldEnum，包括：昵称，性别，头像 URL，签名，手机，邮箱，生日以及扩展字段等。


     SDK对部分字段进行格式校验：

     邮箱：必须为合法邮箱
     手机号：必须为合法手机号 如13588888888、+(86)-13055555555
     生日：必须为"yyyy-MM-dd"格式
     更新头像可选方案：

     先将头像图片上传至网易云通信云存储上（见 NosService ) ，上传成功后可以得到 url 。
     更新个人资料的头像字段，保存 url 。
     此外，开发者也可以自行存储头像，仅将 url 更新到个人资料上。
     * @param fields
     * @param callback
     */
    private static void update(final Map<UserInfoFieldEnum, Object> fields, final RequestCallbackWrapper<Void> callback) {
        NIMClient.getService(UserService.class).updateUserInfo(fields).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {

                if (code == ResponseCode.RES_SUCCESS) {
                    LogUtil.i(TAG, "update userInfo success, update fields count=" + fields.size());
                } else {
                    if (exception != null) {
                        Toast.makeText( DemoCache.getContext(), R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                        LogUtil.i(TAG, "update userInfo failed, exception=" + exception.getMessage());
                    }
                }
                if (callback != null) {
                    callback.onResult(code, result, exception);
                }
            }
        });
    }
}


package com.example.doctorwang.forever2.modules.avchat.activity;

import android.os.Bundle;

import com.example.doctorwang.forever2.R;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;

/**
 * Created by liuqijun on 7/19/16.
 * 注意:全局配置,不区分用户
 */
public class AVChatSettingsActivity extends UI {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.avchat_settings_layout);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.nrtc_settings;
        setToolBar(R.id.toolbar, options);


    }

}

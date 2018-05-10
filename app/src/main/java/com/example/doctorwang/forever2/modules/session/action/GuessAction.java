package com.example.doctorwang.forever2.modules.session.action;

import com.example.doctorwang.forever2.R;
import com.example.doctorwang.forever2.modules.session.GuessAttachment;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class GuessAction extends BaseAction {

    public GuessAction() {
        super( R.drawable.message_plus_guess_selector, R.string.input_panel_guess);
    }

    @Override
    public void onClick() {
        GuessAttachment attachment = new GuessAttachment();
        IMMessage message;
        if (getContainer() != null && getContainer().sessionType == SessionTypeEnum.ChatRoom) {
            // 创建文本消息
            /**
             * 发送消息

             先通过 ChatRoomMessageBuilder 提供的接口创建消息对象，然后调用 ChatRoomService 的 sendMessage 接口发送出去即可。

             聊天室消息 ChatRoomMessage 继承自 IMMessage 。新增了两个方法：分别是设置和获取聊天室消息扩展属性。

             下面给出发送文本类型消息的示例代码：

             发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
             */
            message = ChatRoomMessageBuilder.createChatRoomCustomMessage(
                    getAccount(), // 聊天室id
                    attachment); // 文本内容
        } else {
            message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), attachment.getValue().getDesc(), attachment);
        }

        sendMessage(message);
    }
}

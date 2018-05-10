package com.example.doctorwang.forever2.modules.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.example.doctorwang.forever2.R;
import com.example.doctorwang.forever2.modules.helper.SystemMessageUnreadManager;
import com.example.doctorwang.forever2.modules.model.MainTab;
import com.example.doctorwang.forever2.modules.remind.ReminderId;
import com.example.doctorwang.forever2.modules.remind.ReminderItem;
import com.example.doctorwang.forever2.modules.remind.ReminderManager;
import com.netease.nim.uikit.api.model.contact.ContactsCustomization;
import com.netease.nim.uikit.business.contact.ContactsFragment;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;
import com.netease.nim.uikit.business.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.business.contact.core.viewholder.AbsContactViewHolder;
import com.netease.nim.uikit.common.activity.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * 集成通讯录列表
 * UIKit 提供的通讯录列表默认显示所有好友，提供字母导航，支持帐号、昵称搜索等。
 * 通讯录列表默认不支持功能项（例如，折叠群、黑名单、消息验证、我的电脑等），开发者需要自己定制。默认点击响应为启
 * 动聊天窗口，若不能满足需求，开发者可以通过定制通讯录列表设置通讯录列表点击事件响应处理。
 * 通讯录列表默认显示所有好友，与最近联系人列表类似，通讯录也以 fragment 的方式提供，下面演示集成通讯录
 * 1、静态集成
 * 2、动态集成
 * <p/>
 * Created by huangjun on 2015/9/7.
 */
public class ContactListFragment extends MainTabFragment {

    private ContactsFragment fragment;

    public ContactListFragment() {
        setContainerId( MainTab.CONTACT.fragmentId);
    }

    /**
     * ******************************** 功能项定制 ***********************************
     *
     *  定制通讯录列表功能项
     *  定制通讯录列表功能项依靠 ContactsCustomization 实现，例如折叠群、黑名单、消息验证、我的电脑等。
     *  首先定义功能项 FuncItem，示例：
     */
    final static class FuncItem extends AbsContactItem {
        static final FuncItem VERIFY = new FuncItem();
        //static final FuncItem ROBOT = new FuncItem();
        //static final FuncItem NORMAL_TEAM = new FuncItem();
        //static final FuncItem ADVANCED_TEAM = new FuncItem();
        static final FuncItem BLACK_LIST = new FuncItem();
        //static final FuncItem MY_COMPUTER = new FuncItem();

        @Override
        public int getItemType() {
            return ItemTypes.FUNC;
        }

        @Override
        public String belongsGroup() {
            return null;
        }

        // 系统功能列表：验证提醒、智能机器人、讨论组、高级群、黑名单、我的电脑
        public static final class FuncViewHolder extends AbsContactViewHolder<FuncItem> {
            private ImageView image;
            private TextView funcName;
            private TextView unreadNum;

            @Override
            public View inflate(LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.func_contacts_item, null);
                this.image = (ImageView) view.findViewById(R.id.img_head);
                this.funcName = (TextView) view.findViewById(R.id.tv_func_name);
                this.unreadNum = (TextView) view.findViewById(R.id.tab_new_msg_label);
                return view;
            }

            @Override
            public void refresh(ContactDataAdapter contactAdapter, int position, FuncItem item) {
                if (item == VERIFY) {
                    funcName.setText("验证提醒");
                    image.setImageResource( R.drawable.icon_verify_remind);
                    image.setScaleType(ScaleType.FIT_XY);
                    int unreadCount = SystemMessageUnreadManager.getInstance().getSysMsgUnreadCount();
                    updateUnreadNum(unreadCount);

                    ReminderManager.getInstance().registerUnreadNumChangedCallback( new ReminderManager.UnreadNumChangedCallback() {
                        @Override
                        public void onUnreadNumChanged(ReminderItem item) {
                            if (item.getId() != ReminderId.CONTACT) {
                                return;
                            }

                            updateUnreadNum(item.getUnread());
                        }
                    });
                //} else if (item == ROBOT) {
                    //funcName.setText("智能机器人");
                    //image.setImageResource(R.drawable.ic_robot);
               // } else if (item == NORMAL_TEAM) {
                    //funcName.setText("讨论组");
                    //image.setImageResource(R.drawable.ic_secretary);
                //} else if (item == ADVANCED_TEAM) {
                    //funcName.setText("高级群");
                    //image.setImageResource(R.drawable.ic_advanced_team);
                } else if (item == BLACK_LIST) {
                    funcName.setText("黑名单");
                    image.setImageResource(R.drawable.ic_black_list);
                //} else if (item == MY_COMPUTER) {
                    //funcName.setText("我的电脑");
                   // image.setImageResource(R.drawable.ic_my_computer);
                }

                if (item != VERIFY) {
                    image.setScaleType(ScaleType.FIT_XY);
                    unreadNum.setVisibility(View.GONE);
                }
            }

            private void updateUnreadNum(int unreadCount) {
                // 2.*版本viewholder复用问题
                if (unreadCount > 0 && funcName.getText().toString().equals("验证提醒")) {
                    unreadNum.setVisibility(View.VISIBLE);
                    unreadNum.setText("" + unreadCount);
                } else {
                    unreadNum.setVisibility(View.GONE);
                }
            }
        }

        static List<AbsContactItem> provide() {
            List<AbsContactItem> items = new ArrayList<AbsContactItem>();
            items.add(VERIFY);
            //items.add(ROBOT);
            //items.add(NORMAL_TEAM);
            //items.add(ADVANCED_TEAM);
            items.add(BLACK_LIST);
            //items.add(MY_COMPUTER);

            return items;
        }

        static void handle(Context context, AbsContactItem item) {
        //    if (item == VERIFY) {
        //        SystemMessageActivity.start(context);
        //    } else if (item == ROBOT) {
        //        RobotListActivity.start(context);
        //    } else if (item == NORMAL_TEAM) {
        //        TeamListActivity.start(context, ItemTypes.TEAMS.NORMAL_TEAM);
        //    } else if (item == ADVANCED_TEAM) {
        //       TeamListActivity.start(context, ItemTypes.TEAMS.ADVANCED_TEAM);
        //    } else if (item == MY_COMPUTER) {
        //        SessionHelper.startP2PSession(context, DemoCache.getAccount());
        //    } else if (item == BLACK_LIST) {
        //        BlackListActivity.start(context);
        //    }
        }
    }


    /**
     * ******************************** 生命周期 ***********************************
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onCurrent(); // 触发onInit，提前加载
    }

    @Override
    protected void onInit() {
        addContactFragment();  // 集成通讯录页面
    }

    // 将通讯录列表fragment动态集成进来。 开发者也可以使用在xml中配置的方式静态集成。
    private void addContactFragment() {
        fragment = new ContactsFragment();
        // R.id.contact_fragment是contact_list布局文件的子元素
        fragment.setContainerId(R.id.contact_fragment);

        UI activity = (UI) getActivity();

        // 如果是activity从堆栈恢复，FM中已经存在恢复而来的fragment，此时会使用恢复来的，而new出来这个会被丢弃掉
        fragment = (ContactsFragment) activity.addFragment(fragment);

        // 定制通讯录列表功能项
        // 定制通讯录列表功能项依靠 ContactsCustomization 实现，例如折叠群、黑名单、消息验证、我的电脑等。
        // 1、首先定义功能项 FuncItem —— FuncItem定义
        // 2、添加功能项到 ContactsCustomization —— 设置ContactsCustomization

        // 通讯录列表定制，目前支持：
        // 1.在联系人列表上方加入功能项，并处理点击事件
        fragment.setContactsCustomization(new ContactsCustomization() {

            /**
             *
             * 系统功能列表项的布局
             * @return
             */
            @Override
            public Class<? extends AbsContactViewHolder<? extends AbsContactItem>> onGetFuncViewHolderClass() {
                return FuncItem.FuncViewHolder.class;
            }

            /**
             * 生成要显示的系统功能列表
             * @return
             */
            @Override
            public List<AbsContactItem> onGetFuncItems() {
                return FuncItem.provide();
            }

            /**
             * 系统功能项的点击事件
             * @param item 自定义的功能项的基类，一般可以通过"=="、"instance of"来判断对应的具体功能
             */
            @Override
            public void onFuncItemClick(AbsContactItem item) {
                FuncItem.handle(getActivity(), item);
            }
        });
    }

    @Override
    public void onCurrentTabClicked() {
        // 点击切换到当前TAB
        if (fragment != null) {
            fragment.scrollToTop();
        }
    }
}
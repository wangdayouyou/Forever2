package com.example.doctorwang.forever2.modules.model;

import com.example.doctorwang.forever2.R;
import com.example.doctorwang.forever2.modules.fragment.ContactListFragment;
import com.example.doctorwang.forever2.modules.fragment.MainTabFragment;
import com.example.doctorwang.forever2.modules.fragment.SessionListFragment;
import com.example.doctorwang.forever2.modules.remind.ReminderId;

public enum MainTab {
    RECENT_CONTACTS(0, ReminderId.SESSION, SessionListFragment.class, R.string.main_tab_session, R.layout.session_list),
    CONTACT(1, ReminderId.CONTACT, ContactListFragment.class, R.string.main_tab_contact, R.layout.contact_list);
    //CHAT_ROOM(2, ReminderId.INVALID, ChatRoomListFragment.class, R.string.chat_room, R.layout.chat_room_tab);

    public final int tabIndex;

    public final int reminderId;

    public final Class<? extends MainTabFragment> clazz;

    public final int resId;

    public final int fragmentId;

    public final int layoutId;

    MainTab(int index, int reminderId, Class<? extends MainTabFragment> clazz, int resId, int layoutId) {
        this.tabIndex = index;
        this.reminderId = reminderId;
        this.clazz = clazz;
        this.resId = resId;
        this.fragmentId = index;
        this.layoutId = layoutId;
    }

    public static final MainTab fromReminderId(int reminderId) {
        for (MainTab value : MainTab.values()) {
            if (value.reminderId == reminderId) {
                return value;
            }
        }

        return null;
    }

    public static final MainTab fromTabIndex(int tabIndex) {
        for (MainTab value : MainTab.values()) {
            if (value.tabIndex == tabIndex) {
                return value;
            }
        }

        return null;
    }
}

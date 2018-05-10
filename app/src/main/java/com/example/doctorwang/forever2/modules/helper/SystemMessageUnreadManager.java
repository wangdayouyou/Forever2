package com.example.doctorwang.forever2.modules.helper;

public class SystemMessageUnreadManager {

    private static SystemMessageUnreadManager instance = new SystemMessageUnreadManager();

    public static SystemMessageUnreadManager getInstance() {
        return instance;
    }

    private int sysMsgUnreadCount = 0;

    public int getSysMsgUnreadCount() {
        return sysMsgUnreadCount;
    }

    public synchronized void setSysMsgUnreadCount(int unreadCount) {
        this.sysMsgUnreadCount = unreadCount;
    }
}
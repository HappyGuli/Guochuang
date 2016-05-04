package main.model;

import hello.login.R;
import main.fragment.MainTabFragment;
import main.fragment.NormalTeamFragment;
import main.fragment.P2PFriendListFragment;
import main.fragment.SessionListFragment;
import main.reminder.ReminderId;

public enum MainTab {

    NORMAL_TEAM(0, ReminderId.SESSION, NormalTeamFragment.class,  R.string.normal_team_test, R.layout.group_list_activity),
    RECENT_CONTACTS(1, ReminderId.INVALID, SessionListFragment.class, R.string.main_tab_session, R.layout.session_list),
    CHAT_ROOM(2, ReminderId.INVALID, P2PFriendListFragment.class, R.string.p2p_friend, R.layout.p2p_friend_list);

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

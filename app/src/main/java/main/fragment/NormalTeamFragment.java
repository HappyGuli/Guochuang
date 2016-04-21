package main.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.contact.core.item.ContactItem;
import com.netease.nim.uikit.contact.core.item.ItemTypes;
import com.netease.nim.uikit.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.contact.core.model.ContactGroupStrategy;
import com.netease.nim.uikit.contact.core.provider.ContactDataProvider;
import com.netease.nim.uikit.contact.core.query.IContactDataProvider;
import com.netease.nim.uikit.contact.core.viewholder.ContactHolder;
import com.netease.nim.uikit.contact.core.viewholder.LabelHolder;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

import hello.login.R;
import main.model.MainTab;
import session.SessionHelper;

/**
 * 群列表(通讯录)
 * <p/>
 * Created by huangjun on 2015/4/21.
 */
public class NormalTeamFragment extends MainTabFragment implements AdapterView.OnItemClickListener {

    private static final String EXTRA_DATA_ITEM_TYPES = "EXTRA_DATA_ITEM_TYPES";

    private ContactDataAdapter adapter;

    private ListView lvContacts;

    public NormalTeamFragment() {
        setContainerId(MainTab.NORMAL_TEAM.fragmentId);
    }

    private int itemType;



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

        //初始化 界面
        findViews();

    }



    private void findViews(){




        //区别 高级群 和 normal team
        itemType = ItemTypes.TEAMS.ADVANCED_TEAM;

        //找到展示的listview
        lvContacts = (ListView) getView().findViewById(R.id.group_list);

        GroupStrategy groupStrategy = new GroupStrategy();
        IContactDataProvider dataProvider = new ContactDataProvider(itemType);

        //这个是否有问题
        adapter = new ContactDataAdapter(getActivity(), groupStrategy, dataProvider) {
            @Override
            protected List<AbsContactItem> onNonDataItems() {
                return null;
            }

            @Override
            protected void onPreReady() {
            }

            @Override
            protected void onPostLoad(boolean empty, String queryText, boolean all) {
            }
        };
        adapter.addViewHolder(ItemTypes.LABEL, LabelHolder.class);
        adapter.addViewHolder(ItemTypes.TEAM, ContactHolder.class);

        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(this);
        lvContacts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                showKeyboard(false);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        // load data
        int count = NIMClient.getService(TeamService.class).queryTeamCountByTypeBlock(itemType == ItemTypes.TEAMS
                .ADVANCED_TEAM ? TeamTypeEnum.Advanced : TeamTypeEnum.Normal);

        if (count == 0) {
            if (itemType == ItemTypes.TEAMS.ADVANCED_TEAM) {

                //给出提示

                Toast.makeText(getActivity(),"你还没有高级群了",Toast.LENGTH_SHORT).show();

                Log.e("TTTT"," 你还没有高级群了");

            } else if (itemType == ItemTypes.TEAMS.NORMAL_TEAM) {

                Toast.makeText(getActivity(),"你还没有讨论组呢",Toast.LENGTH_SHORT).show();

                //给出提示
                Log.e("TTTT"," 你还没有讨论组呢");

            }
        }



        adapter.load(true);

        registerTeamUpdateObserver(true);

    }


    //在销毁的时候  解除监听
    @Override
    public void onDestroy() {
        super.onDestroy();
        registerTeamUpdateObserver(false);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AbsContactItem item = (AbsContactItem) adapter.getItem(position);
        switch (item.getItemType()) {
            case ItemTypes.TEAM:

                /***被谷力注释了 ***/
                //进入 特定的群 进行聊天
                SessionHelper.startTeamSession(getActivity(), ((ContactItem) item).getContact().getContactId());
                break;
        }
    }

    //注册监听
    private void registerTeamUpdateObserver(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataChangedObserver);
        } else {
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataChangedObserver);
        }
    }



    TeamDataCache.TeamDataChangedObserver teamDataChangedObserver = new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            adapter.load(true);
        }

        @Override
        public void onRemoveTeam(Team team) {
            adapter.load(true);
        }
    };


    private static class GroupStrategy extends ContactGroupStrategy {
        GroupStrategy() {
            add(ContactGroupStrategy.GROUP_NULL, 0, ""); // 默认分组
        }

        @Override
        public String belongs(AbsContactItem item) {
            switch (item.getItemType()) {
                case ItemTypes.TEAM:
                    return GROUP_NULL;
                default:
                    return null;
            }
        }
    }

}

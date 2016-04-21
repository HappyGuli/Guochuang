package main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hello.login.R;
import com.netease.nim.uikit.common.fragment.TabFragment;

import main.model.MainTab;


public abstract class MainTabFragment extends TabFragment {

    private boolean loaded = false;

    private MainTab tabData;

    /**
     * 由继承类来实现   但是是在 onCurrent中进行调用的  by  guli
     */
    protected abstract void onInit();

    protected boolean inited() {
        return loaded;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_tab_fragment_container, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /**
     * 设置 tabData  先设置这个  然后 调用 loadRealLayout函数   by guli
     * @param tabData
     */
    public void attachTabData(MainTab tabData) {
        this.tabData = tabData;
    }


    @Override
    public void onCurrent() {
        super.onCurrent();
        
        if (!loaded && loadRealLayout()) {
            loaded = true;
            onInit();
        }
    }


    /**
     * 加载真的布局文件   by guli
     * @return
     */
    private boolean loadRealLayout() {
        ViewGroup root = (ViewGroup) getView();
        if (root != null) {
            root.removeAllViewsInLayout();
            View.inflate(root.getContext(), tabData.layoutId, root);
        }
        return root != null;
    }
}

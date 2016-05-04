package course.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nim.uikit.common.activity.TActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.cqu.DemoCache;
import org.cqu.preference.Preferences;

import hello.login.R;
import util.sys.SysInfoUtil;

import java.util.ArrayList;


public class WelcomeActivity extends TActivity {

    private static final String TAG = "WelcomeActivity";

    private boolean customSplash = false;

    private static boolean firstEnter = true; // 是否首次进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }

        showSplashView();
    }


    @Override
    protected void onResume() {
        super.onResume();

            //firstEnter = false;

            //跳转到登录界面
            Intent intent = new Intent();
            intent.setClass(this, CourseLoginActivity.class);
            this.startActivity(intent);

    }


    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }


    /**
     * 首次进入，打开欢迎界面
     */
    private void showSplashView() {
        getWindow().setBackgroundDrawableResource(R.drawable.splash_bg);
        customSplash = true;
    }


}

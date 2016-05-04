package course.activity;

import hello.login.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.cqu.DemoCache;
import org.cqu.preference.Preferences;
import org.cqu.preference.UserPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import api.ApiClient;
import course.bean.Course;
import course.bean.CourseJsonBean;
import course.bean.HelpRecyDataBean;
import course.util.DoGet;
import course.util.IsConnectedToNet;
import widget.AppContext;
import widget.AppException;


public class CourseMainActivity extends ActionBarActivity {

    //左边菜单
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    //和 viewPager相关
    private ViewPager viewPager;//页卡内容
    private ImageView imageView;// 动画图片
    private TextView textView1, textView2;
    private List<View> views;// Tab页面列表
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;         // 动画图片宽度
    private View view1, view2;  //各个页卡
    private List<TextView> textviews;// 保存代表每个view的textview


    //和menu相关的东西
    private String[] menuTitles;


    //和获取课表相关的东西
    private int colors[] = {
            Color.rgb(0x81, 0xC7, 0x84),
            Color.rgb(0xE5, 0x73, 0x73),
            Color.rgb(0x4F, 0xC3, 0xF7),
            Color.rgb(0xFF, 0xCA, 0x28),
            Color.rgb(0x00, 0xBC, 0xD4),
            Color.rgb(0xFF, 0x8A, 0x65),

    };


    private int currColor = 0;
    private LinearLayout ll1, ll2, ll3, ll4, ll5, ll6, ll7;    //分别代表 星期一到星期日
    private int i1, i2, i3, i4, i5, i6, i7;

    //从教务网爬取到的 course数组
    private List<Course> courses;
    private List<CourseJsonBean>  jsonCourses; //用来上传用户的课表信息


    private int currWeek; //当前周数
    //spinner用的数组
    private String[] weeks = {"第1周", "第2周", "第3周", "第4周", "第5周", "第6周", "第7周", "第8周"
            , "第9周", "第10周", "第11周", "第12周", "第13周", "第14周", "第15周", "第16周", "第17周", "第18周", "第19周"};




    //和同学帮相关的东西
    private RecyclerView recyclerView;
    private List<HelpRecyDataBean> mDatas;
    private List uniqCourseName;


    //和drawerLayout相关的菜单
    private LinearLayout ll_relate_to_me, ll_favorite, ll_my_questions, ll_my_answers, ll_setting;


    //从intent处获取到的数据
    private String str_password,str_sid,str_school,str_role;
    private Intent intent;

    //通过sharedPreference保存从intent中获取到的数据
    private  SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String str_name;



    //和登录到网易云信相关的东西
    private String account;
    private String accid;
    private String name;
    private String token;
    private String sb_result;
    private AbortableFuture<LoginInfo> loginRequest;



    //handler 的使用  在从教务网中获取课表数据之后的工作
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                setClassByWeek(currWeek);
                //保存重要数据到sharePreference
                saveToSharePreference();

                new Thread(new Runnable() {

                    Message message = new Message();
                    @Override
                    public void run() {

                        try{

                            //获取用户手机的id
                            String str_phoneid = getPhoneMac();
                            Log.e("TTTT",str_phoneid);

                            ApiClient.saveUserInfo(appContext,str_sid,str_name,str_password,str_phoneid,str_role,"第一级","00000002");

                            //登录到网易云信
                            loginToWYYX();
                            //保存用户信息成功
                            message.what = 1;

                        }catch (Exception e){
                            e.printStackTrace();
                            //保存用户信息失败
                            message.what = 0;

                        }
                        mHandler1.sendMessage(message);
                    }
                }).start();

            } else if (msg.what == 0) {
                Toast.makeText(CourseMainActivity.this,"系统出现错误",Toast.LENGTH_SHORT).show();
            }
        }
    };



    //处理上传用户信息之后的工作
    public Handler mHandler1 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //Toast.makeText(CourseMainActivity.this,"保存用户信息成功",Toast.LENGTH_SHORT).show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                JSONArray json = new JSONArray();
                                for (CourseJsonBean courseJson : jsonCourses) {
                                    JSONObject jo = new JSONObject();
                                    jo.put("cname", courseJson.getCourseName());
                                    jo.put("cid", courseJson.getCid());
                                    jo.put("sid", courseJson.getSid());
                                    jo.put("tname", courseJson.getTname());
                                    json.put(jo);
                                }

                                //上传到服务器
                                ApiClient.saveUserCourseTable(appContext, json.toString());


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(CourseMainActivity.this, "Json 解析封装错误", Toast.LENGTH_SHORT).show();


                            }
                            catch (AppException e){
                                e.printStackTrace();
                                Toast.makeText(CourseMainActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(CourseMainActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();


            } else if (msg.what == 0) {
                Toast.makeText(CourseMainActivity.this,"保存课表信息失败",Toast.LENGTH_SHORT).show();

            }
        }
    };



    /*************************处理登录线程完成之后的事情***********************/

    Handler loginHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                // 登录
                loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(accid, token));
                loginRequest.setCallback(new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {


                        onLoginDone();
                        DemoCache.setAccount(accid);
                        saveLoginInfo(accid, token);

                        // 初始化消息提醒
                        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

                        // 初始化免打扰
                        NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                        // 构建缓存
                        DataCacheManager.buildDataCacheAsync();

                        // 进入主界面

                        Toast.makeText(CourseMainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailed(int code) {

                        if (code == 302 || code == 404) {
                            Toast.makeText(CourseMainActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CourseMainActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        onLoginDone();
                    }
                });


            } else  if(msg.what == -1){

                Toast.makeText(CourseMainActivity.this, "网易云信服务器出错", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /******处理登录到网易云信端*********/
    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    /******处理登录到网易云信端*********/
    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }


    //应用上下文
    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course_activity_main);

        //初始化appContext
        appContext = new AppContext(getApplication());

        //从intent 获取数据
        getDataFromIntent();

        initView();
        initDrawerLayout();

        //设置初始化移动的image的地址
        InitImageView();
        InitTextView();
        InitViewPager();



        if(IsConnectedToNet.isNetworkAvailable(CourseMainActivity.this)){
            //获取课表
            getCourse();
        }else{
            //提出提示
            Toast.makeText(CourseMainActivity.this,"网络连接出现问题",Toast.LENGTH_SHORT).show();

        }

        //根据第几周来展示课程
        currWeek = 1;


        //为recycleView做准备
        uniqCourseName = new ArrayList<String>();
        mDatas = new ArrayList<HelpRecyDataBean>();
        //最后处理 recyclerView

        recyclerView = (RecyclerView) view2.findViewById(R.id.recyclerView);

    }


    private void initDrawerLayout() {
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R
                .string.open, R.string.close);

        //同步
        mToggle.syncState();
        //mDrawerLayout.setDrawerListener(mToggle);
        mDrawerLayout.setDrawerListener(new MyOnDrawerListener());

    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);

        //将 drawerLayout中的 LinearLayout全部找出来
        ll_relate_to_me = (LinearLayout) mDrawerLayout.findViewById(R.id.ll_relate_to_me);
        ll_favorite = (LinearLayout) mDrawerLayout.findViewById(R.id.ll_favorite);
        ll_my_questions = (LinearLayout) mDrawerLayout.findViewById(R.id.ll_my_questions);
        ll_my_answers = (LinearLayout) mDrawerLayout.findViewById(R.id.ll_my_answers);
        ll_setting = (LinearLayout) mDrawerLayout.findViewById(R.id.ll_setting);

        //设置监听事件
        ll_relate_to_me.setOnClickListener(new MyOnDraweritemListener());
        ll_favorite.setOnClickListener(new MyOnDraweritemListener());
        ll_my_questions.setOnClickListener(new MyOnDraweritemListener());
        ll_my_answers.setOnClickListener(new MyOnDraweritemListener());
        ll_setting.setOnClickListener(new MyOnDraweritemListener());

    }


    /*****************登录到网易云信****这个必须得放在线程当中 因为使用到了网络****************************/
    private void loginToWYYX(){

        //从sharedPreferences 中获取学号信息
        sharedPreferences = getSharedPreferences("info", MODE_WORLD_READABLE);
        account = sharedPreferences.getString("str_sid", "20134942");

        //获取token成功
        Message msg = new Message();
        //捕捉异常
        try{
            sb_result = AppContext.getRegisterToken(appContext, account);

        }catch (AppException e){
            msg.what = -1;
            loginHandler.sendMessage(msg);
        }

        String[] results = sb_result.split("_");

        token = results[0];
        name = results[1];
        accid = results[2];


        if(token!=null) {
            msg.what = 1;
            loginHandler.sendMessage(msg);
        }else{
            msg.what = -1;
            loginHandler.sendMessage(msg);
        }

    }

    /**************从intent中获取数据***************/
    public void getDataFromIntent(){

        //从上个activity中获取到数据
        intent = getIntent();
        if(intent!=null){

            Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
            str_sid = bundle.getString("str_sid");
            str_school = bundle.getString("str_school");
            str_password= bundle.getString("str_password");
            str_role= bundle.getString("str_role");

        }else{
            //给出提示
            Toast.makeText(CourseMainActivity.this,"intent is null",Toast.LENGTH_SHORT).show();
        }
    }


    /***********保存数据到sharedPreference***********************************/
    public void saveToSharePreference(){
        sharedPreferences = getSharedPreferences("info",MODE_WORLD_READABLE);
        editor = sharedPreferences.edit();

        editor.putString("str_sid",str_sid);
        editor.putString("str_role", str_role);
        editor.putString("str_password", str_password);
        editor.putString("str_school", str_school);
        editor.putString("str_name", str_name);

        //提交修改
        editor.commit();
    }


    /*************获取用户手机的mac地址***************************************/
    public String getPhoneMac(){

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac =  info.getMacAddress();

        StringBuffer stringBuffer = new StringBuffer();
        String[] macs = mac.split(":");
        for(int i=0;i<macs.length;i++){
            stringBuffer.append(macs[i]);
        }

        //return "test";
       return stringBuffer.toString();
    }


    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //和recyclerView相关的东西
    private void initListAdapterV() {
        //// TODO: 16/5/2


        //设置layoutManager
        LinearLayoutManager layoutManger = new LinearLayoutManager(CourseMainActivity.this);
        recyclerView.setLayoutManager(layoutManger);

        //设置动画效果
//        recyclerView.setItemAnimator(new SlideInLeftAnimator());
//        recyclerView.getItemAnimator().setAddDuration(3000);
//        recyclerView.getItemAnimator().setRemoveDuration(1000);
//        recyclerView.getItemAnimator().setMoveDuration(1000);
//        recyclerView.getItemAnimator().setChangeDuration(1000);


        //设置adapter
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(CourseMainActivity.this, mDatas);
        //设置监听

        recyclerView.setAdapter(adapter);
    }


    /**
     * Created by happypaul on 16/1/20.
     */
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyHolder> {

        private List<HelpRecyDataBean> datas;
        private Context context;


        public MyRecyclerViewAdapter(Context content, List<HelpRecyDataBean> datas) {
            this.context = content;
            this.datas = datas;

        }

        /**
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = View.inflate(context, R.layout.recycleview_item_list_help, null);
            return new MyHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyHolder myHolder, int position) {
            myHolder.setDataAndRefreshUI(datas.get(position));

        }

        @Override
        public int getItemCount() {
            return datas.size();

        }


        public class MyHolder extends RecyclerView.ViewHolder {
            //孩子对象
            private TextView mTvName;
            private ImageView mIvIcon;
            private CardView cardView;

            public MyHolder(View itemView) {
                super(itemView);
                //初始化孩子对象
                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                mIvIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
                cardView = (CardView) itemView.findViewById(R.id.card_view_help);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //跳转到具体课程的界面
                        Intent intent = new Intent(context,SpecificCourseHelpActivity.class);
                        intent.putExtra("courseId", datas.get(getPosition()).getCourseId());
                        intent.putExtra("courseName", datas.get(getPosition()).getCourseTitle());
                        context.startActivity(intent);

                    }
                });


            }

            /**
             * 设置itemView的数据展示
             *
             * @param dataBean
             */
            public void setDataAndRefreshUI(HelpRecyDataBean dataBean) {
                mTvName.setText(dataBean.getCourseTitle());
                mIvIcon.setImageResource(dataBean.getCourseIcon());
            }
        }
    }


    //和recyclerView相关的东西结束
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////



    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////

    /**
     * 和 ViewPager相关的东西
     */
    private void InitViewPager() {
        viewPager = (ViewPager) findViewById(R.id.vPager);
        views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.viewhold_crstble, null);
        view2 = inflater.inflate(R.layout.viewhold_help, null);
        views.add(view1);

        //将获取到的课表信息展示到这个页面当中
        ll1 = (LinearLayout) view1.findViewById(R.id.ll1);
        ll2 = (LinearLayout) view1.findViewById(R.id.ll2);
        ll3 = (LinearLayout) view1.findViewById(R.id.ll3);
        ll4 = (LinearLayout) view1.findViewById(R.id.ll4);
        ll5 = (LinearLayout) view1.findViewById(R.id.ll5);
        ll6 = (LinearLayout) view1.findViewById(R.id.ll6);
        ll7 = (LinearLayout) view1.findViewById(R.id.ll7);
        views.add(view2);


        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        textviews.get(0).setAlpha(1);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }


    /**
     * drawerLayout 中item的监听函数 非常重要
     */
    private class MyOnDraweritemListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.ll_relate_to_me:
                    Toast.makeText(CourseMainActivity.this, "你点击了Relate_to_me", Toast.LENGTH_LONG).show();
                    //跳转到与我相关的界面
                    Intent intent = new Intent(CourseMainActivity.this, RelateToMeActivity.class);
                    CourseMainActivity.this.startActivity(intent);
                    break;

                case R.id.ll_favorite:
                    Toast.makeText(CourseMainActivity.this, "你点击了My_favorite", Toast.LENGTH_LONG).show();
                    //跳转到与我相关的界面
                    Intent i = new Intent(CourseMainActivity.this, MyCollectedActivity.class);
                    CourseMainActivity.this.startActivity(i);

                    break;
                case R.id.ll_my_answers:
                    Toast.makeText(CourseMainActivity.this, "你点击了My_answers", Toast.LENGTH_LONG).show();
                    //跳转到与我相关的界面
                    Intent ii = new Intent(CourseMainActivity.this, MyAnswerActivity.class);
                    CourseMainActivity.this.startActivity(ii);
                    break;

                case R.id.ll_my_questions:
                    Toast.makeText(CourseMainActivity.this, "你点击了My_questions", Toast.LENGTH_LONG).show();
                    //跳转到与我相关的界面
                    Intent iii = new Intent(CourseMainActivity.this, MyQuestionActivity.class);
                    CourseMainActivity.this.startActivity(iii);
                    break;

                case R.id.ll_setting:
                    Toast.makeText(CourseMainActivity.this, "你点击了Setting", Toast.LENGTH_LONG).show();
                    //跳转到与我相关的界面
                    Intent iii1 = new Intent(CourseMainActivity.this, SettingActivity.class);
                    CourseMainActivity.this.startActivity(iii1);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * drawerLayout 的listener
     */
    private class MyOnDrawerListener implements DrawerLayout.DrawerListener {

        /**
         * Drawer的回调方法，需要在该方法中对Toggle做对应的操作
         */
        @Override
        public void onDrawerOpened(View drawerView) {// 打开drawer
            mToggle.onDrawerOpened(drawerView);//需要把开关也变为打开
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerClosed(View drawerView) {// 关闭drawer
            mToggle.onDrawerClosed(drawerView);//需要把开关也变为关闭
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {// drawer滑动的回调
            mToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {// drawer状态改变的回调
            mToggle.onDrawerStateChanged(newState);
        }

        /**
         * DrawerMenu的回调方法，需要在该方法中添加对应的Framgment
         */
        //@Override
        public void onDrawerItemSelected(int position) {
           /* mDrawerLayout.closeDrawer(GravityCompat.START);
            FragmentManager fragmentManager = getFragmentManager();
            BaseFragment fragment = FragmentFactory.createFragment(position);
            mCurrentFragment = fragment;
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();*/
        }


    }


    /**
     * 初始化头标
     */

    private void InitTextView() {
        textView1 = (TextView) mToolbar.findViewById(R.id.tool_bar_crstbl);
        textView2 = (TextView) mToolbar.findViewById(R.id.tool_bar_help);
        textviews = new ArrayList<TextView>(); //初始化
        textviews.add(textView1);
        textviews.add(textView2);

        textView1.setOnClickListener(new MyOnClickListener(0));
        textView2.setOnClickListener(new MyOnClickListener(1));
    }


    /**
     * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据
     */

    private void InitImageView() {
        imageView = (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.cursor).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 2 - bmpW - 20);// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        imageView.setImageMatrix(matrix);// 设置动画初始位置
    }


    /**
     * 头标点击监听 3
     */
    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            textviews.get(index).setAlpha(1);
            textviews.get((index + 1) % 2).setAlpha(0.6f);
            viewPager.setCurrentItem(index);
        }

    }


    /**
     * ViewPagerDaapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    //设计动画
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = 90 + bmpW;// 页卡1 -> 页卡2 偏移量

        public void onPageScrollStateChanged(int arg0) {


        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {


        }

        public void onPageSelected(int arg0) {

            //设置字体灰度值
            textviews.get(arg0).setAlpha(1);
            textviews.get((arg0 + 1) % 2).setAlpha(0.6f);

            //设置同学帮界面
            initListAdapterV();

            Animation animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);//显然这个比较简洁，只有一行代码。
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);
        }

    }
    //ViewPager相关的东西  结束
    //////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////

    /**
     * 和menu相关的东西
     *
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        //先清空
        menu.clear();
        //当前是课表页面
        //初始化
        menuTitles = this.getResources().getStringArray(R.array.menu_crstbl);

        for (String name : menuTitles) {
            MenuItem menuItem = menu.add(name);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final MenuItem item) {
                    Log.e("menu", (String) item.getTitle());

                    if (item.getTitle().equals("显示课表")) {
                        Log.e("menu", "show course tables!");

                        if(courses.size()>0){
                            //显示课表
                            setClassByWeek(currWeek);
                        }else{
                            if(IsConnectedToNet.isNetworkAvailable(CourseMainActivity.this)){
                                try{
                                    getCourse();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                //显示课表
                                setClassByWeek(currWeek);
                            }else{
                                //给出提示
                                Toast.makeText(CourseMainActivity.this,"网络连接出现问题",Toast.LENGTH_SHORT).show();

                            }

                        }
                        return true;
                    }


                    if (((String) item.getTitle()).contains("选择周数")) {
                        Log.e("menu", "select week");

                        AlertDialog.Builder builder = new AlertDialog.Builder(CourseMainActivity.this);
                        builder.setTitle("请选择周数");
                        //items使用全局的finalCharSequenece数组声明
                        builder.setItems(weeks, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                                //设置周数
                                currWeek = which + 1;
                                item.setTitle(item.getTitleCondensed().toString().split("\\(")[0] + "(" + currWeek + ")");

                                if(courses.size()>0){
                                    //显示课表
                                    setClassByWeek(currWeek);
                                }else{
                                    if(IsConnectedToNet.isNetworkAvailable(CourseMainActivity.this)){
                                        try{
                                            getCourse();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        //显示课表
                                        setClassByWeek(currWeek);
                                    }else{
                                        //给出提示
                                        Toast.makeText(CourseMainActivity.this,"网络连接出现问题",Toast.LENGTH_SHORT).show();

                                    }

                                }

                            }
                        });

                        builder.show();

                        return true;
                    }

                    if (item.getTitle().equals("刷新课表")) {
                        if(courses.size()>0){
                            //显示课表
                            setClassByWeek(currWeek);
                        }else{
                            if(IsConnectedToNet.isNetworkAvailable(CourseMainActivity.this)){
                                try{
                                    getCourse();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                //显示课表
                                setClassByWeek(currWeek);
                            }else{
                                //给出提示
                                Toast.makeText(CourseMainActivity.this,"网络连接出现问题",Toast.LENGTH_SHORT).show();

                            }

                        }
                        return true;
                    }
                    if (item.getTitle().equals("退出")) {
                        Log.e("menu", "quit");
                        finish();
                        return true;
                    }

                    return false;
                }
            });
        }

        return true;
    }

    //和menu相关的东西结束
    //////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    //和课表相关的东西


    //获取课表
    public void getCourse() {
        courses = new ArrayList<Course>();
        jsonCourses = new ArrayList<CourseJsonBean>();

            new Thread(new Runnable() {
                    public void run() {
                        Message msg= new Message();
                        try{
                            //获取到课表信息
                            try {
                                String cookies = DoGet.getCookies(str_sid, str_password);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            System.out.println("获取身份证成功");


                            //获取html成功
                            String html = DoGet.getCourseTableHtml();
                            List<List<String>> tables = DoGet.getCourseTableFromHtml(html);
                            courses = DoGet.getCourseDaoFromTables(tables);

                            for (Course course : courses) {

                                if (!uniqCourseName.contains(course.getCourseName())) {
                                    if (!course.getCourseName().endsWith("课程设计") && !course.getCourseName().endsWith("实习")
                                            && !course.getCourseName().endsWith("test")) {
                                        Log.e("TTTT", course.getCourseName());
                                        //如果不存在 就放入到uniqCourseName中去
                                        uniqCourseName.add(course.getCourseName());
                                        HelpRecyDataBean tem = new HelpRecyDataBean();
                                        tem.setCourseIcon(R.mipmap.listview_interhelp_item);
                                        tem.setCourseTitle(course.getCourseName());
                                        tem.setCourseId(course.getCourseNumber());
                                        mDatas.add(tem);


                                        //用来保存   需要上传课表信息的
                                        CourseJsonBean tem_courseJson  = new CourseJsonBean();
                                        tem_courseJson.setCid(course.getCourseNumber());
                                        tem_courseJson.setCourseName(course.getCourseName());
                                        tem_courseJson.setSid(str_sid);
                                        tem_courseJson.setTname(course.getCourseTeacher());
                                        jsonCourses.add(tem_courseJson);

                                    }
                                }
                            }


                            //保存用户的课表信息
                            //先讲mDatas转换成为 JSON  string
                            //net.sf.json.JSONArray jsonArray = new net.sf.json.JSONArray.fromObject(courses);;
                            //Log.e("TTTT",jsonArray.toString());

                            //获取数据成功   并且
                            msg.what=1;
                            mHandler.sendMessage(msg);


                        }catch (Exception e){
                            msg.what=0;
                            mHandler.sendMessage(msg);
                            e.printStackTrace();
                        }
                }
            }).start();

    }


    //展示课表

    /**
     * 展示第几周的课表
     *
     * @param week
     */
    public void setClassByWeek(int week) {
        TextView view1 = new TextView(this);
        view1.setHeight((int) this.getResources().getDimension(R.dimen.xinqi_height));
        view1.setGravity(Gravity.CENTER);
        view1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view1.setTextSize(10);
        view1.setText("周一");

        TextView view2 = new TextView(this);
        view2.setHeight((int) this.getResources().getDimension(R.dimen.xinqi_height));
        view2.setGravity(Gravity.CENTER);
        view2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view2.setTextSize(10);
        view2.setText("周二");

        TextView view3 = new TextView(this);
        view3.setHeight((int) this.getResources().getDimension(R.dimen.xinqi_height));
        view3.setGravity(Gravity.CENTER);
        view3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view3.setTextSize(10);
        view3.setText("周三");

        TextView view4 = new TextView(this);
        view4.setHeight((int) this.getResources().getDimension(R.dimen.xinqi_height));
        view4.setGravity(Gravity.CENTER);
        view4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view4.setTextSize(10);
        view4.setText("周四");

        TextView view5 = new TextView(this);
        view5.setHeight((int) this.getResources().getDimension(R.dimen.xinqi_height));
        view5.setGravity(Gravity.CENTER);
        view5.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view5.setTextSize(10);
        view5.setText("周五");

        TextView view6 = new TextView(this);
        view6.setHeight((int) this.getResources().getDimension(R.dimen.xinqi_height));
        view6.setGravity(Gravity.CENTER);
        view6.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view6.setTextSize(10);
        view6.setText("周六");

        TextView view7 = new TextView(this);
        view7.setHeight((int) this.getResources().getDimension(R.dimen.xinqi_height));
        view7.setGravity(Gravity.CENTER);
        view7.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        view7.setTextSize(10);
        view7.setText("周日");

        ll1.removeAllViews();
        ll2.removeAllViews();
        ll3.removeAllViews();
        ll4.removeAllViews();
        ll5.removeAllViews();
        ll6.removeAllViews();
        ll7.removeAllViews();

        ll1.addView(view1);
        ll2.addView(view2);
        ll3.addView(view3);
        ll4.addView(view4);
        ll5.addView(view5);
        ll6.addView(view6);
        ll7.addView(view7);


        i1 = i2 = i3 = i4 = i5 = i6 = i7 = 0;

        char a = (char) ('a' + week - 1);


        String str = String.valueOf(a);

        for (Course course : courses) {

            System.out.println("遍历courses");

            //获取学生的姓名
            if(course.getCourseName().equals("test")){

                str_name = course.getCourseTeacher();
                ((TextView)findViewById(R.id.tv_user_name)).setText(str_name);
            }else if (getSpecificWeekFromString(course.getCourseWeeks()).contains(str)) {
                //遍历整个课表

                LinearLayout ll = null;
                //获取到 四[3-4]中的 3-4
                String tem = course.getCourseTime().split("\\[")[1].split("\\]")[0];
                System.out.println(tem);
                int startClass = Integer.parseInt(tem.split("-")[0]);
                int endClass = Integer.parseInt(tem.split("-")[1].split("节")[0]);
                int lastClass = endClass - startClass + 1;


                int c = new Random().nextInt(6) + 1;

                //根据是星期几 来设置数据
                if ("一".equals(course.getCourseTime().split("\\[")[0])) {
                    ll = ll1;
                    //i1=i1+lastClass;
                }
                //注意 这里的[是正则表达式里面的特殊的符号
                if ("二".equals(course.getCourseTime().split("\\[")[0])) {
                    //i2=i2+lastClass;
                    ll = ll2;
                }
                if ("三".equals(course.getCourseTime().split("\\[")[0])) {
                    //i3=i3+lastClass;
                    ll = ll3;
                }
                if ("四".equals(course.getCourseTime().split("\\[")[0])) {
                    ll = ll4;
                    //i4=i4+lastClass;
                }
                if ("五".equals(course.getCourseTime().split("\\[")[0])) {
                    ll = ll5;
                }
                if ("六".equals(course.getCourseTime().split("\\[")[0])) {
                    ll = ll6;

                }
                if ("日".equals(course.getCourseTime().split("\\[")[0])) {
                    ll = ll7;

                }
                setClass(ll, course, startClass, lastClass, c);
            }
        }


    }

    /*
     * @param week 表示输入的string 如 （1，4-12）
     * 将其转变成为 adefghijk
     */
    String getSpecificWeekFromString(String week) {
        StringBuffer detailWeek = new StringBuffer("");
        String[] weeks = week.split(",");
        for (String w : weeks) {
            if (w.contains("-")) {
                String[] ws = w.split("-");
                int startWn = Integer.parseInt(ws[0]);
                int endWn = Integer.parseInt(ws[1]);
                for (int i = startWn; i <= endWn; i++) {
                    char a = (char) ('a' + i - 1);
                    detailWeek.append(a);
                }
            } else {
                int wn = Integer.parseInt(w);
                char a = (char) ('a' + wn - 1);
                detailWeek.append(a);
            }
        }

        return detailWeek.toString();
    }


    /**
     * 设置课程的方法
     *
     * @param ll
      * @param lastclasses 课的连续几节
     * @param color       背景色
     */
    void setClass(LinearLayout ll, Course course,int startclasse, int lastclasses, int color) {
        int i = 0;
        if (ll == ll1) {
            i = i1;
            i1 = i1 + lastclasses;
        }
        if (ll == ll2) {
            i = i2;
            i2 = i2 + lastclasses;
        }
        if (ll == ll3) {
            i = i3;
            i3 = i3 + lastclasses;
        }
        if (ll == ll4) {
            i = i4;
            i4 = i4 + lastclasses;
        }
        if (ll == ll5) {
            i = i5;
            i5 = i5 + lastclasses;
        }
        if (ll == ll6) {
            i = i6;
            i6 = i6 + lastclasses;
        }
        if (ll == ll7) {
            i = i7;
            i7 = i7 + lastclasses;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.item_course, null);
        view.setMinimumHeight(dip2px(this, lastclasses * 51));
        view.setY(dip2px(this, (startclasse - 1) * 51 - i * 51));
        view.setBackgroundColor(colors[++currColor % 6]);
        ((TextView) view.findViewById(R.id.title)).setText(course.getCourseName());
        ((TextView) view.findViewById(R.id.place)).setText(course.getCourseRoom());
        ((TextView) view.findViewById(R.id.teacher)).setText(course.getCourseTeacher());
        //为课程View设置点击的监听器
        view.setOnClickListener(new OnClickClassListener(course));
        ll.addView(view);
    }


    //点击课程的监听器
    class OnClickClassListener implements View.OnClickListener {

        private Course course;

        public OnClickClassListener(Course course){

            this.course = course;
        }

        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (!TextUtils.isEmpty(course.getCourseName())) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                //存放要传输的数据
                Bundle bundle = new Bundle();
                bundle.putString("title", course.getCourseName()); //传入字串
                bundle.putString("credit", course.getCourseCredit()); //传入字串
                bundle.putString("room", course.getCourseRoom()); //传入字串
                bundle.putString("last", course.getCourseTime()); //传入字串
                bundle.putString("name", course.getCourseName()); //传入字串
                bundle.putString("teacher", course.getCourseTeacher()); //传入字串
                bundle.putString("weeks", course.getCourseWeeks()); //传入字串
                bundle.putString("cid", course.getCourseNumber()); //传入字串
                bundle.putString("str_sid",str_sid); //传入字串

                intent.putExtras(bundle);
                intent.setClass(CourseMainActivity.this, SpecificCourseActivity.class);
                startActivity(intent); //跳转到下一个activity（Second）
                //Toast.makeText(CourseMainActivity.this, "点击了课程", Toast.LENGTH_LONG).show();
            }

        }
    }


    //工具函数
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //和课表相关的东西结束
    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////


}

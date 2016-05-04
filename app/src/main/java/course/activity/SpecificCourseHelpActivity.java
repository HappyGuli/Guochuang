package course.activity;

import hello.login.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import java.util.ArrayList;
import java.util.List;

import api.URLs;
import common.BitmapManager;
import common.StringUtils;
import common.UIHelper;
import course.netdata.QuesitonInSpecificCourseBean;
import course.netdata.QuesitonInSpecificCourseBeanList;
import widget.AppContext;
import widget.AppException;

/**
 * Created by happypaul on 16/1/21.
 */
public class SpecificCourseHelpActivity extends ActionBarActivity {
    //和recyclerView相关的东西
    private RecyclerView questions;
    //跟recyclerView的刷新相关的东西
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isRefresh = false;

    //悬浮按钮
    private FloatingActionButtonPlus mActionButtonPlus;


    //数据
    private List<QuesitonInSpecificCourseBean> testDatas;
    private Toolbar mToolbar;

    private ProgressDialog selectorDialog;
    private AppContext appContext;// 全局Context


    //测试sharedPreference 读取数据
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    //从intent中获取数据
    private Intent  intent;
    private String  str_cid;
    private String  str_coursename;




    //handler 的使用
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            selectorDialog.cancel();
            if(swipeRefreshLayout!=null){
                swipeRefreshLayout.setRefreshing(false);
            }

            if (msg.what == 1) {
                testDatas = (List<QuesitonInSpecificCourseBean>) msg.obj;

                //数出来进行测试
                for(int i=0;i<testDatas.size();i++){
                    Log.e("TTTT", testDatas.get(i).getQuestionTitle());
                    Log.e("TTTT", testDatas.get(i).getUserName());
                }

                // 根据获取到的数据进行 展示
                initListAdapterV();

            } else if (msg.what == -1) {

                testDatas = new ArrayList<QuesitonInSpecificCourseBean>();
                // 根据获取到的数据进行 展示
                initListAdapterV();

                //当没有数据时，给出提示
                LinearLayout ll = (LinearLayout)SpecificCourseHelpActivity.this.findViewById(R.id.ll_tip);
                ll.removeAllViews();
                // 将TextView 加入到LinearLayout 中
                TextView tv = new TextView(SpecificCourseHelpActivity.this);
                tv.setHeight((int) 240);
                tv.setTextSize(16);
                tv.setTextColor(SpecificCourseHelpActivity.this.getResources()
                        .getColor(R.color.course_table_bg));
                tv.setText("在这课程中，暂时还没有同学提出问题.你可以点击右下角的按钮和同学们进行交流");

                ll. addView(tv);

                UIHelper.ToastMessage(SpecificCourseHelpActivity.this , "没有数据");
            } else if (msg.what == -2) {

                testDatas = new ArrayList<QuesitonInSpecificCourseBean>();
                // 根据获取到的数据进行 展示
                initListAdapterV();

                Log.e("TTTT","what=-2");
                UIHelper.ToastMessage(SpecificCourseHelpActivity.this, "xml解析错误");
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_in_course);

        appContext = new AppContext(getApplication());

        //从intent中获取数据
        getDataFromIntent();


        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(str_coursename);


        //初始化 加载动画
        selectorDialog = ProgressDialog.show(this, null, "正在加载，请稍候...", true,false);


        //初始化 悬浮按钮
        initButton();

        //构建初始化数据
        initDdata();

    }


    /***********************从intent中获取数据*************************************/
    private void getDataFromIntent(){
        //接受数据
        intent=this.getIntent();
        if(intent!=null){
            str_cid = intent.getExtras().getString("courseId");
            str_coursename = intent.getExtras().getString("courseName");

         }
    }



    //设置 actionBar中的返回键的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //对返回键的监听
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(selectorDialog.isShowing()){
            selectorDialog.cancel();
        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    public void initButton(){

        mActionButtonPlus = (FloatingActionButtonPlus) findViewById(R.id.FabPlus);

        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {

                switch (tagView.getId()) {
                    case R.id.action_a:

                        //添加问题
                        Intent i = new Intent(SpecificCourseHelpActivity.this, AddQuestionActivity.class);
                        Bundle b = new Bundle();
                        b.putString("cid", str_cid);
                        b.putString("course_name", str_coursename);
                        i.putExtras(b);
                        SpecificCourseHelpActivity.this.startActivity(i);

                        break;

                    case R.id.action_b:

                        //搜索问题
                        Intent i1 = new Intent(SpecificCourseHelpActivity.this, QuestionSearchActivity.class);
                        Bundle b1 = new Bundle();
                        b1.putString("cid", str_cid);
                        i1.putExtras(b1);
                        SpecificCourseHelpActivity.this.startActivity(i1);

                        break;

                    default:
                        break;
                }


            }
        });


    }


    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////




    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    //和recyclerView相关的东西开始

    //从网络端获取数据
    private void getDataFromServer() {

        //selectorDialog.show();
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {

                    QuesitonInSpecificCourseBeanList list = appContext.getQuestionsList(isRefresh,str_cid);
                    if (list.getCount() > 0) {
                        msg.what = 1;
                        msg.obj = list.getList();
                        appContext.saveObject(list, "questions_in_"+str_cid);
                    } else {
                        msg.what = -1;
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -2;
                    msg.obj = e;
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }


    private void initDdata() {
        //从服务器获得数据
        getDataFromServer();
    }

    private void initListAdapterV() {

        //和recyclerView的刷新相关
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //从服务器获得数据
                isRefresh = true;
                getDataFromServer();

            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        swipeRefreshLayout.setProgressBackgroundColor(R.color.white);

        questions = (RecyclerView) this.findViewById(R.id.specific_question_recyclerview);


        //设置layoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        questions.setLayoutManager(layoutManager);
        questions.setHasFixedSize(true);
        questions.setBackgroundColor(Color.WHITE);

        //设置adapter
        MyQuestionRecyclerViewAdapter adapter = new MyQuestionRecyclerViewAdapter(this, testDatas);
        questions.setAdapter(adapter);
    }


    private class MyQuestionRecyclerViewAdapter extends RecyclerView.Adapter<MyQuestionHolder> {

        private Context context;
        //用来加载图片的
        private BitmapManager bmpManager;
        private List<QuesitonInSpecificCourseBean> datas;

        //构造函数
        public MyQuestionRecyclerViewAdapter(Context context, List<QuesitonInSpecificCourseBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
            this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
                    context.getResources(), R.mipmap.ic_launcher));

        }

        //决定根布局
        @Override
        public MyQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.item_question,null);
            return new MyQuestionHolder(view);

        }

        //决定根数据
        @Override
        public void onBindViewHolder(MyQuestionHolder holder, int position) {
            holder.SetDataAndRefreshUI(datas.get(position),bmpManager);
        }

        @Override
        public int getItemCount() {

            return datas.size();

        }
    }

    //
    private class MyQuestionHolder extends RecyclerView.ViewHolder{

        private ImageView userImage;
        private TextView questionTitle;
        private TextView questionContent;
        private TextView questionDescript;
        private TextView answerQuestion;
        private TextView zanNumbers;


        public MyQuestionHolder(View itemView) {
            super(itemView);
            //初始化孩子对象
            userImage = (ImageView) itemView.findViewById(R.id.question_img);
            questionTitle = (TextView) itemView.findViewById(R.id.question_title);
            questionContent = (TextView) itemView.findViewById(R.id.question_content);
            questionDescript = (TextView) itemView.findViewById(R.id.question_descript);
            answerQuestion = (TextView) itemView.findViewById(R.id.answer_tv);
            zanNumbers = (TextView) itemView.findViewById(R.id.tv_thanked_number);



            //设置监听
            answerQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //测试
                   // Toast.makeText(SpecificCourseHelpActivity.this,"你想回答question:"+
                           // testDatas.get(getPosition()).getQuestionId(),Toast.LENGTH_SHORT).show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                   // Toast.makeText(SpecificCourseHelpActivity.this,"你点击了item"
                          //  +getPosition(),Toast.LENGTH_SHORT).show();



                    //跳转

                    //传送 questionid questiontitle questiondetail等数据

                    int questionid = testDatas.get(getPosition()).getQuestionId();
                    String questiontitle = testDatas.get(getPosition()).getQuestionTitle();
                    String questioncontent = testDatas.get(getPosition()).getQuestionContent();


                    Intent intent = new Intent();
                    intent.setClass(SpecificCourseHelpActivity.this, SpecificQuestionWithAnsActivity.class);   //描述起点和目标
                    Bundle bundle = new Bundle();                            //创建Bundle对象
                    bundle.putInt("questionid", questionid);                 //装入数据
                    bundle.putString("course_name", str_coursename);

                    intent.putExtras(bundle);                                //把Bundle塞入Intent里面
                    startActivity(intent);




                }
            });



        }

        public void SetDataAndRefreshUI(QuesitonInSpecificCourseBean bean,BitmapManager bitmapManager){
            questionTitle.setText(bean.getQuestionTitle());
            questionDescript.setText(bean.getUserName() + "发起了提问 ");
            questionContent.setText(bean.getQuestionContent());
            zanNumbers.setText(String.valueOf(bean.getZanNum()));

            String imgURL = bean.getUserImgurl();
            Log.e("TTTT",imgURL);

            if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {
                userImage.setImageResource(R.mipmap.usericon1);
            } else {
                if (!imgURL.contains("http")) {
                    imgURL = URLs.HTTP + URLs.HOST + "/" + imgURL;
                }
                Log.e("TTTT",imgURL);
                bitmapManager.loadBitmap(imgURL,userImage);
            }
        }


    }
    //和recyclerView相关的东西结束
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

}

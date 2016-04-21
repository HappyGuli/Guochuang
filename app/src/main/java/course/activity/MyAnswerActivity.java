package course.activity;

import api.URLs;
import common.BitmapManager;
import common.StringUtils;
import common.UIHelper;
import course.netdata.CollectInfoBean;
import course.netdata.MyAnsweredBean;
import hello.login.R;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import course.bean.MyAnswerItemBean;
import widget.AppContext;

/**
 * Created by happypaul on 16/1/26.
 */
public class MyAnswerActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private List<MyAnsweredBean> datas;
    private RecyclerView recyclerView;


    //获取用户的 账户信息
    private SharedPreferences sharedPreferences;
    private String self_sid;
    private AppContext appContext;




    private Handler mHandler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(MyAnswerActivity.this, "查看我的回答成功", Toast.LENGTH_SHORT).show();

                // 获取到从服务器中获取到的数据
                datas = ( List<MyAnsweredBean>) msg.obj;

                if(datas!=null&&datas.size()>0){
                    // 将数据给展示出来
                    initListAdapterV();
                }else{
                    //没有数据的情况

                    //当没有数据时，给出提示
                    LinearLayout ll = (LinearLayout)MyAnswerActivity.this.findViewById(R.id.ll_tip);
                    ll.removeAllViews();
                    // 将TextView 加入到LinearLayout 中
                    TextView tv = new TextView(MyAnswerActivity.this);
                    tv.setHeight((int) 60);
                    tv.setTextSize(16);
                    tv.setTextColor(MyAnswerActivity.this.getResources()
                            .getColor(R.color.course_table_bg));
                    tv.setText("您还没有回答任何问题！");

                    ll. addView(tv);

                    UIHelper.ToastMessage(MyAnswerActivity.this, "没有数据");

                }


            } else  if(msg.what == -1){
                Toast.makeText(MyAnswerActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_collected);

        //设置toolbar
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的回答");


        //获取服务器的信息的时候需要这个上下文
        appContext = new AppContext(getApplication());

        //从sharedPreference中获取 用户的账户信息
        getSidFromSharedPreference();

        //从服务器获取收藏信息
        initDdata();


    }

    //设置 actionBar中的返回键的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /******从sharedPreference中获取 用户的账户信息*****/
    public  void getSidFromSharedPreference(){
        sharedPreferences = getSharedPreferences("info", MODE_WORLD_READABLE);
        /*******从sharedPreferences中获得 用户的学号信息********/
        self_sid = sharedPreferences.getString("str_sid","20134942");

    }



    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    //和recyclerView相关的东西开始

    private void initDdata() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                //通知activity的msg
                Message msg = new Message();

                try{
                    //从服务器获取用户的额收藏信息
                    msg.obj = AppContext.FindUserAnswered(appContext, self_sid);
                    msg.what =1;

                }catch (Exception e){
                    //打印错误信息
                    e.printStackTrace();
                    msg.what =-1;

                }
                //通知activity
                mHandler.sendMessage(msg);
            }
        }).start();

    }

    private void initListAdapterV() {

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_collected);
        //设置layoutManager

        LinearLayoutManager layoutManger = new LinearLayoutManager(MyAnswerActivity.this);
        recyclerView.setLayoutManager(layoutManger);
        //设置adapter
        MyQuestionRecyclerViewAdapter adapter = new MyQuestionRecyclerViewAdapter(this, datas);
        recyclerView.setAdapter(adapter);
    }


    private class MyQuestionRecyclerViewAdapter extends RecyclerView.Adapter<MyQuestionHolder> {

        private Context context;
        private List<MyAnsweredBean> datas;

        //用啦加载图片的
        private BitmapManager bitmapManager;


        //构造函数
        public MyQuestionRecyclerViewAdapter(Context context, List<MyAnsweredBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
            //初始化 bitmapManager
            this.bitmapManager = new BitmapManager(BitmapFactory.decodeResource(
                    context.getResources(), R.mipmap.ic_launcher));


        }

        //决定根布局
        @Override
        public MyQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.item_my_answer,null);
            return new MyQuestionHolder(view);

        }

        //决定根数据
        @Override
        public void onBindViewHolder(MyQuestionHolder holder, int position) {
            holder.SetDataAndRefreshUI(datas.get(position),bitmapManager);
        }

        @Override
        public int getItemCount() {

            return datas.size();
        }
    }

    //
    private class MyQuestionHolder extends RecyclerView.ViewHolder{

        private TextView question;
        private TextView ansContent;
        private ImageView userIcon;
        private TextView zanNum;


        public MyQuestionHolder(View itemView) {
            super(itemView);
            //初始化孩子对象tv_collected_question_title
            question = (TextView) itemView.findViewById(R.id.tv_myanswer_question_title);
            ansContent = (TextView) itemView.findViewById(R.id.tv_item_myanswer_anscontent);
            userIcon = (ImageView) itemView.findViewById(R.id.iv_myanswer_item_usericon);
            zanNum = (TextView) itemView.findViewById(R.id.tv_myanswer_thanked_number);


            //设置监听事件  点击问题title
            question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setClass(MyAnswerActivity.this, SpecificQuestionWithAnsActivity.class);   //描述起点和目标
                    Bundle bundle = new Bundle();                            //创建Bundle对象
                    bundle.putInt("questionid", datas.get(getPosition()).getQid());       //装入数据
                    intent.putExtras(bundle);                                //把Bundle塞入Intent里面
                    startActivity(intent);
                }
            });


            //设置点击答案内容直接跳转到相应的问答界面
            ansContent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setClass(MyAnswerActivity.this, SpecificAnswerActivity.class);   //描述起点和目标

                    intent.putExtra("NFN", false);
                    Bundle bundle = new Bundle();                           //创建Bundle对象
                    bundle.putInt("ansid", datas.get(getPosition()).getAnsid());
                    intent.putExtras(bundle);                                //把Bundle塞入Intent里面
                    startActivity(intent);


                }
            });

        }

        public void SetDataAndRefreshUI(MyAnsweredBean bean,BitmapManager bitmapManager){

            question.setText(bean.getQuestionTitle());
            ansContent.setText(bean.getAnsContent());
            zanNum.setText(String.valueOf(bean.getZanNum()));


            //设置图片
            String imgURL = bean.getAnswerImgUrl();

            if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {
                userIcon.setImageResource(R.mipmap.ic_launcher);
            } else {
                if (!imgURL.contains("http")) {
                    imgURL = URLs.HTTP + URLs.HOST + "/" + imgURL;
                }

                bitmapManager.loadBitmap(imgURL,userIcon);
            }



        }


    }
    //和recyclerView相关的东西结束
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////




}

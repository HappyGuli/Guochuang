package course.activity;
import common.UIHelper;
import course.netdata.CollectInfoBean;
import course.netdata.MyQuestionBean;
import hello.login.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import course.bean.MyQuestionItemBean;
import widget.AppContext;

/**
 * Created by happypaul on 16/1/26.
 */
public class MyQuestionActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private List<MyQuestionBean> datas;
    private RecyclerView recyclerView;

    //在请求服务器数据时 需要使用到
    private AppContext appContext;

    //从sharedPreference中获取 用户的账户信息
    private String self_sid;
    private SharedPreferences sharedPreferences;



    private Handler mHandler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(MyQuestionActivity.this, "查看我的提问成功", Toast.LENGTH_SHORT).show();

                // 获取到从服务器中获取到的数据
                datas = ( List<MyQuestionBean>) msg.obj;

                if(datas!=null&&datas.size()>0){
                    // 将数据给展示出来
                    initListAdapterV();
                }else{
                    //没有数据的情况

                    //当没有数据时，给出提示
                    LinearLayout ll = (LinearLayout)MyQuestionActivity.this.findViewById(R.id.ll_tip);
                    ll.removeAllViews();
                    // 将TextView 加入到LinearLayout 中
                    TextView tv = new TextView(MyQuestionActivity.this);
                    tv.setHeight((int) 60);
                    tv.setTextSize(16);
                    tv.setTextColor(MyQuestionActivity.this.getResources()
                            .getColor(R.color.course_table_bg));
                    tv.setText("您还没有发布过任何问题！");

                    ll. addView(tv);

                    UIHelper.ToastMessage(MyQuestionActivity.this, "没有数据");

                }

            } else  if(msg.what == -1){
                Toast.makeText(MyQuestionActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_question);

        //设置toolbar
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的提问");


        //初始化 appContext
        appContext = new AppContext(getApplication());

        //从sharedPreference中获取 用户的账户信息
        getSidFromSharedPreference();


        //从服务器获取到初始化数据
        initDdata();

    }

    /******从sharedPreference中获取 用户的账户信息*****/
    public  void getSidFromSharedPreference(){
        sharedPreferences = getSharedPreferences("info", MODE_WORLD_READABLE);
        /*******从sharedPreferences中获得 用户的学号信息********/
        self_sid = sharedPreferences.getString("str_sid","20134942");

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




    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    //和recyclerView相关的东西开始

    private void initDdata() {

       new Thread(new Runnable() {
           @Override
           public void run() {

               Message msg = new Message();
               try{

                   //从服务器中 获取到 用户发布过的所有的问题
                   List<MyQuestionBean> list = AppContext.FindUserQuestioned(appContext,self_sid);
                   msg.what = 1;
                   msg.obj = list;

               }catch (Exception e){
                   //打印出错误详情
                   e.printStackTrace();
                   msg.what = -1;
               }

               //将获取到的数据 发送给 UI
               mHandler.sendMessage(msg);

           }
       }).start();

    }

    private void initListAdapterV() {

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_collected);
        //设置layoutManager

        LinearLayoutManager layoutManger = new LinearLayoutManager(MyQuestionActivity.this);
        recyclerView.setLayoutManager(layoutManger);
        //设置adapter
        MyQuestionRecyclerViewAdapter adapter = new MyQuestionRecyclerViewAdapter(this, datas);
        recyclerView.setAdapter(adapter);
    }


    private class MyQuestionRecyclerViewAdapter extends RecyclerView.Adapter<MyQuestionHolder> {

        private Context context;
        private List<MyQuestionBean> datas;

        //构造函数
        public MyQuestionRecyclerViewAdapter(Context context, List<MyQuestionBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
        }

        //决定根布局
        @Override
        public MyQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.item_my_question,null);
            return new MyQuestionHolder(view);

        }

        //决定根数据
        @Override
        public void onBindViewHolder(MyQuestionHolder holder, int position) {
            holder.SetDataAndRefreshUI(datas.get(position));
        }

        @Override
        public int getItemCount() {

            return datas.size();
        }
    }

    //
    private class MyQuestionHolder extends RecyclerView.ViewHolder{

        private TextView questionContent;
        private TextView questionTitle;
        private TextView questionDate;


        public MyQuestionHolder(View itemView) {
            super(itemView);
            //初始化孩子对象tv_collected_question_title
            questionContent = (TextView) itemView.findViewById(R.id.tv_myquestion_questioncontent);
            questionTitle = (TextView) itemView.findViewById(R.id.tv_myquestion_question_title);
            questionDate = (TextView) itemView.findViewById(R.id.tv_myquestion_time);
            //设置监听
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //设置监听事件  点击item跳转到相应的问题当中去
                    Intent intent = new Intent();
                    intent.setClass(MyQuestionActivity.this, SpecificQuestionWithAnsActivity.class);   //描述起点和目标
                    Bundle bundle = new Bundle();                            //创建Bundle对象
                    bundle.putInt("questionid", datas.get(getPosition()).getQid());       //装入数据
                    intent.putExtras(bundle);//把Bundle塞入Intent里面
                    startActivity(intent);

                }
            });

        }

        public void SetDataAndRefreshUI(MyQuestionBean bean){

            questionContent.setText(bean.getQuestionContent());
            questionTitle.setText(bean.getQuestionTitle());
            questionDate.setText(bean.getDate());

        }


    }
    //和recyclerView相关的东西结束
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////




}

package course.activity;
import hello.login.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import api.ApiClient;
import common.UIHelper;
import course.netdata.CourseboardBean;
import widget.AppContext;

/**
 * Created by happypaul on 16/1/23.
 */
public class CourseBoardActivity extends ActionBarActivity {

    private RecyclerView recyclerView;
    private Toolbar mToolbar;

    private Intent intent ;
    private String str_cid;
    private String str_tname;

    private List<CourseboardBean>  testDatas ;

    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置界面
        setContentView(R.layout.course_board);


        //设置toolbar
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("课堂公告");

        getDataFromIntent();


        //出事话 appContext
        appContext = new AppContext(getApplication());
        initDdata();


    }

    //handler 的使用
    public android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 1) {

                testDatas = (List<CourseboardBean>)msg.obj;
                // 根据获取到的数据进行 展示
                initListAdapterV();

            } else if (msg.what == -1) {

                testDatas = new ArrayList<CourseboardBean>();
                // 根据获取到的数据进行 展示
                initListAdapterV();

                //当没有数据时，给出提示
                LinearLayout ll = (LinearLayout)CourseBoardActivity.this.findViewById(R.id.ll_tip);
                ll.removeAllViews();
                // 将TextView 加入到LinearLayout 中
                TextView tv = new TextView(CourseBoardActivity.this);
                tv.setHeight((int) 60);
                tv.setTextSize(16);
                tv.setTextColor(CourseBoardActivity.this.getResources()
                        .getColor(R.color.course_table_bg));
                tv.setText("在这课程中，老师还没有发布公告");

                ll. addView(tv);

                UIHelper.ToastMessage(CourseBoardActivity.this, "没有数据");

            } else if (msg.what == -2) {

                testDatas = new ArrayList<CourseboardBean>();
                // 根据获取到的数据进行 展示
                initListAdapterV();

                UIHelper.ToastMessage(CourseBoardActivity.this, "服务器出现问题");
            }
        }
    };
    /***************从intent中 获取到 后面需要使用的参数******************/
    private void getDataFromIntent(){

        intent = getIntent();

        str_cid = intent.getExtras().getString("cid");
        str_tname = intent.getExtras().getString("tname");

//        Log.e("TTTT","cid "+str_cid);
//        Log.e("TTTT","tname"+str_tname);

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
    /***************从服务器中 获取 课堂公告******************/

    private void initDdata() {

       new Thread(new Runnable() {

           @Override
           public void run() {

               Message msg = new Message();
               try{
                   List<CourseboardBean> list = ApiClient.findCrsBrdsBycisTnm(appContext, str_cid, str_tname);

                   msg.obj = list;

                   if(list.size()==0){
                       //如果返回的数列的长度为0
                       msg.what=-1;
                   }else{
                       msg.what=1;
                   }
                   mHandler.sendMessage(msg);

               }catch (Exception e){
                   //如果出现错误
                   e.printStackTrace();
                   msg.what= -2;
                   mHandler.sendMessage(msg);

               }
           }
       }).start();

    }

    private void initListAdapterV() {

        recyclerView = (RecyclerView) this.findViewById(R.id.course_board_recy);
        //设置layoutManager

        LinearLayoutManager layoutManger = new LinearLayoutManager(CourseBoardActivity.this);
        recyclerView.setLayoutManager(layoutManger);
        //设置adapter
        MyQuestionRecyclerViewAdapter adapter = new MyQuestionRecyclerViewAdapter(this, testDatas);
        recyclerView.setAdapter(adapter);
    }


    private class MyQuestionRecyclerViewAdapter extends RecyclerView.Adapter<MyQuestionHolder> {

        private Context context;
        private List<CourseboardBean> datas;

        //构造函数
        public MyQuestionRecyclerViewAdapter(Context context, List<CourseboardBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
        }

        //决定根布局
        @Override
        public MyQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.item_course_board,null);
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

        private TextView boardTitle;
        private TextView boardContent;
        private TextView boardSponserTime;

        public MyQuestionHolder(View itemView) {
            super(itemView);
            //初始化孩子对象
            boardTitle = (TextView) itemView.findViewById(R.id.tv_board_title);
            boardContent = (TextView) itemView.findViewById(R.id.tv_board_content);
            boardSponserTime = (TextView) itemView.findViewById(R.id.tv_board_sponser_time);

            //设置监听
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //测试
                    Toast.makeText(CourseBoardActivity.this, "你点击的公告是" +
                            testDatas.get(getPosition()).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void SetDataAndRefreshUI(CourseboardBean bean){

            boardTitle.setText(bean.getTitle());
            boardContent .setText(bean.getContent());
            boardSponserTime.setText(bean.getTname()+"  "+String.valueOf(bean.getDate()));

        }


    }
    //和recyclerView相关的东西结束
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        //先清空
        menu.clear();
        //当前是课表页面
        //初始化
        MenuItem menuItem = menu.add("发布公告");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                Log.e("menu", (String) item.getTitle());

                if (item.getTitle().equals("发布公告")) {

                    return true;
                }
                return true;
            }
        });
        return true;

    }

}

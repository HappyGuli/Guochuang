package course.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.CollectItemBean;
import bean.MyQuestionItemBean;

/**
 * Created by happypaul on 16/1/26.
 */
public class MyQuestionActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private List<MyQuestionItemBean> datas;
    private RecyclerView recyclerView;


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


        initDdata();
        initListAdapterV();


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

        datas = new ArrayList<MyQuestionItemBean>();

        for(int i=0;i<1;i++){

            MyQuestionItemBean tem= new MyQuestionItemBean();

            tem.setQuestionTitle("有没有办法关掉用户新建话题的权限呢?");
            tem.setQuetionContent("建立了Excel表格，有没有一种权限设置了之后，别人将无法将整个文档删除？\" +" +
                    "                    \"是不是需要靠软件来实现？请知道的帮帮忙，谢谢拉！");

            //获取当前时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            Date currDate = new Date(System.currentTimeMillis());
            String curD = simpleDateFormat.format(currDate);
            tem.setDate(curD);

            datas.add(tem);
        }

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
        private List<MyQuestionItemBean> datas;

        //构造函数
        public MyQuestionRecyclerViewAdapter(Context context, List<MyQuestionItemBean> datas) {
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
                    //测试
                    Toast.makeText(MyQuestionActivity.this, "你点击的收藏是" +
                            datas.get(getPosition()), Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void SetDataAndRefreshUI(MyQuestionItemBean bean){

            questionContent.setText(bean.getQuetionContent());
            questionTitle.setText(bean.getQuestionTitle());
            questionDate.setText(bean.getDate());

        }


    }
    //和recyclerView相关的东西结束
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////




}

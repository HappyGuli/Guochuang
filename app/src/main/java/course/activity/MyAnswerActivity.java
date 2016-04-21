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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bean.CollectItemBean;
import bean.MyAnswerItemBean;

/**
 * Created by happypaul on 16/1/26.
 */
public class MyAnswerActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private List<MyAnswerItemBean> datas;
    private RecyclerView recyclerView;

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

        datas = new ArrayList<MyAnswerItemBean>();

        for(int i=0;i<1;i++){

            MyAnswerItemBean tem= new MyAnswerItemBean();

            tem.setUserIcon(R.mipmap.usericon1);
            tem.setQuestionTitle("去英国留学有没有必要去买商业保险?");
            tem.setAnsContent("完全有必要！\n" +
                    "在一个完全陌生，尤其是沟通还有障碍的国家学习和生活，还是会遇到蛮多问题的。" +
                    "除了日常生活中偶尔生病，在学校、住处、旅游的目的地可能遇到的人身意外和财产损失，" +
                    "还有些是由于学生自己的责任造成第三方损失（比如损毁房东的家具）、" +
                    "不熟悉海外驾驶习惯和交通规则撞伤他人等。为了减少这些风险所造成的损失，" +
                    "在到达留学目的地之前最好是能够买一份保险。" +
                    "因为遇到的风险多种多样，所以最好是买一份相对来说保障比较全面的保险。" +
                    "除了医疗保险（办理签证时必须买的nhs），最好再配下人身意外和财产的保障，" +
                    "而且最好是选择全球范围内的保险公司的保险产品，他们通常都会有24小时的紧急救援团队，" +
                    "比较熟悉当地的语言、文化和法律，而且可以提供24小时的中文服务。");
            tem.setZanNum(i);

            datas.add(tem);
        }

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
        private List<MyAnswerItemBean> datas;

        //构造函数
        public MyQuestionRecyclerViewAdapter(Context context, List<MyAnswerItemBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
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
            holder.SetDataAndRefreshUI(datas.get(position));
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
            //设置监听
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //测试
                    Toast.makeText(MyAnswerActivity.this, "你点击的收藏是" +
                            datas.get(getPosition()), Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void SetDataAndRefreshUI(MyAnswerItemBean bean){

            question.setText(bean.getQuestionTitle());
            ansContent.setText(bean.getAnsContent());
            userIcon.setImageResource(bean.getUserIcon());
            zanNum.setText(String.valueOf(bean.getZanNum()));

        }


    }
    //和recyclerView相关的东西结束
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////




}

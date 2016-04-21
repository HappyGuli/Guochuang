package course.activity;

import common.UIHelper;
import course.customUI.HtmlTextView;
import course.netdata.AnswerDetailBean;
import course.netdata.CollectInfoBean;
import hello.login.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import api.ApiClient;
import widget.AppContext;
import widget.AppException;

/**
 * Created by happypaul on 16/1/24.
 */
public class SpecificAnswerActivity extends ActionBarActivity {


    private Toolbar mToolbar;
    private String[] menuTitles;

    // 和点赞 和 取消点赞相关
    private ImageView iv_volt;
    private ImageView iv_volt_down;

    private TextView tv_volt;
    private TextView tv_volt_down;

    private int volt_count,vlot_down_count;

    private boolean volt_ed,volt_down_ed;

    // 保存赞 信息到服务器端
    private AppContext appContext ;
    private AnswerDetailBean answerDetailBean ;

    //从intent中获得数据
    private Intent intent;
    private String questionTitle;
    private String answerContent;
    private int ansid;
    private String userName;
    private String self_sid ;  //自己的sid
    private SharedPreferences sharedPreferences; //用于获取保存在手机上的信息
    private int zanNum;
    private int caiNum;

    //展示从intent中获取到的数据
    private TextView tv_question_title;
    private TextView tv_user_name;
    private HtmlTextView tv_answer_content;

    private Handler mHandler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(SpecificAnswerActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
            } else  if(msg.what == -1){

                Toast.makeText(SpecificAnswerActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private Handler answer_mHandler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                //获取到 详细答案信息
                answerDetailBean = (AnswerDetailBean) msg.obj;

                //将获取到的数据进行 赋值给私有化属性
                questionTitle = answerDetailBean.getQuestionTitle();
                answerContent = answerDetailBean.getAnswerContent();
                userName = answerDetailBean.getUserName();
                zanNum = answerDetailBean.getZanNum();
                caiNum = answerDetailBean.getCaiNum();

                //根据获取到的数据进行展示
                InitUI();

            } else  if(msg.what == -1){
                Toast.makeText(SpecificAnswerActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private Handler volt_mHandler = new android.os.Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                //设置可点
                iv_volt.setEnabled(true);

                //保存用户点赞信息成功
                volt_ed = !volt_ed;
                iv_volt.setImageResource(R.mipmap.ic_vote_checked);
                volt_count = Integer.valueOf(tv_volt.getText().toString());
                tv_volt.setText(String.valueOf(volt_count + 1));

            } else  if(msg.what == 2){


                //设置可点
                iv_volt.setEnabled(true);

                //取消点赞信息成功
                volt_ed = !volt_ed;
                iv_volt.setImageResource(R.mipmap.ic_vote_normal);
                volt_count = Integer.valueOf(tv_volt.getText().toString());
                tv_volt.setText(String.valueOf(volt_count - 1));

            } else  if(msg.what == 3){

                //设置可点
                iv_volt_down.setEnabled(true);

                //保存用户点踩信息成功
                volt_down_ed = !volt_down_ed;
                iv_volt_down.setImageResource(R.mipmap.ic_vote_down_checked);
                vlot_down_count = Integer.valueOf(tv_volt_down.getText().toString());
                tv_volt_down.setText(String.valueOf(vlot_down_count + 1));

            } else  if(msg.what == 4){

                //设置可点
                iv_volt_down.setEnabled(true);

                //保存取消点踩信息成功
                volt_down_ed = !volt_down_ed;
                iv_volt_down.setImageResource(R.mipmap.ic_vote_down_normal);
                vlot_down_count = Integer.valueOf(tv_volt_down.getText().toString());
                tv_volt_down.setText(String.valueOf(vlot_down_count - 1));


            } else  if(msg.what == 5){

                //根据结果去 初始化
                int result = (int)msg.obj;
                if(result==0){
                    //处理初始化的工作
                    volt_ed = false;
                    volt_down_ed = false;
                }else if(result == 1){
                    //处理初始化的工作
                    volt_ed = true;
                    volt_down_ed = false;
                    iv_volt.setImageResource(R.mipmap.ic_vote_checked);

                }else if(result == 2){
                    //处理初始化的工作
                    volt_ed = false;
                    volt_down_ed = true;
                    iv_volt_down.setImageResource(R.mipmap.ic_vote_down_checked);

                }

            } else  if(msg.what == -1){

                //设置可点
                iv_volt_down.setEnabled(true);
                //设置可点
                iv_volt.setEnabled(true);

                //服务器出错时 给出提示
                Toast.makeText(SpecificAnswerActivity.this, "服务器出错", Toast.LENGTH_SHORT).show();
            }
        }
    };






    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.specific_answer);


        //设置toolbar
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("问答");

        //初始化 上下文对象
        appContext = new AppContext(getApplication());


        //get Data、 from sharedPreference 获取 用户账号信息
        getDataFromSharedPreference();

        //初始化数据
        InitData();


    }




    /***********从sharedPreference中获取到自己的学号等信息***************/
    public void getDataFromSharedPreference(){

        //从sharedPreferences 中获取学号信息
        sharedPreferences = getSharedPreferences("info", MODE_WORLD_READABLE);
        /*******从sharedPreferences中获得 用户的学号信息********/
        self_sid = sharedPreferences.getString("str_sid", "20134942");

    }


    //从intent中获得数据
    private void InitData(){

        //从上个activity中获取到数据
        intent = getIntent();

        if(intent!=null){

            //如果不用从服务器中获取数据
            if(intent.getBooleanExtra("NFN",false)){
                Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
                ansid = bundle.getInt("ansid", 1);
                questionTitle = bundle.getString("questiontitle");
                answerContent = bundle.getString("ansContent");
                userName = bundle.getString("username");
                zanNum = bundle.getInt("zanNum");
                caiNum = bundle.getInt("caiNum");


                //根据上面获取的数据进行展示
                //初始化界面
                InitUI();

            }else{
                Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
                ansid = bundle.getInt("ansid", 1);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Message msg = new Message();

                        try{
                            // 从服务器获取到 answer的详细信息
                            AnswerDetailBean bean =  AppContext.findAnserDetail(appContext,ansid);

                            //发功信息给UI
                            msg.what = 1;
                            msg.obj = bean;

                        }catch (Exception e){
                            e.printStackTrace();
                            msg.what = -1;
                        }

                        //发送信息 给UI
                        answer_mHandler.sendMessage(msg);
                    }
                }).start();
            }


        }else{
            //给出提示
            Toast.makeText(SpecificAnswerActivity.this,"intent is null",Toast.LENGTH_SHORT).show();
        }

    }


    //初始化 UI
    public  void InitUI(){

        appContext = new AppContext(getApplication());

        //展示 从intent中获取到的数据
        tv_answer_content = (HtmlTextView)findViewById(R.id.tv_answer_content);
        tv_user_name= (TextView)findViewById(R.id.tv_answer_author);
        tv_question_title = (TextView)findViewById(R.id.tv_question_title);

        tv_user_name.setText(userName);
        tv_question_title.setText(questionTitle);


        //设置html
        tv_answer_content.setHtmlFromString(answerContent,false);



        // 和点赞 点踩 相关
        iv_volt = (ImageView) findViewById(R.id.iv_volt);
        iv_volt_down = (ImageView) findViewById(R.id.iv_volt_down);

        tv_volt = (TextView)findViewById(R.id.tv_specific_answer_thanked_num);
        tv_volt_down = (TextView)findViewById(R.id.tv_specific_answer_disliked_num);

        // 将从intent中获取的数据 暂时出来
        tv_volt.setText(String.valueOf(zanNum));
        tv_volt_down.setText(String.valueOf(caiNum));





        //获取 用户之前是否对这个回答进行过点赞
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg  = new Message();

                try{

                    msg.what = 5;
                    int result = AppContext.FindIsAnswerVoltOrVoltdown(appContext, self_sid, ansid);
                    msg.obj = result;

                }catch (Exception e){
                    e.printStackTrace();
                    msg.what = -1;
                }

                //发送信息给 handler
                volt_mHandler.sendMessage(msg);

            }
        }).start();



        iv_volt_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //设置不能点
                v.setEnabled(false);

                if (!volt_down_ed && !volt_ed) {

                    //保存用户点赞信息
                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            Message msg  = new Message();

                            try{
                                //3 代表 保存点踩 信息成功
                                msg.what = 3;
                                ApiClient.saveUserVoltOrVoltdown(appContext,self_sid,ansid,0);

                            }catch (Exception e){
                                e.printStackTrace();
                                msg.what = -1;
                            }

                            //发送信息给 handler
                            volt_mHandler.sendMessage(msg);

                        }
                    }).start();


                } else if (volt_down_ed && !volt_ed) {

                    //设置不能点
                    v.setEnabled(false);

                    //保存用户取消点赞信息
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Message msg  = new Message();

                            try{
                                //4 代表 保存取消点踩 信息成功
                                msg.what = 4;
                                AppContext.SaveUserCancelVoltOrVoltdown(appContext, self_sid, ansid, 0);

                            }catch (Exception e){
                                e.printStackTrace();
                                msg.what = -1;
                            }

                            //发送信息给 handler
                            volt_mHandler.sendMessage(msg);

                        }
                    }).start();

                }

            }
        });



        iv_volt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!volt_down_ed && !volt_ed) {

                    //保存用户点赞信息
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Message msg  = new Message();

                            try{
                                //1 代表 保存点赞 信息成功
                                msg.what = 1;
                                ApiClient.saveUserVoltOrVoltdown(appContext,self_sid,ansid,1);

                            }catch (Exception e){
                                e.printStackTrace();
                                msg.what = -1;
                            }

                            //发送信息给 handler
                            volt_mHandler.sendMessage(msg);

                        }
                    }).start();




                } else if (!volt_down_ed && volt_ed) {

                    //保存用户取消点赞信息
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Message msg  = new Message();

                            try{
                                //2 代表 保存取消点赞 信息成功
                                msg.what = 2;
                                AppContext.SaveUserCancelVoltOrVoltdown(appContext, self_sid, ansid, 1);

                            }catch (Exception e){
                                e.printStackTrace();
                                msg.what = -1;
                            }

                            //发送信息给 handler
                            volt_mHandler.sendMessage(msg);

                        }
                    }).start();
                }

            }
        });

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

    //////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////
    /**
     * 和menu相关的东西
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        //先清空
        menu.clear();

        //初始化
        menuTitles = this.getResources().getStringArray(R.array.answer_help);

        for (String name : menuTitles){
            MenuItem menuItem = menu.add(name);
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {


                    Log.e("menu", (String) item.getTitle());

                    if(item.getTitle().equals("收藏")){

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Message msg = new Message();
                                try{
                                    // 保存用户收藏问答的信息
                                    AppContext.saveUserCollect(appContext,self_sid,ansid);
                                    msg.what = 1;

                                }catch (Exception e){
                                    //打印出错误
                                    e.printStackTrace();
                                    msg.what = -1;
                                }
                                //发送信息
                                mHandler.sendMessage(msg);

                            }
                        }).start();
                        return true;
                    }


                    if(item.getTitle().equals("评论")){
                        //Toast.makeText(SpecificAnswerActivity.this,"你点击的是评论",Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(SpecificAnswerActivity.this,AddAnswerCommentActivity.class);
                        i.putExtra("ansid",ansid);
                        SpecificAnswerActivity.this.startActivity(i);
                        return true;
                    }


                    if(item.getTitle().equals("分享")){
                        Toast.makeText(SpecificAnswerActivity.this,"你点击的是分享",Toast.LENGTH_SHORT).show();

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

}


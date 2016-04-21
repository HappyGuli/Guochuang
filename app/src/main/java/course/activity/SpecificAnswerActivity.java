package course.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import api.ApiClient;
import netdata.AnswerToSpecQuestionBeanList;
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

    //从intent中获得数据
    private Intent intent;
    private String questionTitle;
    private String answerContent;
    private int ansid;
    private String userName;
    private String anser_sid; //回答者的sid
    private String self_sid = "20134942";  //自己的sid
    private int zanNum;
    private int caiNum;

    //展示从intent中获取到的数据
    private TextView tv_question_title;
    private TextView tv_user_name;
    private TextView tv_answer_content;


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

        //初始化数据
        InitData();

        //初始化界面
        InitUI();
    }



    //从intent中获得数据
    private void InitData(){

        intent = getIntent();
        //从上个activity中获取到数据
        intent = getIntent();
        if(intent!=null){

            Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
            ansid = bundle.getInt("ansid", 1);
            questionTitle = bundle.getString("questiontitle");
            answerContent = bundle.getString("ansContent");
            userName = bundle.getString("username");
            anser_sid = bundle.getString("sid");
            zanNum = bundle.getInt("zanNum");
            caiNum = bundle.getInt("caiNum");

        }else{
            //给出提示
            Toast.makeText(SpecificAnswerActivity.this,"intent is null",Toast.LENGTH_SHORT).show();
        }

    }


    //初始化 UI
    public  void InitUI(){

        appContext = new AppContext(getApplication());

        //展示 从intent中获取到的数据
        tv_answer_content = (TextView)findViewById(R.id.tv_answer_content);
        tv_user_name= (TextView)findViewById(R.id.tv_answer_author);
        tv_question_title = (TextView)findViewById(R.id.tv_question_title);

        tv_answer_content.setText(answerContent);
        tv_user_name.setText(userName);
        tv_question_title.setText(questionTitle);



        // 和点赞 点踩 相关
        iv_volt = (ImageView) findViewById(R.id.iv_volt);
        iv_volt_down = (ImageView) findViewById(R.id.iv_volt_down);

        tv_volt = (TextView)findViewById(R.id.tv_specific_answer_thanked_num);
        tv_volt_down = (TextView)findViewById(R.id.tv_specific_answer_disliked_num);

        // 将从intent中获取的数据 暂时出来
        tv_volt.setText(String.valueOf(zanNum));
        tv_volt_down.setText(String.valueOf(caiNum));

        volt_ed = false;
        volt_down_ed = false;

        iv_volt_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!volt_down_ed && !volt_ed) {
                    volt_down_ed = !volt_down_ed;
                    iv_volt_down.setImageResource(R.mipmap.ic_vote_down_checked);
                    vlot_down_count = Integer.valueOf(tv_volt_down.getText().toString());
                    tv_volt_down.setText(String.valueOf(vlot_down_count + 1));

                } else if (volt_down_ed && !volt_ed) {
                    volt_down_ed = !volt_down_ed;
                    iv_volt_down.setImageResource(R.mipmap.ic_vote_down_normal);
                    vlot_down_count = Integer.valueOf(tv_volt_down.getText().toString());
                    tv_volt_down.setText(String.valueOf(vlot_down_count - 1));


                }

            }
        });

        iv_volt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!volt_down_ed && !volt_ed) {
                    volt_ed = !volt_ed;
                    iv_volt.setImageResource(R.mipmap.ic_vote_checked);
                    volt_count = Integer.valueOf(tv_volt.getText().toString());
                    tv_volt.setText(String.valueOf(volt_count + 1));

                } else if (!volt_down_ed && volt_ed) {
                    volt_ed = !volt_ed;
                    iv_volt.setImageResource(R.mipmap.ic_vote_normal);
                    volt_count = Integer.valueOf(tv_volt.getText().toString());
                    tv_volt.setText(String.valueOf(volt_count - 1));
                }

            }
        });

    }

    //保存用户的点赞信息到 服务器端
    private void saveUserVoltOrVoltdown() {

        //selectorDialog.show();
        new Thread() {
            public void run() {
                try {
                    if(volt_ed==true&&volt_down_ed==false){
                        ApiClient.saveUserVoltOrVoltdown(appContext,self_sid,ansid, 1);
                    }
                    if(volt_ed==false&&volt_down_ed==true){
                        ApiClient.saveUserVoltOrVoltdown(appContext,self_sid,ansid, 0);
                    }

                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //设置 actionBar中的返回键的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {

            //在界面改变之前 将这个点赞信息保存起来
            saveUserVoltOrVoltdown();

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

                    //在页面变化之前 将点赞信息给保存起来
                    saveUserVoltOrVoltdown();

                    Log.e("menu", (String) item.getTitle());

                    if(item.getTitle().equals("收藏")){

                        //Toast.makeText(SpecificAnswerActivity.this,"你点击的是收藏",Toast.LENGTH_SHORT).show();
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


package course.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import api.ApiClient;
import common.UIHelper;
import widget.AppContext;
import widget.AppException;

/**
 * Created by happypaul on 16/1/24.
 */
public class AddAnswerActivity extends ActionBarActivity {


    private Toolbar mToolbar;

    //获取intent中的数据
    private Intent intent;
    private int qid;
    private String answer_content;


    //通过sharedPreference来获取公共数据
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String self_sid;


    //button
    private Button btn_add_image;
    private Button btn_add_answer;
    private EditText et_add_answer;


    private AppContext appContext;



    //handler 的使用
    public android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                UIHelper.ToastMessage(AddAnswerActivity.this, "添加回答成功");

                //结束自己这个界面
                finish();

                //跳转到具体课程的界面
                Intent intent = new Intent(AddAnswerActivity.this, SpecificQuestionWithAnsActivity.class);
                intent.putExtra("questionid", qid);
                AddAnswerActivity.this.startActivity(intent);

            } else if (msg.what == 0) {
                UIHelper.ToastMessage(AddAnswerActivity.this, "服务器出错");
            }
        }
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_answer);

        // //设置toolbar
        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("添加回答");

        appContext = new AppContext(getApplication());

        btn_add_image= (Button)mToolbar.findViewById(R.id.btn_add_picture);
        btn_add_answer= (Button)mToolbar.findViewById(R.id.btn_add_answer);

        InitData();
        InitUI();

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


    private void InitData(){

        //从sharedPreference 中获取自己的学号信息
        sharedPreferences = getSharedPreferences("info",MODE_WORLD_READABLE);
        self_sid = sharedPreferences.getString("str_sid","20134942");


        intent = getIntent();
        if(intent!=null){

            Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
            qid = bundle.getInt("qid", 100);

        }else{
            //给出提示
            Toast.makeText(AddAnswerActivity.this, "intent is null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化 控件
     */
    private void InitUI(){

        et_add_answer = (EditText)findViewById(R.id.et_answer_detail);
        //设置监听事件
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                Toast.makeText(AddAnswerActivity.this,"此版本暂不支持此功能",Toast.LENGTH_SHORT).show();
            }
        });

        //设置回答按钮 点击事件
        btn_add_answer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                answer_content = et_add_answer.getText().toString();

                if(answer_content==null||answer_content.equals("")){

                    Toast.makeText(AddAnswerActivity.this,"请输入答案内容",Toast.LENGTH_SHORT).show();

                }else{
                    //先禁用按钮 防止重复条件申请
                    btn_add_answer.setEnabled(false);

                    //selectorDialog.show();
                    new Thread() {
                        public void run() {
                            Message msg = new Message();
                            try {

                                ApiClient.saveUserAnswer(appContext,self_sid,qid,answer_content);
                                msg.what=1;

                                Intent intent = new Intent();
                                intent.setClass(AddAnswerActivity.this, SpecificQuestionWithAnsActivity.class);   //描述起点和目标
                                Bundle bundle = new Bundle();                           //创建Bundle对象
                                bundle.putInt("questionid", qid);     //装入数据
                                intent.putExtras(bundle);                                //把Bundle塞入Intent里面
                                startActivity(intent);


                            } catch (AppException e){
                                msg.what=0;
                            }
                            mHandler.sendMessage(msg);
                        }
                    }.start();

                }


            }
        });
    }



}




package course.activity;

import hello.login.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import course.util.DoGet;

/**
 * Created by happypaul on 16/3/2.
 */
public class CourseLoginActivity extends Activity{

    private View view_school,view_password,view_student_number,view_role;
    private Button btn_login;
    private AutoCompleteTextView et_school,et_role;
    private TextView et_password,et_student_num;

    //判断editText中是否有输入文字
    private String str_school,str_sid,str_passwrd,str_role;


    //和记住密码相关
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
             if (msg.what == 1) {
                 Intent intent = new Intent();
                 intent.setClass(CourseLoginActivity.this, CourseMainActivity.class);   //描述起点和目标
                 Bundle bundle = new Bundle();                 //创建Bundle对象
                 bundle.putString("str_school", str_school);   //装入数据
                 bundle.putString("str_role", str_role);       //装入数据
                 bundle.putString("str_password", str_passwrd); //装入数据
                 bundle.putString("str_sid", str_sid);         //装入数据
                 intent.putExtras(bundle);                     //把Bundle塞入Intent里面
                 startActivity(intent);

             } else if (msg.what == 0) {

                 Toast.makeText(CourseLoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
             }else  if(msg.what == -1){

                 Toast.makeText(CourseLoginActivity.this,"服务器出错",Toast.LENGTH_SHORT).show();
             }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_in_new);

        initUI();

        InitData();
    }


    private void initUI(){


        btn_login = (Button)this.findViewById(R.id.btn_login);

        btn_login.setEnabled(true);

        view_school = this.findViewById(R.id.view_school);
        view_password = this.findViewById(R.id.view_password);
        view_student_number = this.findViewById(R.id.view_student_number);
        view_role = this.findViewById(R.id.view_role);

        et_password = (EditText) this.findViewById(R.id.et_password);
        et_school = (AutoCompleteTextView) this.findViewById(R.id.et_school);
        et_student_num = (EditText) this.findViewById(R.id.et_student_num);
        et_role = (AutoCompleteTextView) this.findViewById(R.id.et_add_role);

        et_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    view_password.setBackgroundColor(getResources().getColor(R.color.et_login_focused));
                }else{
                    view_password.setBackgroundColor(getResources().getColor(R.color.et_login_normal));
                }

            }
        });

        et_student_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    view_student_number.setBackgroundColor(getResources().getColor(R.color.et_login_focused));

                } else {
                    view_student_number.setBackgroundColor(getResources().getColor(R.color.et_login_normal));
                }

            }
        });

        et_school.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    view_school.setBackgroundColor(getResources().getColor(R.color.et_login_focused));
                } else {
                    view_school.setBackgroundColor(getResources().getColor(R.color.et_login_normal));
                }

            }
        });


        et_role.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    view_role.setBackgroundColor(getResources().getColor(R.color.et_login_focused));
                }else{
                    view_role.setBackgroundColor(getResources().getColor(R.color.et_login_normal));
                }

            }
        });

        //设置自动补全功能
        ArrayAdapter schoolAdapter=ArrayAdapter.createFromResource(this, R.array.school, android.R.layout.simple_spinner_item);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        et_school.setAdapter(schoolAdapter);


        //设置自动补全功能
        ArrayAdapter roleAdapter=ArrayAdapter.createFromResource(this, R.array.school, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        et_role.setAdapter(roleAdapter);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_passwrd = et_password.getText().toString();
                str_school = et_school.getText().toString();
                str_sid = et_student_num.getText().toString();
                str_role = et_role.getText().toString();

                if(str_passwrd==null||str_passwrd.equals("")){
                    Toast.makeText(CourseLoginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                }else if(str_school==null||str_school.equals("")){
                    Toast.makeText(CourseLoginActivity.this,"请选择学校",Toast.LENGTH_SHORT).show();
                }else if(str_sid==null||str_sid.equals("")){
                    Toast.makeText(CourseLoginActivity.this,"请输入学号",Toast.LENGTH_SHORT).show();
                }else if(str_role==null||str_role.equals("")){
                    Toast.makeText(CourseLoginActivity.this,"请选择角色",Toast.LENGTH_SHORT).show();
                }else{


                    new Thread() {
                        public void run() {

                            Message msg = new Message();

                            try{
                                if(DoGet.isUserNameAndPsdRight(str_sid,str_passwrd)){
                                    //用户名密码正确
                                    msg.what = 1;
                                }else{
                                    msg.what = 0;
                                }
                             }catch (Exception e){
                                //给出提示
                                Log.e("TTTT","测试密码是否正确 失败");
                                //出现错误
                                msg.what = -1;
                             }

                             mHandler.sendMessage(msg);
                        }
                    }.start();
                }


            }
        });


   }


    private void InitData(){

        sharedPreferences = getSharedPreferences("info",MODE_WORLD_READABLE);

        str_role = sharedPreferences.getString("str_role", "学生");
        str_sid = sharedPreferences.getString("str_sid","20134942");
        str_passwrd = sharedPreferences.getString("str_password","");
        str_school = sharedPreferences.getString("str_school", "重庆大学");

        //展示
        et_password.setText(str_passwrd);
        et_school.setText(str_school);
        et_student_num.setText(str_sid);
        et_role.setText(str_role);

    }

}

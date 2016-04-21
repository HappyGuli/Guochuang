package course.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by happypaul on 16/1/23.
 */
public class SpecificCourseActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private CardView cdv_course_comunication;
    private CardView cdv_course_board;
    private CardView cdv_course_sign;



    //从intent中获取数据
    private  Intent intent;
    private  String str_teacher,str_room,str_weeks,str_last,str_courseName,str_cid;

    //和课程信息相关的widget
    private TextView tv_teacher,tv_room,tv_weeks,tv_last,tv_courseName;

    private SharedPreferences sharedPreferences ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.specific_course);

        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("课程");

        getDataFromIntentAndSave();

        //设置cardView的点击事件
        initCdvClickListener();

    }



    /*****************从intent中获取数据 然后在相应的组件中进行展示********************************/
    private void getDataFromIntentAndSave(){
        intent = getIntent();
        if(intent !=null){
            str_courseName = intent.getExtras().getString("title");
            str_last = intent.getExtras().getString("last");
            str_room = intent.getExtras().getString("room");
            str_teacher = intent.getExtras().getString("teacher");
            str_weeks = intent.getExtras().getString("weeks");
            str_cid = intent.getExtras().getString("cid");


        }

        tv_courseName = (TextView)this.findViewById(R.id.tv_course_name);
        tv_last = (TextView)this.findViewById(R.id.tv_class_num);
        tv_teacher = (TextView)this.findViewById(R.id.tv_teacher_name);
        tv_room = (TextView)this.findViewById(R.id.tv_classroom);
        tv_weeks = (TextView)this.findViewById(R.id.tv_week_num);

        tv_courseName.setText(str_courseName);
        tv_last.setText(str_last);
        tv_teacher.setText(str_teacher);
        tv_room.setText(str_room);
        tv_weeks.setText(str_weeks);

    }


    public void  initCdvClickListener(){
        cdv_course_board = (CardView) findViewById(R.id.cdv_course_board);
        cdv_course_comunication = (CardView) findViewById(R.id.cdv_course_comunication);
        cdv_course_sign = (CardView) findViewById(R.id.cdv_course_sign);

        //设置点击事件
        cdv_course_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //实现跳转
                Intent i = new Intent(SpecificCourseActivity.this,CourseBoardActivity.class);
                //存放要传输的数据
                Bundle bundle = new Bundle();
                //测试
                bundle.putString("tname", str_teacher); //传入字串
                bundle.putString("cid", str_cid); //传入字串
                i.putExtras(bundle);

                SpecificCourseActivity.this.startActivity(i);
            }
        });
        cdv_course_comunication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SpecificCourseActivity.this,"你点击的是课程交流",Toast.LENGTH_SHORT).show();
            }
        });
        cdv_course_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SpecificCourseActivity.this,"你点击的是快速签到",Toast.LENGTH_SHORT).show();

                sharedPreferences = getSharedPreferences("info",MODE_WORLD_READABLE);
                String name = sharedPreferences.getString("str_name","hello");
                String role = sharedPreferences.getString("str_role","学生");
                //String role="教师";

                if(role.equals("学生")){
                    //实现跳转
                    Intent i = new Intent(SpecificCourseActivity.this,CourseSignStudentActivity.class);
                    SpecificCourseActivity.this.startActivity(i);

                }else{
                    Intent intent = new Intent();
                    //存放要传输的数据
                    Bundle bundle = new Bundle();
                    //测试
                    //bundle.putString("tname", name); //传入字串
                    bundle.putString("tname", "葛亮"); //传入字串
                    bundle.putString("cid", str_cid); //传入字串

                    intent.putExtras(bundle);
                    intent.setClass(SpecificCourseActivity.this, CourseSignTeacherActivity.class);
                    startActivity(intent);

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


}

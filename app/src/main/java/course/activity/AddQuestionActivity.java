package course.activity;

import hello.login.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import api.ApiClient;
import common.UIHelper;
import widget.AppContext;
import widget.AppException;

/**
 * Created by happypaul on 16/1/23.
 */
public class AddQuestionActivity extends ActionBarActivity {


    //和 viewPager相关
    private ViewPager viewPager;//页卡内容
    private ImageView imageView;// 动画图片
    private TextView textView1,textView2;
    private List<View> views;// Tab页面列表
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;         // 动画图片宽度
    private View view1,view2;  //各个页卡
    private List<TextView> textviews;// 保存代表每个view的textview

    //和toolbar相关的东西
    private Toolbar mToolbar;
    //private Button btn_add_picture;
    private ImageView iv_add_question;
    private EditText et_question_title,et_question_detail;
    private String str_question_title,str_question_detail;


    //和intent相关的东西
    private Intent intent;
    private String str_cid,str_coursename,str_sid;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private AppContext appContext;


    //handler 的使用
    public android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                UIHelper.ToastMessage(AddQuestionActivity.this, "提问成功");

                //结束本activity
                finish();

                //跳转到具体课程的界面
                Intent intent = new Intent(AddQuestionActivity.this,SpecificCourseHelpActivity.class);
                intent.putExtra("courseId", str_cid);
                intent.putExtra("courseName", str_coursename);
                AddQuestionActivity.this.startActivity(intent);

            } else if (msg.what == 0) {

                UIHelper.ToastMessage(AddQuestionActivity.this, "系统出错");
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_question);

        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iv_add_question= (ImageView) mToolbar.findViewById(R.id.btn_add_answer);
        //btn_add_picture= (Button) mToolbar.findViewById(R.id.btn_add_picture);

        appContext = new AppContext(getApplication());

        getDataFromIntent();

        InitImageView();
        InitViewPager();


        //设置提交问题按钮
        iv_add_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取提问题的内容
                str_question_title = et_question_title.getText().toString();
                str_question_detail = et_question_detail.getText().toString();

                if(str_question_title.equals("")||str_question_title==null){
                    Toast.makeText(AddQuestionActivity.this,"请填写question详细描述",Toast.LENGTH_SHORT).show();
                }else if(str_question_detail.equals("")||str_question_detail==null){
                    Toast.makeText(AddQuestionActivity.this,"请填写question描述",Toast.LENGTH_SHORT).show();
                }else{

                    //问题title 和 问题 content都不为空的时候 才上传

                    //按钮不可点
                    iv_add_question.setEnabled(false);

                    //selectorDialog.show();
                    new Thread() {
                        public void run() {
                            Message msg = new Message();
                            try {
                                ApiClient.saveUserQuestion(appContext,str_sid,str_cid,str_question_detail,str_question_title);
                                msg.what=1;

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



    /*****************从intent中获取数据***********************/
    private void getDataFromIntent(){

        intent  = getIntent();
        str_cid = intent.getExtras().getString("cid");
        str_coursename = intent.getExtras().getString("course_name");

        //从sharedPreference中获取数据
        sharedPreferences = getSharedPreferences("info", MODE_WORLD_READABLE);
        str_sid = sharedPreferences.getString("str_sid","20134942");

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





    /**
     * 和 ViewPager相关的东西
     */
    private void InitViewPager() {
        viewPager=(ViewPager) findViewById(R.id.vPager);
        views=new ArrayList<View>();
        LayoutInflater inflater=getLayoutInflater();
        view1=inflater.inflate(R.layout.question_title, null);
        view2=inflater.inflate(R.layout.question_detail, null);
        views.add(view1);
        views.add(view2);

        et_question_title= (EditText) view1.findViewById(R.id.et_question_title);
        et_question_detail = (EditText) view2.findViewById(R.id.et_question_detail);

        InitTextView();

        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);
        textviews.get(0).setAlpha(1);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     *  初始化头标
     */

    private void InitTextView() {
        textView1 = (TextView) findViewById(R.id.tv_question_title);
        textView2 = (TextView) findViewById(R.id.tv_question_detail);
        textviews = new ArrayList<TextView>(); //初始化
        textviews.add(textView1);
        textviews.add(textView2);

        textView1.setOnClickListener(new MyOnClickListener(0));
        textView2.setOnClickListener(new MyOnClickListener(1));
    }


    /**
     *  初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据
     */

    private void InitImageView() {
        imageView= (ImageView) findViewById(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.cursor).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset =90;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        offset=200;
        imageView.setImageMatrix(matrix);// 设置动画初始位置
    }


    /**
     *
     * 头标点击监听 3 */
    private class MyOnClickListener implements View.OnClickListener {
        private int index=0;
        public MyOnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            textviews.get(index).setAlpha(1);
            textviews.get((index+1)%2).setAlpha(0.6f);
            viewPager.setCurrentItem(index);
        }

    }


    /**
     * ViewPagerDaapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)   {
            container.removeView(mListViews.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return  mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
    }

    //设计动画
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset;// 页卡1 -> 页卡2 偏移量
        public void onPageScrollStateChanged(int arg0) {


        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {


        }

        public void onPageSelected(int arg0) {



            //设置字体灰度值
            textviews.get(arg0).setAlpha(1);
            textviews.get((arg0+1)%2).setAlpha(0.6f);

            Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);//显然这个比较简洁，只有一行代码。
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            imageView.startAnimation(animation);
        }

    }
    //ViewPager相关的东西  结束
    //////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////

}

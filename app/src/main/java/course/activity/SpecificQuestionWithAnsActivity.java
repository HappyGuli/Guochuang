package course.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import api.ApiClient;
import api.URLs;
import bean.SimpleAnswerBean;
import common.BitmapManager;
import common.StringUtils;
import common.UIHelper;
import netdata.AnswerToSpecQuestionBean;
import netdata.AnswerToSpecQuestionBeanList;
import netdata.QuesitonInSpecificCourseBean;
import netdata.QuesitonInSpecificCourseBeanList;
import netdata.SpecificQuestionBean;
import widget.AppContext;
import widget.AppException;


/**
 * Created by happypaul on 16/1/24.
 */
public class SpecificQuestionWithAnsActivity extends ActionBarActivity {


    private Toolbar mToolbar;
    //和recyclerview 相关的东西
    private RecyclerView recyclerView;
    private boolean isRefresh = false;
    private ProgressDialog selectorDialog;


    private List<SimpleAnswerBean> datas;

    private List<AnswerToSpecQuestionBean> testDatas;

    private AppContext appContext;// 全局Context

    //对应问题的qid
    private int qid;
    private Intent intent;

    //用来保存 那个特定的问题对象的
    private SpecificQuestionBean specQuestion;
    //展示这个问题的主要内容
    private TextView tv_questiontitle;
    private TextView tv_questiondetail;


    //展示 当没有答案时的提示
    private CardView cardview_tips;


    //悬浮按钮
    private FloatingActionsMenu menu;

    //handler 的使用
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            selectorDialog.cancel();
            if (msg.what == 1) {

                tv_questiondetail.setText(specQuestion.getQcontent()+
                        "\n                      " +
                        "                 "
                        +specQuestion.getTime());
                tv_questiontitle.setText(specQuestion.getQtitle());

                testDatas = (List<AnswerToSpecQuestionBean>) msg.obj;

                if(testDatas.size()!=0){
                    cardview_tips.setVisibility(View.GONE);
                }else{
                    cardview_tips.setVisibility(View.VISIBLE);
                }
                // 根据获取到的数据进行 展示
                initListAdapterV();

                //数出来进行测试
                for(int i=0;i<testDatas.size();i++){
                    Log.e("TTTT", testDatas.get(i).getAnswerContent());
                }

            } else if (msg.what == -1) {
                tv_questiondetail.setText(specQuestion.getQcontent()+
                        "\n               "+specQuestion.getTime());
                tv_questiontitle.setText(specQuestion.getQtitle());
                cardview_tips.setVisibility(View.VISIBLE);

                Log.e("TTTT","what=-1");
                UIHelper.ToastMessage(SpecificQuestionWithAnsActivity.this, "没有数据");
            } else if (msg.what == -2) {
                Log.e("TTTT","what=-2");
                UIHelper.ToastMessage(SpecificQuestionWithAnsActivity.this, "xml解析错误");
            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.specific_question_with_answers);

        appContext = new AppContext(getApplication());

        //初始化 加载动画
        selectorDialog = ProgressDialog.show(this, null, "正在加载，请稍候...", true,false);


        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("问题");


        initButtonAndView();

        initDdata();

        //初始化 提示的内容不显示
        cardview_tips.setVisibility(View.GONE);


    }


    //设置 actionBar中的返回键的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //对返回键的监听
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(selectorDialog.isShowing()){
            selectorDialog.cancel();
        }

    }



    //设置按钮的功能
    public void initButtonAndView(){
        //设置按钮监听事件
        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.btn_answer_action_a);
        final FloatingActionButton actionB = (FloatingActionButton) findViewById(R.id.btn_answer_action_b);
        final FloatingActionButton actionC = (FloatingActionButton) findViewById(R.id.btn_answer_action_c);

        menu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.collapse();


                Intent intent = new Intent();
                intent.setClass(SpecificQuestionWithAnsActivity.this, AddAnswerActivity.class);   //描述起点和目标
                Bundle bundle = new Bundle();                           //创建Bundle对象
                bundle.putInt("qid", qid);     //装入数据
                intent.putExtras(bundle);                                //把Bundle塞入Intent里面
                startActivity(intent);


            }
        });

        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.collapse();
                Intent i = new Intent(SpecificQuestionWithAnsActivity.this, InviteOtherToAnswerActivity.class);
                SpecificQuestionWithAnsActivity.this.startActivity(i);

            }
        });


        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.collapse();
                Toast.makeText(SpecificQuestionWithAnsActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
            }
        });


        tv_questiondetail= (TextView) findViewById(R.id.tv_question_detail);
        tv_questiontitle= (TextView) findViewById(R.id.tv_question_title);

        //找到提示的部分
        cardview_tips = (CardView) findViewById(R.id.cardview_tips);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    //和recyclerView相关的东西开始

    //从网络端获取数据
    private void getDataFromServer() {

        //selectorDialog.show();
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {

                    //加载问题信息
                    String result = ApiClient.getQuestionDetailByqid(appContext, qid);
                    JSONObject obj = StringUtils.toJSONObject(result);//将json字符串转换为json对象

                    specQuestion = new SpecificQuestionBean();

                    specQuestion.setQcontent(obj.getString("qcontent"));
                    specQuestion.setQtitle(obj.getString("qtitle"));
                    specQuestion.setTime(obj.getString("time"));

                    //加载问题对应的答案信息
                    AnswerToSpecQuestionBeanList list = appContext.getAnswerList(qid, isRefresh);

                    if (list.getCount() > 0) {
                        msg.what = 1;
                        msg.obj = list.getList();
                        //appContext.saveObject(list, "answerslist_");
                    } else {
                        msg.what = -1;
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -2;
                    msg.obj = e;
                } catch (JSONException e){
                    msg.what = -2;
                    msg.obj = e;
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }


    private void initDdata() {

        //从上个activity中获取到数据
        intent = getIntent();
        if(intent!=null){

            Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
            qid = bundle.getInt("questionid", 100);

        }else{
            //给出提示
            Toast.makeText(SpecificQuestionWithAnsActivity.this,"intent is null",Toast.LENGTH_SHORT).show();
        }


        //从服务器获得数据
        getDataFromServer();
    }



    private void initListAdapterV() {

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_answers);
        //设置layoutManager
        recyclerView.setLayoutManager(new WrappingLinearLayoutManager(SpecificQuestionWithAnsActivity.this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        //设置adapter
        MyQuestionRecyclerViewAdapter adapter = new MyQuestionRecyclerViewAdapter(this, testDatas);
        recyclerView.setAdapter(adapter);
    }


    private class MyQuestionRecyclerViewAdapter extends RecyclerView.Adapter<MyQuestionHolder> {

        private Context context;
        private List<AnswerToSpecQuestionBean> datas;
        private BitmapManager bitmapManager;

        //构造函数
        public MyQuestionRecyclerViewAdapter(Context context, List<AnswerToSpecQuestionBean> datas) {
            super();
            this.context = context;
            this.datas = datas;
            this.bitmapManager = new BitmapManager(BitmapFactory.decodeResource(
                    context.getResources(), R.mipmap.ic_launcher));
        }

        //决定根布局
        @Override
        public MyQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.item_answer,null);
            return new MyQuestionHolder(view);

        }

        //决定根数据
        @Override
        public void onBindViewHolder(MyQuestionHolder holder, int position) {
            holder.SetDataAndRefreshUI(datas.get(position),bitmapManager,position);
        }

        @Override
        public int getItemCount() {

            return datas.size();
        }
    }

    //
    private class MyQuestionHolder extends RecyclerView.ViewHolder{

        private ImageView userImage;
        private TextView userName;
        private TextView zanNumber;
        private TextView ansContent;
        private TextView ansTag;

        public MyQuestionHolder(View itemView) {
            super(itemView);
            //初始化孩子对象
            userImage = (ImageView) itemView.findViewById(R.id.iv_answer_user);
            userName = (TextView)itemView.findViewById(R.id.tv_answer_user_name);
            zanNumber = (TextView)itemView.findViewById(R.id.tv_answer_thanked_number);
            ansContent = (TextView)itemView.findViewById(R.id.tv_answer_content);
            ansTag = (TextView)itemView.findViewById(R.id.tv_answer_tag);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //将悬浮按钮 菜单给关掉
                    menu.collapse();

                    Intent intent = new Intent();
                    intent.setClass(SpecificQuestionWithAnsActivity.this, SpecificAnswerActivity.class);   //描述起点和目标
                    Bundle bundle = new Bundle();                           //创建Bundle对象
                    bundle.putString("questiontitle", specQuestion.getQtitle());     //装入数据
                    bundle.putString("username", testDatas.get(getPosition()).getUserName());     //装入数据
                    bundle.putString("sid", testDatas.get(getPosition()).getSid());     //装入数据
                    bundle.putInt("ansid", testDatas.get(getPosition()).getAnsid());     //装入数据
                    bundle.putString("ansContent", testDatas.get(getPosition()).getAnswerContent());
                    bundle.putInt("zanNum",testDatas.get(getPosition()).getZanNum());
                    bundle.putInt("caiNum",testDatas.get(getPosition()).getCaiNum());
                    intent.putExtras(bundle);                                //把Bundle塞入Intent里面
                    startActivity(intent);



                }
            });



        }

        public void SetDataAndRefreshUI(AnswerToSpecQuestionBean bean, BitmapManager bitmapManager,int position){

            userName.setText(bean.getUserName());
            zanNumber.setText(String.valueOf(bean.getZanNum()));
            ansContent.setText(bean.getAnswerContent());
            //设置答案的标签
            if(position==0){
                Log.e("TTTT","最优答案"+position);
                ansTag.setText("最优答案");

            }else if(position == 1){
                Log.e("TTTT","备选答案"+position);
                ansTag.setText("备选答案");
            }else{
                //ansTag.setText(String.valueOf(position));
                ansTag.setVisibility(View.INVISIBLE);
            }
            //设置图片
            String imgURL = bean.getUserImgUrl();
            Log.e("TTTT",imgURL);

            if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {
                userImage.setImageResource(R.mipmap.ic_launcher);
            } else {
                if (!imgURL.contains("http")) {
                    imgURL = URLs.HTTP + URLs.HOST + "/" + imgURL;
                }
                Log.e("TTTT",imgURL);
                bitmapManager.loadBitmap(imgURL,userImage);
            }


        }

    }
    //和recyclerView相关的东西结束
    ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

}

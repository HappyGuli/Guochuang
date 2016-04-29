package course.activity;
import common.UIHelper;
import course.netdata.QuesitonInSpecificCourseBean;
import course.netdata.QuestionSearchedBean;
import hello.login.R;
import widget.AppContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.common.ui.listview.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by happypaul on 16/1/22.
 */
public class QuestionSearchActivity  extends ActionBarActivity {

    private Toolbar mToolbar;
    private EditText et_search;



    // 和这个展示数据相关
    private AppContext appContext ;
    private List<QuestionSearchedBean> list_qst;
    private String str_cid;  // 课程号
    private String str_key;
    private Intent intent ;


    //listview
    private ListView listview_searched;

    //handler 的使用
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 1) {


                UIHelper.ToastMessage(QuestionSearchActivity.this, "获取数据成功");

                //获取到服务器中的数据
                list_qst =(List) msg.obj;

                //在list_view中展示出来
                listview_searched.setAdapter(new MyAdapter(QuestionSearchActivity.this));


            } else if (msg.what == -1) {

                Log.e("TTTT","what=-1");
                UIHelper.ToastMessage(QuestionSearchActivity.this, "xml解析错误");
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.searche_question);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        //初始化 AppContext
        appContext = new AppContext(getApplication());


        //从intent 中获取数据
        intent = getIntent();
        str_cid = intent.getExtras().getString("cid");


        //获取到Listview 组件
        listview_searched = (ListView)this.findViewById(R.id.searched_questions);

        //通过这个获取到搜索的关键词
        et_search = (EditText)mToolbar.findViewById(R.id.et_search);

        //设置监听 当输入enter时获取文本文字
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND ||(event!=null&& event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
                {
                    if(v.getText().toString()!=""&&v.getText()!=null){


                        //Log.e("TTTT","keyword ="+v.getText().toString());


                        str_key = v.getText().toString();
                        //在一个新的线程中 从服务器中获取数据
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Message msg = new Message();

                                try{

                                    //从服务器中获取到和关键词相关的问题
                                    List<QuestionSearchedBean> list = AppContext.SearchInQuestions(appContext, str_cid, str_key);


                                    //Log.e("TTTT","length ="+list.size());

                                    msg.what = 1;
                                    msg.obj = list;

                                }catch (Exception e){
                                    msg.what = -1;

                                }

                                //通知handler
                                mHandler.sendMessage(msg);
                            }
                        }).start();

                    }else{
                        Toast.makeText(QuestionSearchActivity.this,"搜索的关键词不能为空",Toast.LENGTH_SHORT).show();

                    }
                    return true;

                }
                return false;
            }
        });


    }



    //ViewHolder静态类
    static class ViewHolder
    {
        public TextView tv_title;
        public TextView tv_zanNum;
        public TextView tv_ansCnt;
    }


    public class MyAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater = null;
        private MyAdapter(Context context)
        {
            //根据context上下文加载布局，这里的是Demo17Activity本身，即this
            this.mInflater = LayoutInflater.from(context);
            }
        @Override
        public int getCount() {
            //How many items are in the data set represented by this Adapter.
            //在此适配器中所代表的数据集中的条目数
            return list_qst.size();
            }
        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.
            //获取数据集中与指定索引对应的数据项
            return position;
            }
        @Override
        public long getItemId(int position) {
            //Get the row id associated with the specified position in the list.
            //获取在列表中与指定索引对应的行id
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //如果缓存convertView为空，则需要创建View
            if(convertView == null)
            {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.item_question_searched, null);
                holder.tv_ansCnt = (TextView)convertView.findViewById(R.id.searched_question_ansCnt);
                holder.tv_title = (TextView)convertView.findViewById(R.id.searched_question_title);
                holder.tv_zanNum = (TextView)convertView.findViewById(R.id.searched_question_zanNum);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            }else {

                holder = (ViewHolder)convertView.getTag();
            }


            holder.tv_ansCnt.setText("一共有"+list_qst.get(position).getAnsCnt()+"回答");
            holder.tv_title.setText(list_qst.get(position).getQuestioTitle());
            holder.tv_zanNum.setText("有"+list_qst.get(position).getZanNum()+"点赞");

            holder.tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setClass(QuestionSearchActivity.this, SpecificQuestionWithAnsActivity.class);   //描述起点和目标
                    Bundle bundle = new Bundle();                            //创建Bundle对象
                    bundle.putInt("questionid", list_qst.get(position).getQid());                 //装入数据
                    intent.putExtras(bundle);                                //把Bundle塞入Intent里面
                    startActivity(intent);

                }
            });

            return convertView;
        }

    }

        //Get a View that displays the data at the specified position in the data set.
        //获取一个在数据集中指定索引的视图来显示数据






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

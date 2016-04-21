package course.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import api.ApiClient;
import api.URLs;
import common.BitmapManager;
import common.StringUtils;
import common.UIHelper;
import netdata.CommentToSpecAnswerBean;
import netdata.CommentToSpecAnswerBeanList;
import netdata.QuesitonInSpecificCourseBeanList;
import widget.AppContext;
import widget.AppException;

/**
 * Created by happypaul on 16/1/24.
 */
public class AddAnswerCommentActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private EditText comment_content ;
    private ImageView iv_add_comment;


    //和展示评论信息相关
    private ListView listView;
    private List<CommentToSpecAnswerBean> commentsList;
    private AppContext appContext;// 全局Context
    private ProgressDialog selectorDialog;
    private boolean isRefresh = false;
    private MainListViewAdapter listViewAdapter;


    //从intent和 sharedPreference中获取数据
    private int ansid ;
    private Intent intent;
    private String str_sid;
    private SharedPreferences sharedPreferences;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.comment_answer);


        appContext = new AppContext(getApplication());
        //初始化 加载动画
        selectorDialog = ProgressDialog.show(this, null, "正在加载，请稍候...", true,false);


        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("评论问答信息");



        initView();

        getDataFromServer();

    }


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            selectorDialog.cancel();
            if (msg.what == 1) {
                commentsList = (List<CommentToSpecAnswerBean>) msg.obj;
                listViewAdapter = new MainListViewAdapter(AddAnswerCommentActivity.this,
                        commentsList);
                listView.setAdapter(listViewAdapter);
            } else if (msg.what == -1) {
                UIHelper.ToastMessage(AddAnswerCommentActivity.this, "没有数据");
            } else if (msg.what == -2) {
                UIHelper.ToastMessage(AddAnswerCommentActivity.this,
                        "XML解析失败");
            }
        }
    };


    //从intent中获取数据
    private void getDataFromIntentAndShaPre(){

        intent = getIntent();
        if(intent!=null){
            ansid = intent.getIntExtra("ansid",1);
        }

        sharedPreferences = getSharedPreferences("info",MODE_WORLD_READABLE);
        str_sid = sharedPreferences.getString("str_sid","20134942");




    }

    //初始化
    public void initView(){
        iv_add_comment = (ImageView) findViewById(R.id.iv_add_comment);
        comment_content = (EditText) findViewById(R.id.et_add_comment);

        //设置评论监听事件
        iv_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取系统的当前时间
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String comment_date = sdf.format(date);
                String comment_contnt= comment_content.getText().toString();
                String comment_userimgurl = "";
                String comment_username = "张三";

                CommentToSpecAnswerBean bean = new CommentToSpecAnswerBean();
                bean.setContent(comment_contnt);
                bean.setUserImgUrl(comment_userimgurl);
                bean.setDate(comment_date);
                bean.setUserName(comment_username);

                if(commentsList==null){
                    //如果commentList为空
                    commentsList = new ArrayList<CommentToSpecAnswerBean>();

                }else{
                    commentsList.add(bean);

                }

                //通知listview中对应的内容发生了改变
                listViewAdapter.notifyDataSetChanged();

                //强制隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //2.调用hideSoftInputFromWindow方法隐藏软键盘
                imm.hideSoftInputFromWindow(comment_content.getWindowToken(), 0);

                //清空editText中的内容
                comment_content.setText("");

                //发送数据到服务器端
                saveUserComment(comment_contnt,ansid,str_sid);

                Toast.makeText(AddAnswerCommentActivity.this,comment_contnt,Toast.LENGTH_SHORT).show();
            }
        });

        listView  = (ListView) this.findViewById(R.id.listview_comments);

    }


    //发送评论信息到服务器端
    private void saveUserComment(final String content, final int ansid, final String sid){

        new Thread() {
            public void run() {
                try{
                    ApiClient.saveUserComment(appContext, sid,ansid,content);
                }catch (Exception e){
                    //给出提示
                    Log.e("TTTT","评论失败");
                    //UIHelper.ToastMessage(AddAnswerCommentActivity.this,"评论失败");
                }
            }
        }.start();

    }


    //从网络端获取数据
    private void getDataFromServer() {

        selectorDialog.show();
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    CommentToSpecAnswerBeanList list = appContext.getCommentList(ansid,isRefresh);
                    if (list.getCount() > 0) {
                        msg.what = 1;
                        msg.obj = list.getList();
                        appContext.saveObject(list, "commentslist_");
                    } else {
                        msg.what = -1;
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -2;
                    msg.obj = e;
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    ///和ListView 相关的东西
    public class MainListViewAdapter extends BaseAdapter {
        LayoutInflater inflater;
        // 定义Context
        private Context mContext;
        private ViewHolder holder;
        private int clickTemp = -1;
        List<CommentToSpecAnswerBean> list = new ArrayList<CommentToSpecAnswerBean>();
        private BitmapManager bmpManager;

        public MainListViewAdapter(Activity activity, List<CommentToSpecAnswerBean> listViewList) {
            inflater = activity.getLayoutInflater();
            list = listViewList;
            this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
                    activity.getResources(), R.mipmap.ic_launcher));
        }



        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public void setSeclection(int position) {
            clickTemp = position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_comment, null);
                holder.comment_date = (TextView) convertView
                        .findViewById(R.id.tv_comment_date);
                holder.comment_username = (TextView) convertView
                        .findViewById(R.id.tv_comment_user_name);
                holder.comtent_content = (TextView) convertView
                        .findViewById(R.id.tv_comment_content);
                holder.comment_user_img = (ImageView) convertView
                        .findViewById(R.id.iv_comment_user_img);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.comment_username.setText(list.get(position).getUserName());
            holder.comment_date.setText(list.get(position).getDate());
            holder.comtent_content.setText(list.get(position).getContent());
            Log.e("TTTT", list.get(position).getContent());

            //holder.comment_user_img.setImageResource(R.mipmap.ic_launcher);

            String imgURL = list.get(position).getUserImgUrl();
            if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {
                holder.comment_user_img.setImageResource(R.mipmap.ic_launcher);
            } else {
                if (!imgURL.contains("http")) {
                    imgURL = URLs.HTTP + URLs.HOST + "/" + imgURL;
                    Log.e("TTTT",imgURL);
                }
                bmpManager.loadBitmap(imgURL, holder.comment_user_img);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView comtent_content;
            TextView comment_date;
            TextView comment_username;

            ImageView comment_user_img;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

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

package course.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import api.ApiClient;
import bean.InviteOtherBean;
import common.UIHelper;
import netdata.StudentInfoInClassBean;
import netdata.StudentInfoInClassBeanList;
import util.WifiHotUtil;
import widget.AppContext;
import widget.AppException;

/**
 * Created by happypaul on 16/3/9.
 */

public class CourseSignTeacherActivity extends ActionBarActivity {

    private ListView ll_invite;
    private MyAdapter ll_adapter;
    private List<StudentInfoInClassBean> datas;
    private List<String> phonesIds;

    private Button btn_confirm;
    private Toolbar mToolbar;

    private AppContext context;

    //和intent 相关的东西
    private String str_cid,str_tname;
    private Intent intent;

    //
    private WifiHotUtil wifiHotUtil;
    private WifiManager wifiManager;


    //handler 的使用
    public android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 1) {

                datas = (List<StudentInfoInClassBean>)msg.obj;
                initView();

            } else if (msg.what == 0) {

                UIHelper.ToastMessage(CourseSignTeacherActivity.this, "服务器出错，获取人员信息出错");
            } else if (msg.what == -1){

                //有人签到成功
                ll_adapter.notifyDataSetChanged();

            } else if(msg.what == -2){
                UIHelper.ToastMessage(CourseSignTeacherActivity.this, "线程出错");

            }
        }
    };

    //监听 连接本机的设备的变化
    private Thread thread ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course_sign_teacher);



        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("快速签到");

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        final boolean isWifiEnabled = wifiManager.isWifiEnabled();


        phonesIds = new ArrayList<String>();
        context = new AppContext(getApplication());
        wifiHotUtil = new WifiHotUtil(getApplication());


        //从intent中获取数据
        getDataFromIntent();

        //从服务器中获取学生数据
        initData();

        //初始化监听的thread
        initThread();



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


    //从intent中获取数据
    private void getDataFromIntent(){

        intent = getIntent();
        if(intent!=null){
            str_cid = intent.getExtras().getString("cid");
            str_tname = intent.getExtras().getString("tname");
        }

    }

    //初始化数据
    public void initData(){
        //selectorDialog.show();
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {

                    Log.e("TTTT","name="+str_tname);
                    Log.e("TTTT","cid="+str_cid);
                    StudentInfoInClassBeanList list = context.getStudentsInfo( str_cid, str_tname);
                    msg.what=1;
                    msg.obj = list.getList();

                } catch (AppException e){

                    e.printStackTrace();
                    msg.what=0;
                }
                mHandler.sendMessage(msg);
            }
        }.start();

    }

    //初始化组件
    public void  initView(){

        btn_confirm = (Button) findViewById(R.id.btn_invite_confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = btn_confirm.getText().toString();

                if(text.equals("开始签到")){
                    Toast.makeText(CourseSignTeacherActivity.this,"开始签到",Toast.LENGTH_SHORT).show();
                    btn_confirm.setText("结束签到");

                    boolean isWifiEnabled  = wifiManager.isWifiEnabled();

                    //开启热点 开始签到
                    setWifiApEnabled(isWifiEnabled, true, wifiManager, str_cid, str_tname);

                    //监听ap的变化
                    listenAP(true);

                }else{

                    btn_confirm.setText("开始签到");
                    Toast.makeText(CourseSignTeacherActivity.this,"签到结束",Toast.LENGTH_SHORT).show();

                    boolean isWifiEnabled  = wifiManager.isWifiEnabled();
                    //关闭热点 结束签到
                    setWifiApEnabled(isWifiEnabled, false, wifiManager, str_cid,str_tname);

                }


            }
        });


        //给listView 设置adapter
        ll_invite = (ListView) findViewById(R.id.lv_invite_other_to_answer);
        ll_adapter=new MyAdapter(CourseSignTeacherActivity.this);
        ll_invite.setAdapter(ll_adapter);

    }


    private  void initThread(){
        thread = new Thread() {
            public void run() {

                while(true){

                    Message msg = new Message();
                    try {
                        //休息三秒
                        Thread.sleep(5000);

                        // 得到现在连接到手机热点的mac地址
                        phonesIds = wifiHotUtil.getConnectedIP();

                        for(int i = 0;i<phonesIds.size();i++){
                            int pos = isPhoneidExist(phonesIds.get(i));

                            //在界面上面确认
                            if(pos!=-1){
                                datas.get(pos).setChecked(true);
                            }
                        }
                        msg.what=-1;


                    } catch (Exception e){
                        e.printStackTrace();
                        msg.what=-2;
                    }
                    mHandler.sendMessage(msg);
                }

            }
        };

    }



    //监听 热点是否被连接

    /**
     * 是否启动 还是 关闭 监听线程
     */
    public  void  listenAP(boolean enable) {
        //如果是enable 开启线程
        if(enable) {

            if(thread==null){
                initThread();
            }
            //开启线程
            thread.start();

        }else{

            if(thread==null){
                initThread();
            }
            //关闭线程
            thread.stop();

        }
    }



    //ViewHolder静态类
    static class ViewHolder
    {
        public ImageView userIcon;
        public TextView userName;
        public CheckBox isSelected;
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
            return datas.size();
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

        //Get a View that displays the data at the specified position in the data set.
        //获取一个在数据集中指定索引的视图来显示数据
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //如果缓存convertView为空，则需要创建View
            if(convertView == null)
            {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.item_invite_other_to_answer, null);
                holder.userIcon = (ImageView)convertView.findViewById(R.id.item_invite_usericon);
                holder.userName = (TextView)convertView.findViewById(R.id.tv_item_invite_other_username);
                holder.isSelected = (CheckBox)convertView.findViewById(R.id.cb_invite_other);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            }else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.userIcon.setImageResource(R.mipmap.usericon1);
            holder.userName.setText((String) datas.get(position).getSname());

            if(datas.get(position).getChecked()){
                holder.isSelected.setChecked(true);
                Toast.makeText(CourseSignTeacherActivity.this,datas.get(position).getSname()+"签到成功",Toast.LENGTH_SHORT).show();

            }


            return convertView;
        }

    }





    /**
     * 返回的是学生在list中的位置
     * 如果不存在 则返回－1
     * @param phoneid
     * @return
     */
    private int isPhoneidExist(String phoneid){

        for(int i=0;i<datas.size(); i++){

            StudentInfoInClassBean bean = datas.get(i);
            //如果存在
            if(phoneid.equals(bean.getPhoneid())){
                return i;
            }
        }
        return -1;
    }







    /**
     * 用来开启手机热点的  手机热点的名字:课程号_教师名字 ,热点的密码:课程号
     * @param iswifienabled
     * @param wifiManager
     * @param str_cid
     * @param str_tname
     * @return
     */
    public boolean setWifiApEnabled(boolean iswifienabled,boolean enableAp,WifiManager wifiManager,String str_cid,String str_tname ) {
        if (iswifienabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }

        try {

            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();


            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = str_cid+"_"+str_tname;

            Log.e("TTTT", str_cid + "_" + str_tname);

            //配置热点的密码
            apConfig.preSharedKey=str_cid;

            //通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enableAp);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


}

package course.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bean.Course;
import bean.WifiBean;
import util.ComparatorWifi;
import util.GPRSUnit;
import util.WifiUtil;

/**
 * Created by happypaul on 16/1/23.
 */
public class CourseSignStudentActivity extends ActionBarActivity {


    private Toolbar mToolbar;

    private WifiInfo currentWifiInfo;// 当前所连接的wifi

    private String isSelectSSid, deviceMac, type;// 当前选中的热点 ssid mac
    private boolean isRead;
    private int passType;  //加密类型


    //展示 现在wifi状态
    private TextView wifi_result_textview;

    MyAdapter adapter;
    private ConnectivityManager mCM;  //判断wifi是否连接
    public static final int WIFI_CHECK = 1;
    List<ScanResult> scanList = new ArrayList<ScanResult>();  // wifi扫描结果
    private ProgressDialog progressDialog;


    //展示 wifi结果
    private ListView lv;
    private boolean isIntent = false;


    //线程当中使用
    private boolean isNoGetPw = false;
    private boolean isError = false;



    private Handler mHandler;
    private WifiUtil wifiUtil;
    private GPRSUnit gprsUnit;
    private CheckBox isOenWfi;
    private LinearLayout wifi_list;
    final String connectionMean[] = new String[] { "detail", "forget", "disconnect", "cancel" };// 连接对象菜单
    final String isSave[] = new String[] { "connect", "forget", "cancel" };//





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course_sign_student);

        //设置toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //btn_add_question.setEnabled(false);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("快速签到");


        //初始化wifi
        init();
        addOnclick();

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







    /***************WIFI  Adapter******************/
    public class MyAdapter extends BaseAdapter {
        LayoutInflater inflater;
        List<WifiBean> alist;

        public MyAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            alist = new ArrayList<WifiBean>();
        }

        public int getCount() {
            return alist.size();
        }

        public void removeAll() {
            alist.clear();
            this.notifyDataSetChanged();
        }

        public void addItem(WifiBean WifiBean) {
            alist.add(WifiBean);
        }

        public Object getItem(int position) {
            return position;
        }

        public WifiBean getItemById(int index) {
            return alist.get(index);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ComparatorWifi compar = new ComparatorWifi();
            Collections.sort(alist, compar);

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_wifi_list, null);
                holder = new ViewHolder();
                holder.textView1 = (TextView) convertView.findViewById(R.id.companion_wifi_ssid);
                holder.mIcon = (ImageView) convertView.findViewById(R.id.imageView);
                holder.mIocnLock = (ImageView) convertView.findViewById(R.id.imageView2);
                holder.textView2 = (TextView) convertView.findViewById(R.id.wifi_mess);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            WifiBean WifiBean = alist.get(position);
            String mStringBuffer = "";
            if (WifiBean.isSelect()) {
                if(WifiBean.isIntent()){
                    mStringBuffer = "(已连接,可以上网)";
                }else {
                    mStringBuffer = "(已连接,不可以上网)";
                }

            } else if (WifiBean.isRead()) {
                mStringBuffer = "(已记住)";
            }
            holder.textView1.setText(WifiBean.getSsid());
            holder.textView2.setText( mStringBuffer);
            if(getType(WifiBean.getPassType()) > 1){
                holder.mIcon.setVisibility(View.GONE);
                holder.mIocnLock.setVisibility(View.VISIBLE);
                holder.mIocnLock.setImageLevel(Math.abs(WifiBean.getLeave()));
            }else{
                holder.mIcon.setVisibility(View.VISIBLE);
                holder.mIocnLock.setVisibility(View.GONE);
                holder.mIcon.setImageLevel(Math.abs(WifiBean.getLeave()));
            }




            return convertView;
        }

        class ViewHolder {
            TextView textView1;
            TextView textView2;
            ImageView mIcon;
            ImageView mIocnLock;
        }

        /** 判断加密类型 */
        public int getType(String type) {
            int tem = -1;
            if (type.indexOf("WPA") > 0) {
                tem = WifiUtil.WIFICIPHER_WPA;
            } else if (type.indexOf("WEP") > 0) {
                tem = WifiUtil.WIFICIPHER_WEP;
            } else {
                tem = WifiUtil.WIFICIPHER_NOPASS;
            }
            return tem;
        }

    }


    public void init() {
        /** gprs 工具 */
        mCM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        gprsUnit = new GPRSUnit(mCM);

        lv = (ListView) findViewById(R.id.wifiItemListView);

        wifi_result_textview = (TextView) findViewById(R.id.wifi_result_textview);

        wifi_list = (LinearLayout) findViewById(R.id.wifi_list);// wifi列表

        isOenWfi = (CheckBox) findViewById(R.id.cb_open_wifi);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WIFI_CHECK:
                        if (scanList != null) {
                            scanList.clear();
                        }
                        scanList = (ArrayList<ScanResult>) msg.obj;
                        addDateForListView();
                    default:
                        break;
                }
            }
        };

        /** wifi 工具类 */
        wifiUtil = new WifiUtil(this, mHandler);
        showTitle();
    }

    public void showTitle() {
        /** 添加wifi状态广播 */
        WifiBroad wifiReceiver = new WifiBroad();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.registerReceiver(wifiReceiver, filter);

        /** 当前连接的wifi */
        currentWifiInfo = wifiUtil.getWifiManager().getConnectionInfo();
        if (!wifiUtil.checkWIFIConnection(this)) {
            wifi_result_textview.setText("当前没有连接网络");
        } else {
            wifi_result_textview.setText("当前网络：" + currentWifiInfo.getSSID());
        }

    }

    public void addOnclick() {

        isOenWfi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOenWfi.isChecked()) {
                    wifiUtil.setFalg(false);
                    wifiUtil.closeWifi();

                } else {
                    wifiUtil.openWifi();
                }
            }
        });

        // 点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // 加密类型
                type = adapter.getItemById(arg2).getPassType();
                isSelectSSid = adapter.getItemById(arg2).getSsid();
                deviceMac = adapter.getItemById(arg2).getDeviceMac();
                isRead = adapter.getItemById(arg2).isRead();

                getType(type);

                if (adapter.getItemById(arg2).isSelect() && isRead) {// 连接
                    createNowConnection();
                } else if (isRead && (!adapter.getItemById(arg2).isSelect())) {// 记住
                    createIsSaveNoconnceion();
                } else {
                    /** 加密方式 */
                    Log.i("", "*********getPassType()***" + adapter.getItemById(arg2).getPassType());
                    if (adapter.getItemById(arg2).getPassType() != null && adapter.getItemById(arg2).getPassType().length() > 0 && !adapter.getItemById(arg2).getPassType().equals("[ESS]")) {
                        createPWDialog(adapter.getItemById(arg2).getPassType(), adapter.getItemById(arg2).getLeave());
                    } else {
                        new ConnectWifiThread().execute("");
                    }
                }
            }
        });

        // 添加长按事件
        lv.setOnCreateContextMenuListener(this);
        adapter = new MyAdapter(this);
        lv.setAdapter(adapter);
    }




    /** wifi状态广播 */
    class WifiBroad extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {

            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                // WIFI开关
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {// 如果关闭
                    wifi_result_textview.setText("请开启WIFI，连接老师热点");
                    isOenWfi.setChecked(false);
                    wifiUtil.setFalg(false);
                    wifi_list.setVisibility(View.INVISIBLE);
                }else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                    isOenWfi.setChecked(true);
                    wifi_list.setVisibility(View.VISIBLE);
                    Log.i("", "******开启 wifi**************" );
                    wifi_result_textview.setText("WIFI开启，请连接老师热点");
                    wifiUtil = new WifiUtil(CourseSignStudentActivity.this, mHandler);
                    wifiUtil.setFalg(true);
                    wifiUtil.start();

                }else if(wifistate == WifiManager.WIFI_STATE_DISABLING){
                    wifi_result_textview.setText("WIFI正在关闭...");
                }else if(wifistate == WifiManager.WIFI_STATE_ENABLING)
                    wifi_result_textview.setText("WIFI正在开启...");
            }else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {// 如果断开连接
                    wifi_result_textview.setText("wifi连接已断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
                    wifi_result_textview.setText("正在获取ip...");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    currentWifiInfo = wifiUtil.getWifiManager().getConnectionInfo();

                   // wifi_result_textview.setText("连接到网络 " + currentWifiInfo.getSSID());
                    if(currentWifiInfo.getSSID().contains("老师")){
                        wifi_result_textview.setText("你已签到成功" );
                    }else{
                        wifi_result_textview.setText("连接到网络 " + currentWifiInfo.getSSID());
                    }
                    //张谷力修改
                    // handler.sendEmptyMessage(4);
                }
            }
        }
    }


    //给 listView添加数据
    public void addDateForListView() {
        adapter.removeAll();
        currentWifiInfo = wifiUtil.getWifiManager().getConnectionInfo();
        if (scanList != null) {
            for (final ScanResult scanResult : scanList) {
                if (scanResult.SSID == null || scanResult.SSID.equals("")) {
                    return;
                }
                WifiBean wifiBean = new WifiBean();
                wifiBean.setLeave(scanResult.level);
                wifiBean.setPassType(scanResult.capabilities);
                wifiBean.setSsid(scanResult.SSID);
                wifiBean.setDeviceMac(scanResult.BSSID);

                if (wifiUtil.isExsits(scanResult.SSID) != null) {
                    wifiBean.setRead(true);
                    wifiBean.setFalg("2");
                } else {
                    wifiBean.setFalg("3");
                }
                if (wifiUtil.checkWIFIConnection(this) && currentWifiInfo.getBSSID() != null && currentWifiInfo.getBSSID().equals(scanResult.BSSID)) {
                    wifiBean.setIntent(isIntent);
                    wifiBean.setSelect(true);
                    wifiBean.setFalg("1");
                }
                adapter.addItem(wifiBean);
            }
        }
        adapter.notifyDataSetChanged();
    }


    /** 判断加密类型 */
    public void getType(String type) {
        if (type.indexOf("WPA") > 0) {
            passType = WifiUtil.WIFICIPHER_WPA;
        } else if (type.indexOf("WEP") > 0) {
            passType = WifiUtil.WIFICIPHER_WEP;
        } else {
            passType = WifiUtil.WIFICIPHER_NOPASS;
        }
    }



    /** 当前连接的ｗｉｆｉ */
    private void createNowConnection() {
        new AlertDialog.Builder(CourseSignStudentActivity.this).setTitle(isSelectSSid).setIcon(null).setItems(connectionMean, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        isConnceionShow();
                        break;
                    case 1:
                        wifiUtil.forGetPw(isSelectSSid);
                        break;
                    case 2:
                        wifiUtil.breakWifi(isSelectSSid);
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    /** 保存没有连接 */
    private void createIsSaveNoconnceion() {
        new AlertDialog.Builder(CourseSignStudentActivity.this).setTitle(isSelectSSid).setIcon(null).setItems(isSave, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (isRead)
                            wifiUtil.connectConfiguration(isSelectSSid);

                        break;
                    case 1:
                        wifiUtil.forGetPw(isSelectSSid);
                        break;
                    default:

                        break;
                }
            }
        }).show();
    }

    /** 输入密码框 */
    public void createPWDialog(String pwType, int leave) {
        LayoutInflater factory = LayoutInflater.from(CourseSignStudentActivity.this);
        final View textEntryView = factory.inflate(R.layout.wifi_pw_dialog_layout, null);
        TextView passType = (TextView) textEntryView.findViewById(R.id.aqx);
        TextView leaveTem = (TextView) textEntryView.findViewById(R.id.xhqd);
        final CheckBox mCheckBoxView = (CheckBox) textEntryView.findViewById(R.id.checkBox1);
        final EditText mEditTextView = (EditText) textEntryView.findViewById(R.id.wifi_pw);
        passType.setText(pwType);
        int lea = Math.abs(leave);
        /** 信号强度 */
        leaveTem.setText(getLeaveType(lea));
        AlertDialog dlg = new AlertDialog.Builder(CourseSignStudentActivity.this).setTitle(isSelectSSid).setView(textEntryView).setPositiveButton("保存", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pwString = mEditTextView.getText().toString().trim();
                if (pwString != null && pwString.length() > 0) {
                    new ConnectWifiThread().execute(pwString);
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).create();

        mCheckBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCheckBoxView.isChecked()) {
                    // 文本正常显示
                    mEditTextView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable = mEditTextView.getText();
                    Selection.setSelection(etable, etable.length());
                } else {
                    // 文本以密码形式显示
                    mEditTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    // 下面两行代码实现: 输入框光标一直在输入文本后面
                    Editable etable = mEditTextView.getText();
                    Selection.setSelection(etable, etable.length());
                }
            }
        });
        dlg.show();
    }

    /** 已经连接的热点详情 */
    public void isConnceionShow() {
        LayoutInflater factory = LayoutInflater.from(CourseSignStudentActivity.this);
        final View textEntryView = factory.inflate(R.layout.wifi_show_connecion_dialog_layout, null);
        TextView passType = (TextView) textEntryView.findViewById(R.id.aqx);
        TextView leaveTem = (TextView) textEntryView.findViewById(R.id.xhqd);
        TextView speed = (TextView) textEntryView.findViewById(R.id.wifi_speed);
        TextView ip = (TextView) textEntryView.findViewById(R.id.wifi_ip_address);
        passType.setText(type);
        int lea = Math.abs(currentWifiInfo.getRssi());
        /** 信号强度 */
        leaveTem.setText(getLeaveType(lea));
        speed.setText(currentWifiInfo.getLinkSpeed() + "Mbps");
        int Ip = currentWifiInfo.getIpAddress();
        String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
        ip.setText(strIp);
        AlertDialog dlg = new AlertDialog.Builder(CourseSignStudentActivity.this).setTitle(isSelectSSid).setView(textEntryView).setPositiveButton("忘记", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                wifiUtil.forGetPw(currentWifiInfo.getSSID());
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).create();
        dlg.show();
    }


    /**
     * 扫描连接的wifi 是否成功！
     */

    class RefreshSsidThread extends Thread {
        int tem = 0;

        @Override
        public void run() {
            super.run();
            while (true) {
                currentWifiInfo = wifiUtil.getWifiManager().getConnectionInfo();
                Log.i("", "*******正在获取ip..........**************");
                if (null != currentWifiInfo.getSSID() && 0 != currentWifiInfo.getIpAddress()) {
                    Log.i("", "*******获取ip成功**************");
                    handler.sendEmptyMessage(4);
                    break;
                }
                if (tem == 20) {
                    Log.i("", "*******自动连接失败**************");
                    handler.sendEmptyMessage(1);
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tem++;
            }
        }
    }


    /**
     * 连接wifi
     */
    class ConnectWifiThread extends AsyncTask<String, Integer, String> {
        /**
         * 在onPreExecute()完成后立即执行，用于执行较为费时的操作， 此方法将接收输入参数和返回计算结果。
         * 在执行过程中可以调用publishProgress(Progress... values)来更新进度信息
         */
        @Override
        protected String doInBackground(String... params) {

            // 连接配置好指定ID的网络
            WifiConfiguration config = wifiUtil.createWifiInfo(isSelectSSid, params[0], passType);
            int networkId = wifiUtil.getWifiManager().addNetwork(config);
            Log.i("", "***********连接的热点名*********" + isSelectSSid + " *****tyep:" + passType + "***networkId" + networkId);
            if (params[0].equals("")) {
                wifiUtil.getWifiManager().enableNetwork(networkId, true);
                return isSelectSSid;
            } else {
                if (null != config && networkId != -1) {
                    wifiUtil.getWifiManager().enableNetwork(networkId, true);
                    return isSelectSSid;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (null != progressDialog) {
                progressDialog.dismiss();
            }
            if (null != result) {
                handler.sendEmptyMessage(0);
            }else {
                handler.sendEmptyMessage(1);
            }
            super.onPostExecute(result);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    new RefreshSsidThread().start();
                    break;
                case 1:
                    /** 自动连接错误 */
                    if (isNoGetPw) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isError = true;
                        wifiUtil = new WifiUtil(CourseSignStudentActivity.this, mHandler);
                        wifiUtil.setFalg(true);
                        wifiUtil.start();
                    }
                    Toast.makeText(CourseSignStudentActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if (isNoGetPw) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wifiUtil = new WifiUtil(CourseSignStudentActivity.this, mHandler);
                        wifiUtil.setFalg(true);
                        wifiUtil.start();
                    }


                    break;

            }
            super.handleMessage(msg);
        }
    };

    /** 显示信号强度 */
    private String getLeaveType(int lea) {
        String result = "";
        if (lea < 50) {
            result = "棒极了";
        } else if (lea > 50 && lea < 60) {
            result = "较强";
        } else if (lea > 60 && lea < 70) {
            result = "一般";
        } else {
            result = "较弱";
        }
        return result;
    }
}

package course.activity;
import hello.login.R;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by happypaul on 16/1/23.
 */
public class CourseSignActivity extends ActionBarActivity {


    private Toolbar mToolbar;

    private Button back_btn;
    private WifiManager wifiManager;
    List<ScanResult> list;
    private ListView recyclerVIew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.course_sign);

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

    }



    private void init() {
        wifiManager = (WifiManager) getSystemService(CourseSignActivity.this.WIFI_SERVICE);
        openWifi();
        list = wifiManager.getScanResults();
        recyclerVIew = (ListView) findViewById(R.id.recycler_course_sign);
        if (list == null) {
            Toast.makeText(this, "wifi未打开！", Toast.LENGTH_LONG).show();
        }else {
            recyclerVIew.setAdapter(new MyAdapter(CourseSignActivity.this ,list));
        }

    }

    /**
     *  打开WIFI
     */
    private void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

    }

    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<ScanResult> list;
        public MyAdapter(Context context, List<ScanResult> list) {
            // TODO Auto-generated constructor stub
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = null;
            view = inflater.inflate(R.layout.item_course_sign, null);
            ScanResult scanResult = list.get(position);
            TextView tv_wifiName = (TextView) view.findViewById(R.id.tv_wifi_name);
            tv_wifiName.setText(scanResult.SSID);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_wife_icon);
            //判断信号强度，显示对应的指示图标
            if (Math.abs(scanResult.level) > 100) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_icon));
            } else if (Math.abs(scanResult.level) > 80) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_icon));
            } else if (Math.abs(scanResult.level) > 70) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_icon));
            } else if (Math.abs(scanResult.level) > 60) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_icon));
            } else if (Math.abs(scanResult.level) > 50) {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_icon));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.wifi_icon));
            }
            return view;
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

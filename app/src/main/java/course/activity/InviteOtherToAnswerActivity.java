package course.activity;

import hello.login.R;
import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import course.bean.InviteOtherBean;

/**
 * Created by happypaul on 16/1/25.
 */
public class InviteOtherToAnswerActivity extends ActionBarActivity {

    private ListView ll_invite;
    private List<InviteOtherBean> datas;
    private List<String> selectedStudents;
    private Button btn_confirm;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.invite_other_to_answer_question);


        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //Set a Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("邀请回答");


        initData();
        initView();

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


    //初始化数据
    public void initData(){
        selectedStudents = new ArrayList<String>();
        datas = new ArrayList<InviteOtherBean>();

        //for(int i=0;i<12;i++){

            InviteOtherBean tem = new InviteOtherBean();
            tem.setUserIcon(R.mipmap.usericon1);
            tem.setUserName("刘林生");
            tem.setUserId("2013499" );
            datas.add(tem);

            InviteOtherBean tem1= new InviteOtherBean();
            tem1.setUserIcon(R.mipmap.usericon1);
            tem1.setUserName("马文攀");
            tem1.setUserId("2013450");
            datas.add(tem1);

            InviteOtherBean tem2 = new InviteOtherBean();
            tem2.setUserIcon(R.mipmap.usericon1);
            tem2.setUserName("阿不都");
            tem2.setUserId("2013452" );
            datas.add(tem2);


        //}
    }

    //初始化组件
    public void  initView(){

        btn_confirm = (Button) findViewById(R.id.btn_invite_confirm);


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InviteOtherToAnswerActivity.this,"hello",Toast.LENGTH_SHORT).show();

                for (int i=0;i<selectedStudents.size();i++){
                    String student=selectedStudents.get(i);
                    Log.e("TTTT",student);
                }

                //finish();

            }
        });

        ll_invite = (ListView) findViewById(R.id.lv_invite_other_to_answer);

        ll_invite.setAdapter(new MyAdapter(InviteOtherToAnswerActivity.this));

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
            holder.userIcon.setImageResource((Integer) datas.get(position).getUserIcon());
            holder.userName.setText((String)datas.get(position).getUserName());

            //设置监听
            ((CheckBox)holder.isSelected).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //选择 或 不选择
                    //邀请同学
                    if (((CheckBox)v).isChecked()) {
                        Toast.makeText(InviteOtherToAnswerActivity.this, "selected", Toast.LENGTH_SHORT).show();
                        selectedStudents.add(datas.get(position).getUserId());
                    }
                    if (!((CheckBox)v).isChecked()) {
                        Toast.makeText(InviteOtherToAnswerActivity.this, "not selected", Toast.LENGTH_SHORT).show();
                        if (selectedStudents.contains(datas.get(position).getUserId())) {
                            selectedStudents.remove(datas.get(position).getUserId());
                        }
                    }
                }
            });

            return convertView;
        }

    }
}
